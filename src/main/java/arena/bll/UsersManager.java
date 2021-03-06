package arena.bll;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import arena.bll.Encryptor;
import arena.bll.Users;
import arena.dal.DBManager;
import arena.servlets.Authentication;
import org.json.simple.JSONObject;


public final class UsersManager {

    //----------------------------------------Boolean----------------------------------------

    public static boolean beforeInsertUser(org.json.JSONObject bodyParams, HttpServletResponse response) throws IOException {
        /*
         * This function get the request and the response,
         * first it's validating that all field are not null or blank.
         * after it validate the email format, and check the Database..
         * if the email if valid and there are no user with this email it will create a User object
         * and pass it as an argument to the insertUser function with his password.
         */
        Map<String, String> jsonMapResponse = new HashMap<>();
        Map<String, String> values = Authentication.checkParameters(bodyParams);

        // Email validation
        assert values != null;
        if (values.containsValue(null) || values.containsValue("null")) {
            response.setStatus(400);
            jsonMapResponse.put("Error", "Something is missing");
            response.getWriter().append(new JSONObject(jsonMapResponse).toJSONString());
            return false;
        }
        String mailTxt = values.get("email").toLowerCase();
        if (emailValidation(mailTxt)) {
            String query = String.format("Select email from users where email = '%s';", mailTxt);
            if (DBManager.isExists(query) == 0)
                return insertUser(new Users(mailTxt, values.get("firstName"), values.get("lastName"), values.get("phoneNumber"),
                        Integer.parseInt(values.get("age")), values.get("gender"), values.get("interestedIn"), Integer.parseInt(values.get("score"))), values.get("password"));
            else if (DBManager.isExists(query) == 1) {
                response.setStatus(400);
                jsonMapResponse.put("Error", "User already exists");
                response.getWriter().append(new JSONObject(jsonMapResponse).toJSONString());
            }
        }
        return false;
    }

    private static boolean insertUser(Users user, String pass) {
        /*
         * !!!	PLEASE DONT USE THIS FUNCTION WITH OUT CALL THE BEFORE FUNCTION (beforeInsertUser)	!!!
         * This method is get a User object and password,
         * first it encrypt the password,
         * second it create a new user in the database, and get the new userId
         * third it insert the hashed password to the database.
         */
        String p = Encryptor.encryptThisString(pass);
        String query = String.format(
                "INSERT INTO users(email, firstName ,lastName ,phoneNumber ,age ,gender ,interestedIn, score) "
                        + "VALUES ('%s','%s','%s',%s,'%d','%s','%s','%d');",
                user.getEmail(), user.getFirstName().toLowerCase(), user.getLastName().toLowerCase(), user.getPhoneNumber(), user.getAge(),
                user.getGender().toLowerCase(), user.getInterestedIn().toLowerCase(), user.getScore());

        String getNewUserId = String.format("select id from users where users.email = '%s';", user.getEmail());


        if (DBManager.runExecute(query) > 0) {
            DBManager.runSelect(getNewUserId, (res) -> {
                try {
                    user.setId(res.getInt("id"));
                    LocationManager.registerNewUser(user.getId());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });

            if (user.getId() > 0) {
                String profileQuery = "INSERT INTO userProfilePic (photo) VALUE (null);";
                DBManager.runExecute(profileQuery);
                String passwordQuery = String.format("INSERT INTO passwords (userId,hashedPassword) value ('%d','%s');",
                        user.getId(), p);

                return DBManager.runExecute(passwordQuery) > 0;
            }
        }
        return false;
    }

    public static boolean emailValidation(String emailTxt) {
        /*
         * Email Validation function using a regex.
         * if the input is matching the regex it will return true.
         */
        String regex = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]+(?:\\.[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]+)*@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$";

        Pattern pattern = Pattern.compile(regex);

        Matcher matcher = pattern.matcher(emailTxt);
        return matcher.matches();
    }

    public static boolean updatePassword(String emailTxt, String newPassword) {
        /*
         * This function get a email and a new password,
         * need to use this function after the email was validated!
         *
         * this function return a boolean if the operation was succeed it will return true
         * else false;
         */
        String p = Encryptor.encryptThisString(newPassword);
        Users updatePasswordToThisUser = returnUserId(emailTxt.toLowerCase());
        if (updatePasswordToThisUser != null) {
            String query = String.format("UPDATE passwords SET hashedPassword = '%s' WHERE userId = %d;", p, updatePasswordToThisUser.getId());
            return DBManager.runExecute(query) > 0;
        } else
            return false;


    }

    public static boolean blockedUser(int user,int userToBlock){
        if (user != 0 && userToBlock != 0) {
            if (DBManager.isExists(String.format("SELECT id from users where id = %d",user)) > 0 && DBManager.isExists(String.format("SELECT id FROM users WHERE id = %d",userToBlock)) > 0){
                return DBManager.runExecute(String.format("INSERT INTO blocked (userId, blockedUserId) values (%d , %d)", user, userToBlock)) > 0;
            }
        }
        return false;
    }

    public static boolean removeBlock(int userId,int userToRemove){
        if (DBManager.isExists(String.format("SELECT id from users where id = %d",userId)) > 0 && DBManager.isExists(String.format("SELECT id FROM users WHERE id = %d",userToRemove)) > 0){
            return DBManager.runExecute(String.format("DELETE FROM blocked WHERE userId = %d AND blockedUserId = %d;",userId,userToRemove)) > 0;
        }
        return false;
    }

    //---------------------------------ArrayList<Integer>--------------------------------

    public static ArrayList<Integer> getMyBlockedUsersList(int userId){
        ArrayList<Integer> usersArrayList = new ArrayList<>();
        if (userId != 0){
            DBManager.runSelect(String.format("SELECT blockedUserId FROM blocked WHERE userId = %d",userId),resultSet -> {
                try {
                    usersArrayList.add(resultSet.getInt("blockedUserId"));
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            });
        }

        return usersArrayList;
    }

    //----------------------------------------Users----------------------------------------
    public static Users checkAuthentication(String emailTxt, String password) {
        /*
         * This function get an email and password and check if there is a such
         * user in the DB and return it as an JsonObject.
         * first it check if the user is exists by calling the isExists() function
         * and then it call the returnUser function... that return a user object :)
         */
        String p = Encryptor.encryptThisString(password);
        String query = String.format("select email from users join passwords on"
                + " passwords.userId = users.id where email = '%s' and hashedPassword = '%s';", emailTxt.toLowerCase(), p);

        if (DBManager.isExists(query) == 1) {
            return returnUserId(emailTxt.toLowerCase());
        } else {
            return null;
        }

    }

//    private static Users returnUser(String emailTxt) {
//        /*
//         * This function get an email as an argument and first check if there is such a user in the DB.
//         * second it send a query to the GetUserInfo function.
//         */
//        String query = String.format(
//                "select email ,firstName ,lastName , phoneNumber, age, gender, interestedIn , score from users where email = '%s';",
//                emailTxt.toLowerCase());
//        if (DBManager.isExists(query) > 0) {
//            return DBManager.getUserInfo(query);
//        } else {
//            return null;
//        }
//    }

    public static Users returnUserId(String emailTxt) {
        /*
         * This function get an email as an argument and first check if there is such a user in the DB.
         * second it send a query to the GetUserInfo function.
         */
        String query = String.format(
                "select * from users where email = '%s';", emailTxt.toLowerCase());
        if (DBManager.isExists(query) > 0) {
            return DBManager.getUserInfoWithId(query);
        } else {
            return null;
        }
    }

    public static Users returnUserId(int id) {
        /*
         * This function get an id as an argument and first check if there is such a user in the DB.
         * second it send a query to the GetUserInfo function.
         */
        String query = String.format(
                "select * from users where id = %d;", id);
        if (DBManager.isExists(query) > 0) {
            return DBManager.getUserInfoWithId(query);
        } else {
            return null;
        }
    }

	public static int updateFirstName(int userId, String firstName) {
		// TODO Auto-generated method stub
		String query = String.format("update users Set firstName = '%s' where id = %d;", firstName,userId);
		return DBManager.runExecute(query);
	}

	public static int updateLastName(int userId, String lastName) {
		// TODO Auto-generated method stub
		String query = String.format("update users Set lastName = '%s' where id = %d;", lastName,userId);
		return DBManager.runExecute(query);
	}

	public static int updatePhoneNumber(int userId, String phoneNumber) {
		// TODO Auto-generated method stub
		String query = String.format("update users Set phoneNumber = '%s' where id = %d;", phoneNumber,userId);
		return DBManager.runExecute(query);
	}


	public static int updateUserEmail(int userId, String email) {
		// TODO Auto-generated method stub
		if (emailValidation(email)) {
			String query = String.format("Select email from users where email = '%s';", email);
            if (DBManager.isExists(query) == 0) {
            	query = String.format("update users Set email = '%s' where id = %d;", email,userId);
				return DBManager.runExecute(query);	
            }
			}
		return -1;
	}

}
