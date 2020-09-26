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


	public static void insertPhoto(String mail, InputStream image) {
		//============================================================================
		// This function is used by the PhotosServlet and is called
		// for each image the user sends to the server,in order to upload it to the DB.
		//============================================================================
		Users currentUser = UsersManager.returnUserId(mail);
		assert currentUser != null;
		DBManager.insertImage(currentUser.getId(), image);
	}

	public static void selectPhoto(int id, OutputStream os) {
		//============================================================================
		// This function is used by the PhotosServlet and used to get
		// a specific photo from the DB.
		//============================================================================
		json.clear();
		String query = String.format("SELECT photo FROM usersPhotos WHERE id = %d;", id);
		try {
			DBManager.selectImage(query, os);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean deletePhoto(String mail, int photoId) {
		//============================================================================
		// This function is used by the PhotosServlet and used to delete
		// a specific photo from the DB.
		// Returns true if the delete succeeded and false otherwise.
		//============================================================================
		Users currentUser = UsersManager.returnUserId(mail);
		assert currentUser != null;
		String query = String.format("DELETE FROM usersPhotos WHERE id = %d AND userId = %d;", photoId,
				currentUser.getId());
		return DBManager.runExecute(query) > 0;

	}

	public static ArrayList<Integer> selectPhotosIds(String mail) {
		//============================================================================
		// This function is used by the PhotosServlet and used to retrieve
		// all the photos id's of a specified user.
		//============================================================================
		ArrayList<Integer> photosIds = new ArrayList<>();
		Users currentUser = UsersManager.returnUserId(mail);
		assert currentUser != null;
		String query = String.format("SELECT id FROM usersPhotos WHERE userId = %d;", currentUser.getId());
		if (DBManager.isExists(query) > 0) {
			DBManager.runSelect(query, (res) -> {
				try {
					photosIds.add(res.getInt("id"));
				} catch (SQLException e) {
					e.printStackTrace();
				}
			});
			return photosIds;
		}
		return null;
	}

}
