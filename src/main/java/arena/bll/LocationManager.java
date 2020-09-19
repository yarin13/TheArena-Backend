package arena.bll;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONObject;


import arena.dal.DBManager;


public final class LocationManager {
	
	

	public static JSONObject json = new JSONObject();
	public static ArrayList<String> location = new ArrayList<>(); 
	

	
	
	
	public static  void getOnlineUsersLocation(String mail,double lat,double lng) {
//		this function get latitude and longitude from the user and sends back answer with all the people within
//		500 meters(including the user itself)
//		returns null if no one in his area is found

		
//		new Thread(new Runnable() {
//			@Override
//			public void run() {

		location.clear();
					Users currentUser = UsersManager.returnUserId(mail);
					String interestedIn = currentUser.getInterestedIn();
					int minScore = currentUser.getScore() - 11;
					int maxScore = currentUser.getScore() + 11;
					String query;
					
					if(interestedIn.equals("female")) {
						query = String.format("SELECT userId,firstName,lastName,email,phoneNumber,age,lastLatitude,lastLongitude,"
								+ " ( 6371000 * acos( cos( radians(%s) ) * cos( radians( lastLatitude ) ) * cos( radians( lastLongitude ) - radians(%s) ) + sin( radians(%s) ) * sin(radians(lastLatitude)) ) )"
								+ " AS distance From users inner join usersStatus on users.id = usersStatus.userId"
								+ " WHERE loggedIn = true AND "
								+ "gender = 'female' "
								+ "AND score BETWEEN %d AND %d "
								+ "AND userId != %d "
								+ "HAVING distance < 500;",String.valueOf(lat),String.valueOf(lng),String.valueOf(lat),minScore,maxScore,currentUser.getId());
						
					}
					else {
						query = String.format("SELECT userId,firstName,lastName,email,phoneNumber,age,lastLatitude,lastLongitude,"
								+ " ( 6371000 * acos( cos( radians(%s) ) * cos( radians( lastLatitude ) ) * cos( radians( lastLongitude ) - radians(%s) ) + sin( radians(%s) ) * sin(radians(lastLatitude)) ) )"
								+ " AS distance From users inner join usersStatus on users.id = usersStatus.userId"
								+ " WHERE loggedIn = true AND "
								+ "gender = 'male' "
								+ "AND score BETWEEN %d AND %d "
								+ "AND userId != %d "
								+ "HAVING distance < 500;",String.valueOf(lat),String.valueOf(lng),String.valueOf(lat),minScore,maxScore,currentUser.getId());
						
					}
					
					json.clear();

					DBManager.runSelect(query, (res) -> {
						try {
							LocationManager.location.clear();
							LocationManager.location.add(res.getString("firstName"));
							LocationManager.location.add(res.getString("lastName"));
							LocationManager.location.add(res.getString("email"));
							LocationManager.location.add(res.getString("phoneNumber"));
							LocationManager.location.add(res.getString("age"));
							LocationManager.location.add(res.getString("lastLatitude"));
							LocationManager.location.add(res.getString("lastLongitude"));
							json.put(res.getInt("userId"), new ArrayList<String>(LocationManager.location));
						
							

						} catch (SQLException e) {
							System.out.println("empty2");
							json.clear();
							e.printStackTrace();
						}

					});
				
				}
//					
//
//		}).start(); 

//	}
	
//=======================================================	
//	When new user is registered
//	this function should be called from the Authentication
//=======================================================	
	public static void registerNewUser(int id) {
		String query = String.format("INSERT INTO usersStatus(userId,loggedIn,lastLatitude,lastLongitude) values "
				+ "(%d,false,null,null);",id );
		DBManager.runExecute(query);
	}
	
	
//=======================================================	
//	every time the user requests to see other online users in his area
//	this function should update his info as well
//=======================================================	
	public static void updateUsersStatus(String mail, double lat,double lng) {
		Users currentUser = UsersManager.returnUserId(mail);
		
		String query = String.format("UPDATE usersStatus SET loggedIn = true, lastLatitude = %s,lastLongitude = %s WHERE userId = %d;",String.valueOf(lat),String.valueOf(lng),currentUser.getId());
		DBManager.runExecute(query);
	}
	
	
	
//=======================================================	
//	When the user is logged out
//=======================================================	
	public static void logOutUser(String mail) {
		Users currentUser = UsersManager.returnUserId(mail);
		String query = String.format("UPDATE usersStatus SET loggedIn = false WHERE userId = %d;", currentUser.getId());
		DBManager.runExecute(query);
	}

}
