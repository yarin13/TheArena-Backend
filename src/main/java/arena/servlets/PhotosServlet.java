package arena.servlets;

import arena.bll.PhotosManager;
import com.sun.jdi.ClassType;
import com.sun.jdi.IntegerType;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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
                PhotosManager.insertPhoto("fromPOST", mail, null, fileContent, response);
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
        Map<String, ArrayList<Integer>> arrayListHashMap = new HashMap<>();
        Map<String, String> jsonMap = new HashMap<>();
        org.json.simple.JSONObject jsonObject;
        String action = params.getString("action");

        switch (action) {
            case "getPhotosIds": {
                // return array of photos ids
                try {
                    String mail = params.getString("email");
                    photosIds = PhotosManager.selectPhotosIds(mail);
                    if (photosIds != null) {
                        // return the array with ids
                        arrayListHashMap.put(mail, photosIds);
                    } else {
                        // return error saying no photos for that user
                        arrayListHashMap.put("Error", null);
                    }
                    jsonObject = new org.json.simple.JSONObject(arrayListHashMap);
                    response.getWriter().append(jsonObject.toJSONString());
                }catch (Exception e){
                    if (!params.has("email") || params.getString("email") == null || params.getString("email").equals("")) {
                        jsonMap.put("Error", "Missing email or email is empty");
                        jsonObject = new org.json.simple.JSONObject(jsonMap);
                        response.setStatus(400);
                        response.getWriter().append(jsonObject.toJSONString());
                    }
                }
                break;
            }
            case "getPhoto": {
                // return photo matches the given id
                try {
                    int photoId = params.getInt("photoId");
                    String query = String.format("SELECT photo FROM usersPhotos WHERE id = %d;", photoId);
                    response.setContentType("image/jpeg");
                    OutputStream os = response.getOutputStream();
                    PhotosManager.selectPhoto(query, os, response);
                }catch (Exception e){
                    if (!params.has("photoId") || params.getInt("photoId") == 0 || params.getString("photoId").equals("")) {
                        jsonMap.put("Error", "Missing photoId or photoId is empty");
                        jsonObject = new org.json.simple.JSONObject(jsonMap);
                        response.setStatus(400);
                        response.getWriter().append(jsonObject.toJSONString());
                    }
                }
                break;
            }
            case "getProfilePhoto": {
                //return profile photo from the userProfilePic table
                try {
                    int userId = params.getInt("userId");
                    String query = String.format("SELECT photo FROM userProfilePic WHERE id = %d", userId);
                    response.setContentType("image/jpeg");
                    OutputStream os = response.getOutputStream();
                    PhotosManager.selectPhoto(query, os, response);

                } catch (Exception e) {
                    if (!params.has("userId") || params.getString("userId") == null || params.getString("userId").equals("")) {
                        jsonMap.put("Error", "Missing userId or userId is empty");
                        jsonObject = new org.json.simple.JSONObject(jsonMap);
                        response.setStatus(400);
                        response.getWriter().append(jsonObject.toJSONString());
                    }

                }
                break;
            }
        }
    }

    protected void doDelete(HttpServletRequest request, HttpServletResponse response) {
        //============================================================================
        // This function gets the params : "email" and "photoId" and deletes the photo from the DB.
        //============================================================================
        JSONObject params = getBodyParams(request);
        org.json.simple.JSONObject jsonObject;
        Map<String, String> jsonMap = new HashMap<>();

        try {
            String mail = params.getString("email");
            int photoId = params.getInt("photoId");
            if (PhotosManager.deletePhoto(mail, photoId)) {
                jsonMap.put("Success", "Photo was deleted successfully");
                response.setStatus(200);
            } else {
                jsonMap.put("Error", "Could not delete photo");
                response.setStatus(400);
            }
            jsonObject = new org.json.simple.JSONObject(jsonMap);
            response.getWriter().append(jsonObject.toJSONString());

        } catch (JSONException | IOException e1) {
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
                PhotosManager.insertPhoto("fromPUT", null, userId, fileContent, response);
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