package arena.bll;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.Part;

import org.json.simple.JSONObject;

import arena.dal.DBManager;

public class PhotosManager {

	public static JSONObject json = new JSONObject();
	public static ArrayList<FileOutputStream> photos = new ArrayList<>();

//============================================================================	
// This function is used by the PhotosServlet and is called
// for each image the user sends to the server,in order to upload it to the DB.
//============================================================================	
	public static void insertPhoto(String mail, InputStream image) throws IOException {
		Users currentUser = UsersManager.returnUserId(mail);
		DBManager.insertImage(currentUser.getId(), image);
	}

//============================================================================
// This function is used by the PhotosServlet and used to get 
// a specific photo from the DB.
//============================================================================	
	public static void selectPhoto(int id, OutputStream os) {
		json.clear();
		String query = String.format("SELECT photo FROM usersPhotos WHERE id = %d;", id);
		try {
			DBManager.selectImage(query, os);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

//============================================================================
// This function is used by the PhotosServlet and used to delete 
// a specific photo from the DB.
// Returns true if the delete succeeded and false otherwise.
//============================================================================	
	public static boolean deletePhoto(String mail, int photoId) {
		Users currentUser = UsersManager.returnUserId(mail);
		String query = String.format("DELETE FROM usersPhotos WHERE id = %d AND userId = %d;", photoId,
				currentUser.getId());
		if (DBManager.runExecute(query) > 0) {
			return true;
		} else
			return false;

	}

//============================================================================
// This function is used by the PhotosServlet and used to retrieve 
// all the photos id's of a specified user.
//============================================================================	
	public static ArrayList<Integer> selectPhotosIds(String mail) {
		ArrayList<Integer> photosIds = new ArrayList<Integer>();
		Users currentUser = UsersManager.returnUserId(mail);
		String query = String.format("SELECT id FROM usersPhotos WHERE userId = %d;", currentUser.getId());
		if (DBManager.isExists(query) > 0) {
			DBManager.runSelect(query, (res) -> {
				try {
					photosIds.add(res.getInt("id"));
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			});
			return photosIds;
		}
		return null;
	}

}
