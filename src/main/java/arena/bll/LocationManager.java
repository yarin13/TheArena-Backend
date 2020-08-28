package arena.bll;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;

import arena.dal.DBManager;


public final class LocationManager {
	
	

	public static JSONObject json = new JSONObject();
	public static ArrayList<String> location = new ArrayList<>(); 
	

	
	
	public static void getOnlineUsersLocation(String lat,String lng) {
//		this function get latitude and longtitude from the user and sends back answer with all the people within
//		500 meters(includind the user itself)
//		returns null if no one in his area is found

		json.clear();
		String query = String.format("		SELECT userId,lastLatitude,lastLongtitude\r\n" + 
				"		  , ( 6371000 * acos( cos( radians(%s) ) * cos( radians( lastLatitude ) ) * cos( radians( lastLongtitude ) - radians(%s) ) + sin( radians(%s) ) * sin(radians(lastLatitude)) ) ) AS distance \r\n" + 
				"		FROM \r\n" + 
				"		  usersStatus\r\n" + 
				"		where logedIn = true\r\n" + 
				"		HAVING \r\n" + 
				"		  distance < 500;",lat,lng,lat);
		
		


		DBManager.runSelect(query, (res) -> {
			try {
				location.clear();
				location.add(res.getString("lastLatitude"));
				location.add(res.getString("lastLongtitude"));
				
				//json.put(res.getInt("userId", new ArrayList<>(location));
				json.put(res.getInt("userId"), location);
				
			} catch (SQLException e) {
				json.clear();
				e.printStackTrace();
			}

		});
	}

}
