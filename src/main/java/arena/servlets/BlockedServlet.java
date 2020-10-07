package arena.servlets;

import arena.bll.UsersManager;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/BlockedServlet")
public class BlockedServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JSONObject jsonObject = Authentication.getBodyParams(request);
        HashMap<String,String> map = Authentication.checkParameters(jsonObject);

        assert map != null;
        try {
            int user = Integer.parseInt(map.get("userId"));
            int userToBlock = Integer.parseInt(map.get("userToBlock"));

            Integer[] argsToPass = {user, userToBlock};
            if (UsersManager.blockedUser(user, userToBlock)) {
                responseHandler(response, 200, argsToPass);
            } else {
                responseHandler(response, 400, argsToPass);
            }
        }
        catch (Exception e){
            responseHandler(response,417,null);
        }

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int user;
        if (!request.getHeader("userId").isEmpty())
            user = Integer.parseInt(request.getHeader("userId"));
        else {
            responseHandler(response, 417, null);
            return;
        }
        ArrayList<Integer> userArray = UsersManager.getMyBlockedUsersList(user);

        if (userArray.size() == 0){
            response.setStatus(204);
        }else{
            org.json.simple.JSONObject res;
            Map<String, ArrayList<Integer>> jsonMap = new HashMap<>();
            jsonMap.put("UserIds",userArray);
            res = new org.json.simple.JSONObject(jsonMap);
            response.setStatus(200);
            response.getWriter().append(res.toJSONString());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JSONObject jsonObject = Authentication.getBodyParams(request);
        HashMap<String,String> map = Authentication.checkParameters(jsonObject);

        try {
            assert map != null;
            int user = Integer.parseInt(map.get("userId"));
            int userToRemoveBlocking = Integer.parseInt(map.get("userToRemoveBlocking"));

            Integer[] argsToPass = {user, userToRemoveBlocking};
            if (UsersManager.removeBlock(user, userToRemoveBlocking)) {
                responseHandler(response, 410, argsToPass);
            } else {
                responseHandler(response, 204, null);
            }
        }
        catch (Exception e){
            responseHandler(response,417,null);
        }

    }

    public static void responseHandler(HttpServletResponse response, int status, Integer[] args) throws IOException {
        org.json.simple.JSONObject res;
        Map<String, String> jsonMap = new HashMap<>();

        if (status == 200){
            jsonMap.put("Success",String.format("Successfully blocked user %d",args[1]));
        }else if (status == 400){
            jsonMap.put("Error",String.format("Could not found user with the id of: %d",args[1]));
        }else if (status == 410){
          jsonMap.put("Success",String.format("Successfully remove user %d from your blocked list",args[1]));
        } else if (status == 417){
            jsonMap.put("Error","Please check your request");
        }
        response.setStatus(status);
        res = new org.json.simple.JSONObject(jsonMap);
        response.getWriter().append(res.toJSONString());
    }
}
