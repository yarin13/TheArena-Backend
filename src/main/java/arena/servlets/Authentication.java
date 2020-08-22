package arena.servlets;

import arena.dal.UsersManager;
import arena.bll.Users;
import org.json.simple.JSONObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/Authentication")
public class Authentication extends HttpServlet {
    /*
     * -----------------------------------------------Authentication-----------------------------------------------
     * To check if a single user is exists |GET: http://localhost:8080/TheArenaServler/Authentication 	|Done
     * To register a new user 			   |POST:http://localhost:8080/TheArenaServler/Authentication	|Done
     * To reset a user password			   |PUT: http://localhost:8080/TheArenaServler/Authentication	|Not yet ready
     */
    private static final long serialVersionUID = 1L;


    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        Map<String,String> jsonMap = new HashMap<>();
        JSONObject res ;
        if(checkHeader(request)) {
            Users signInUser = UsersManager.checkAuthentication(request.getHeader("email"),
                    request.getHeader("password"));

            if (signInUser != null) {
                jsonMap.put("email", signInUser.getEmail());
            } else {
                jsonMap.put("Error", "User is not exists");
            }
        }else {
            jsonMap.put("Error", "Please check your request!");
        }
        res = new JSONObject(jsonMap);
        response.getWriter().append(res.toJSONString());
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        Map<String,String> jsonMap = new HashMap<>();
        JSONObject res;
        if (checkParameters(request, response))
            if (UsersManager.beforeInsertUser(request , response)){
                jsonMap.put("Success","New user is created!");
                res = new JSONObject(jsonMap);
                response.getWriter().append(res.toJSONString());
            }
    }

    /**
     * @see HttpServlet#doPut(HttpServletRequest, HttpServletResponse)
     */
    protected void doPut(HttpServletRequest request, HttpServletResponse response) {
        //TODO: check parameters from the request and reset user password.

    }

    //----------------------------------------Other----------------------------------------

    protected boolean checkParameters(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        Map<String,String> jsonMap = new HashMap<>();
        JSONObject res ;
        Object[] paramsObjects = request.getParameterMap().keySet().toArray();
        ArrayList<String> values = new ArrayList<>();

        for (Object paramsObject : paramsObjects) {
            if (request.getParameter((String) paramsObject) != null
                    && !request.getParameter((String) paramsObject).isBlank()) {
                values.add(request.getParameter((String) paramsObject));
            } else {
                values.add(null);
            }
        }

        if (values.contains(null) || values.isEmpty()) {
            jsonMap.put("Error", "Missing some fields");
            res = new JSONObject(jsonMap);
            response.getWriter().append(res.toJSONString());
            return false;
        }
        return true;
    }

    public static Object[] checkParameters(HttpServletRequest request) {
        Object[] paramsObjects = request.getParameterMap().keySet().toArray();
        Object[] values = new Object[paramsObjects.length];

        for (int i = 0; i < paramsObjects.length; i++) {
            if (request.getParameter((String) paramsObjects[i]) != null
                    && !request.getParameter((String) paramsObjects[i]).isBlank())
                values[i] = (request.getParameter((String) paramsObjects[i]));
            else
                values[i] = null;
        }

        // Check if there is any blanks or null in the fields..
        for (Object value : values)
            if (value == null)
                return null;

        return values;

    }

    public static boolean checkHeader(HttpServletRequest request) {
        String email = request.getHeader("email");
        String pass = request.getHeader("password");

        if (email != null)
            return UsersManager.emailValidation(email) && pass != null && !pass.isBlank();
        return false;

    }
}