package arena.servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.simple.JSONArray;
//import org.json.simple.JSONObject;

import org.json.JSONObject;

import arena.bll.*;
import arena.dal.DBManager;
/**
 * Servlet implementation class onlineUsersLocation
 */
@WebServlet("/onlineUsersLocation")
public class OnlineUsersLocation extends HttpServlet {
	
    /*
     * -------------------------------------------onlineUsersLocation-------------------------------------------
     * GET: http://localhost:8080/TheArenaServlet/onlineUsersLocation
     */
	
	
	private static final long serialVersionUID = 1L;
//	private static JSONArray jsonResponse = new JSONArray();
 
    /**
     * @see HttpServlet#HttpServlet()
     */
    public OnlineUsersLocation() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
//    {
//        "lat":31.895668,
//        "lng":35.016050,
//        "mail":"inbaryarin@gmail.com"
//    }
//=====================================================    
//    added synchronized because of the jsonResponse
//	  probably can be removed if we create the jsonResponse locally
//    Headers to pass:"lat","lng","mail"
//	  The get also updated the user current location in the DB    
//=====================================================    
    
	protected  void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		JSONArray jsonResponse = new JSONArray();
//		jsonResponse.clear();
		String lat = null;
		String lng = null;
		String mail = null;
		JSONObject params = getBodyParams(request);
		
        Map<String,String> jsonMap = new HashMap<>();
        JSONObject res ;
		
		try {
			lat = params.get("lat").toString();
			lng = params.get("lng").toString();
			mail = params.get("mail").toString();
		} catch (JSONException e) {
        	jsonMap.put("Error","Missing some fields");
        	res = new JSONObject(jsonMap);
            jsonResponse.add(res);
            response.getWriter().append(jsonResponse.toJSONString());
		}

		LocationManager.updateUsersStatus(mail, lat, lng);
		
		jsonResponse.clear();
		LocationManager.getOnlineUsersLocation(mail,lat,lng);
		
        if (!LocationManager.location.isEmpty()) {
            jsonResponse.add(LocationManager.json);
            response.getWriter().append(jsonResponse.toJSONString());
        } else {
        	jsonMap.put("Error","No one else was found");
        	res = new JSONObject(jsonMap);
            jsonResponse.add(res);
            response.getWriter().append(jsonResponse.toJSONString());
        }
    
        return;
    }
		
		
		
	
	

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	
//============================================================================	
//	when the app goes to onDestroy this POST sets the user as offline
//  Headers to pass:"email"(the userName from the android) 	
//============================================================================	
	
	protected  void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		JSONArray jsonResponse = new JSONArray();
//		jsonResponse.clear();
		String mail = null;
		JSONObject params = getBodyParams(request);
	    Map<String,String> jsonMap = new HashMap<>();
	    JSONObject res ;
		try {
			mail = params.get("mail").toString();
			LocationManager.logOutUser(mail);
		} catch (JSONException e) {
	      	jsonMap.put("Error","Missing some fields");
        	res = new JSONObject(jsonMap);
            jsonResponse.add(res);
            response.getWriter().append(jsonResponse.toJSONString());
		}
		
		
	}
	

	
	
	
//============================================================================	
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
