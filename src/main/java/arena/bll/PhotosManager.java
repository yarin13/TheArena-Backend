package arena.bll;



import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.http.Part;

import org.json.simple.JSONObject;

import arena.dal.DBManager;

public class PhotosManager {

	
	public static JSONObject json = new JSONObject();
	public static ArrayList<String> photos = new ArrayList<>();
	
	public static void insertPhoto(String mail,InputStream image) throws IOException {
		/*

		 */
	
		Users currentUser = UsersManager.returnUserId(mail);
		//String query = String.format("INSERT INTO usersPhotos(userId,photo) values (%d,%b);", currentUser.getId(),image);
//		String query = "INSERT INTO usersPhotos(userId,photo) values ("+currentUser.getId() +","+ image.read()+");";
//		DBManager.runExecute(query);
		DBManager.runExecuteImage(currentUser.getId(), image);
	}
	
	public static void selectPhoto(String mail) {
		json.clear();
		photos.clear();
		
		Users currentUser = UsersManager.returnUserId(mail);
		String query = String.format("SELECT photo from usersPhotos where userId = %d;", currentUser.getId());
		
		
		DBManager.runSelect(query, (res) -> {
			try {

				photos.add(res.getString("photo"));
				json.put("userPhoto", photos);
				
			} catch (SQLException e) {
				photos.clear();
				json.clear();
				//questions.clear();
				e.printStackTrace();
			}
		});
	}
	
	
}
