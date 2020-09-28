package arena.servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
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

import arena.dal.DBManager;
import org.json.JSONException;
import org.json.JSONObject;

import arena.bll.PhotosManager;

/**
 * Servlet implementation class Test
 */
@WebServlet("/PhotosServlet")

@MultipartConfig
public class PhotosServlet extends HttpServlet {
    // doPOST: adding a picture to the userPhoto table, a user can have more then one picture.
    //
    // doPUT: once a user register with the Authentication servlet it get a default profile picture, in order to update that picture,
    // you will have to send a userId and a picture.
    //
    // doGET: this function get an action that is one of the following : (getPhotosIds, getPhoto, getProfilePhoto).
    // getPhotosIds & email - return array of photos ids.
    // getPhoto & photoId - return a single photo.
    // getProfilePhoto & userId - return a user profile picture.
    //
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public PhotosServlet() {
        super();
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     * response)
     */

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //============================================================================
        // This function gets a parameter called "email" and photo or photos to the userPhoto table.
        //============================================================================
        Collection<Part> parts = request.getParts();
        String mail = request.getParameter("email");

        for (Part part : parts) {
            InputStream fileContent = part.getInputStream();
            if (!part.getName().equals("email"))
                PhotosManager.insertPhoto("fromPOST", mail, null, fileContent);
        }
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     * response)
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //============================================================================
        //this function gets parameter with key called action
        //if the value of that key is: "photosIds" ,the client gets all the photos ids of the requested user(by sending the user's userName
        //if the value of that key is: "image" the client gets the photo he requested by sending the id of that photo
        //============================================================================
        ArrayList<Integer> photosIds;
        JSONObject params = getBodyParams(request);
        Map<String, ArrayList<Integer>> jsonMap = new HashMap<>();
        org.json.simple.JSONObject res;
        String action = params.getString("action");

        switch (action) {
            case "getPhotosIds": {
                // return array of photos ids
                String mail = params.getString("email");
                photosIds = PhotosManager.selectPhotosIds(mail);
                if (photosIds != null) {
                    // return the array with ids
                    jsonMap.put(mail, photosIds);
                    res = new org.json.simple.JSONObject(jsonMap);
                    response.getWriter().append(res.toJSONString());
                } else {
                    // return error saying no photos for that user
                    jsonMap.put("Error", null);
                    res = new org.json.simple.JSONObject(jsonMap);
                    response.getWriter().append(res.toJSONString());
                }
                break;
            }
            case "getPhoto": {
                // return photo matches the given id
                response.setContentType("image/jpeg");
                OutputStream os = response.getOutputStream();
                int photoId = params.getInt("photoId");
                String query = String.format("SELECT photo FROM usersPhotos WHERE id = %d;", photoId);
                PhotosManager.selectPhoto(query, os);
                break;
            }
            case "getProfilePhoto": {
                //return profile photo from the userProfilePic table
                response.setContentType("image/jpeg");
                OutputStream os = response.getOutputStream();
                String userId = params.getString("userId");
                String query = String.format("SELECT photo FROM userProfilePic WHERE id = %s", userId);
                PhotosManager.selectPhoto(query, os);
                break;
            }
        }
    }

    protected void doDelete(HttpServletRequest request, HttpServletResponse response) {
        //============================================================================
        // This function gets the params : "email" and "photoId" and deletes the photo from the DB.
        //============================================================================
        JSONObject params = getBodyParams(request);
        Map<String, String> jsonMap = new HashMap<>();

        try {
            String mail = params.getString("email");
            int photoId = params.getInt("photoId");
            if (PhotosManager.deletePhoto(mail, photoId))
                jsonMap.put("Success", "Photo was deleted successfully");
            else
                jsonMap.put("Error", "Could not delete photo");
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //============================================================================
        // This function gets the params : "userId" for the userID, and a "newPhoto".
        // We use this function to update user profile picture.
        //============================================================================
        Collection<Part> parts = request.getParts();
        int userId = Integer.parseInt(request.getParameter("userId"));
        for (Part part : parts) {
            if (part.getName().equals("newPhoto")) {
                InputStream fileContent = part.getInputStream();
                PhotosManager.insertPhoto("fromPUT",null,userId, fileContent);
            }
        }
    }

    protected JSONObject getBodyParams(HttpServletRequest request) {
        // ============================================================================
        //	this function extracts to body of the request
        //  and returns it as JSONObject
        //============================================================================
        StringBuilder sb = new StringBuilder();
        String line;
        JSONObject json = null;

        BufferedReader reader;
        try {
            reader = request.getReader();
            while ((line = reader.readLine()) != null)
                sb.append(line);
            json = new JSONObject(sb.toString());
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return json;
    }
}