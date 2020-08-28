package arena.bll;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
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

	public static boolean beforeInsertUser(HttpServletRequest request , HttpServletResponse response) throws IOException {
		/*
		 * This function get the request and the response,
		 * first it's validating that all field are not null or blank. 
		 * after it validate the email format, and check the Database..
		 * if the email if valid and there are no user with this email it will create a User object
		 * and pass it as an argument to the insertUser function with his password. 
		 */
		Object[] values = Authentication.checkParameters(request);
		
		// Email validation
		if (values == null) throw new AssertionError();
		String mailTxt = (String) values[0];
		if (emailValidation(mailTxt)) {
			String query = String.format("Select email from users where email = '%s';", mailTxt);
			if (DBManager.isExists(query) == 0)
				return insertUser(new Users(mailTxt, (String) values[1], (String) values[2], (String) values[3],
						Integer.parseInt((String) values[4]), (String) values[5], (String) values[6], Integer.parseInt((String)values[7])), (String) values[8]);
			else if(DBManager.isExists(query) ==1 ) {
				Map<String,String> jsonMap = new HashMap<>();
				jsonMap.put("Error","User already exists");
				JSONObject jsonError = new JSONObject(jsonMap);
				response.getWriter().append(jsonError.toJSONString());
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
				user.getEmail(), user.getFirstName(), user.getLastName(), user.getPhoneNumber(), user.getAge(),
				user.getGender(), user.getIntrestedIn(), user.getScore());

		String getNewUserId = String.format("select id from users where users.email = '%s';" , user.getEmail());
		

		if (DBManager.runExecute(query) > 0) {
			DBManager.runSelect(getNewUserId, (res) -> {
			try {
				user.setId(res.getInt("id"));
			} catch (SQLException e) {
				e.printStackTrace();
			}
			});
			
			if (user.getId() > 0) {
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
	
	public static boolean updatePassword(HttpServletRequest request,String emailTxt,String newPassword) {
		/*
		 * This function get a email and a new password,
		 * need to use this function after the email was validated! 
		 * 
		 * this function return a boolean if the operation was succeed it will return true
		 * else false;
		 */
		
		String p = Encryptor.encryptThisString(newPassword);
		String query = String.format("UPDATE passwords SET hashedPassword = '%s' WHERE userId = %d;",p,returnUserId(emailTxt).getId());
		return DBManager.runExecute(query) > 0;
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
				+ " passwords.userId = users.id where email = '%s' and hashedPassword = '%s';", emailTxt, p);

		if (DBManager.isExists(query) == 1) {
			return returnUser(emailTxt);
		} else {
			return null;
		}

	}

	private static Users returnUser(String emailTxt) {
		/*
		 * This function get an email as an argument and first check if there is such a user in the DB.
		 * second it send a query to the GetUserInfo function.
		 */
		String query = String.format(
				"select email ,firstName ,lastName , phoneNumber, age, gender, interestedin , score from users where email = '%s';",
				emailTxt);
		if (DBManager.isExists(query) > 0) {
			return DBManager.getUserInfo(query);
		} else {
			return null;
		}
	}

	private static Users returnUserId(String emailTxt) {
		/*
		 * This function get an email as an argument and first check if there is such a user in the DB.
		 * second it send a query to the GetUserInfo function.
		 */
		String query = String.format(
				"select id from users where email = '%s';",
				emailTxt);
		if (DBManager.isExists(query) > 0) {
			return DBManager.getUserInfoWithId(query);
		} else {
			return null;
		}
	}
	

}
