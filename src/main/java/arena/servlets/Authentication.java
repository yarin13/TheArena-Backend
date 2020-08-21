package arena.servlets;

import arena.dal.UseresManager;
import arena.bll.Users;
import org.json.simple.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

@WebServlet("/Authentication")
public class Authentication extends HttpServlet {
    /*
     * -----------------------------------------------Authentication-----------------------------------------------
     * To check if a single user is exists |GET: http://localhost:8080/TheArenaServler/Authentication 	|Done
     * To register a new user 			   |POST:http://localhost:8080/TheArenaServler/Authentication	|Done
     * To reset a user password			   |PUT: http://localhost:8080/TheArenaServler/Authentication	|Not yet ready
     */
    private static final long serialVersionUID = 1L;
    private static JSONObject res =  new JSONObject();

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if(checkHeader(request)) {
            Users signInUser = UseresManager.checkAutacntication(request.getHeader("email"),
                    request.getHeader("password"));
            res.clear();
            if (signInUser != null) {
                res.put("email", signInUser.getEmail().toString());
                response.getWriter().append(res.toJSONString());
            } else {
                res.put("Error", "User is not exists");
                response.getWriter().append(res.toJSONString());
            }
        }else {
            res.clear();
            res.put("Error", "Please check your request!");
            response.getWriter().append(res.toJSONString());
        }
        return;

    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        res.clear();
        if (checkParameters(request, response))
            UseresManager.beforeInsertUser(request , response);
    }

    /**
     * @see HttpServlet#doPut(HttpServletRequest, HttpServletResponse)
     */
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        res.clear();
        //need to check how to get request parameters ....
    }

    //----------------------------------------Other----------------------------------------

    protected boolean checkParameters(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        res.clear();
        Object[] paramsObjects = request.getParameterMap().keySet().toArray();
        ArrayList<String> values = new ArrayList<String>();

        for (int i = 0; i < paramsObjects.length; i++) {
            if (request.getParameter((String) paramsObjects[i]) != null
                    && !request.getParameter((String) paramsObjects[i]).isBlank()) {
                values.add(request.getParameter((String) paramsObjects[i]));
            } else {
                values.add(null);
            }
        }

        if (values.contains(null) || values.isEmpty()) {
            res.put("Error", "Missing some fields");
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
        for (int i = 0; i < values.length; i++)
            if (values[i] == null)
                return null;

        return values;

    }

    public static boolean checkHeader(HttpServletRequest request) {
        String email = request.getHeader("email");
        String pass = request.getHeader("password");

        if (email != null)
            if ((UseresManager.emailValidation(email) && pass != null && !pass.isBlank()))
                return true;
        return false;

    }

}
