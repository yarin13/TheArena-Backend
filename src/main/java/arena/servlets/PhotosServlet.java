package arena.servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.json.JSONException;
import org.json.JSONObject;

import arena.bll.PhotosManager;

/**
 * Servlet implementation class Test
 */
@WebServlet("/PhotosServlet")

@MultipartConfig
public class PhotosServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public PhotosServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */

//============================================================================	
// This function gets a parameter called "email" and photo or photos to upload to the DB.
//============================================================================	
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Collection<Part> parts = request.getParts();
		String mail = request.getParameter("email");
		List<Part> fileParts = (List<Part>) request.getParts();
		for (Part part : parts) {
			InputStream fileContent = part.getInputStream();
			if (!part.getName().equals("email"))
				PhotosManager.insertPhoto(mail, fileContent);
		}
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */

//============================================================================	
//this function gets parameter with key called action
//if the value of that key is: "photosIds" ,the client gets all the photos ids of the requested user(by sending the user's userName
//if the value of that key is: "image" the client gets the photo he requested by sending the id of that photo 	
//============================================================================	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		ArrayList<Integer> photosIds;
		JSONObject params = getBodyParams(request);
		Map<String, ArrayList<Integer>> jsonMap = new HashMap<>();
		org.json.simple.JSONObject res;

		String action = null;
		String mail = null;
		try {
			action = params.getString("action");
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if ("photosIds".equals(action)) {
			// return array of photos ids

			try {
				mail = params.getString("email").toString();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			photosIds = PhotosManager.selectPhotosIds(mail);
			if (photosIds != null) {
				// return the array with ids
				jsonMap.put(mail, photosIds);
				res = res = new org.json.simple.JSONObject(jsonMap);
				response.getWriter().append(res.toJSONString());
				return;
			} else {
				// return error saying no photos for that user
				jsonMap.put("Error", null);
				res = new org.json.simple.JSONObject(jsonMap);
				response.getWriter().append(res.toJSONString());
				return;
			}

		} else if ("image".equals(action)) {
			// return photo matches the given id
			response.setContentType("image/jpeg");
			OutputStream os = response.getOutputStream();
			try {
				int id = params.getInt("photoId");
				PhotosManager.selectPhoto(id, os);
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}

	}
//============================================================================	
//This function gets the params : "email" and "photoId" and deletes the photo from
//the DB.
//============================================================================	

	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
		JSONObject params = getBodyParams(request);
		Map<String, String> jsonMap = new HashMap<>();
		org.json.simple.JSONObject res;

		try {
			String mail = params.getString("email");
			int photoId = params.getInt("photoId");
			if (PhotosManager.deletePhoto(mail, photoId))
				jsonMap.put("Success", "Photo was deleted succesfully");
			else
				jsonMap.put("Error", "Could not delete photo");
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

// ============================================================================
//	this function extracts to body of the request 
//  and returns it as JSONObject	
//============================================================================		
	protected JSONObject getBodyParams(HttpServletRequest request) {
		StringBuilder sb = new StringBuilder();
		String line = null;
		JSONObject json = null;

		BufferedReader reader;
		try {
			reader = request.getReader();
			while ((line = reader.readLine()) != null)
				sb.append(line);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			json = new JSONObject(sb.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return json;

	}

}