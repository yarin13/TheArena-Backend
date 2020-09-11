package arena.servlets;

import arena.bll.UsersManager;
import arena.bll.Users;

import org.json.JSONException;
import org.json.JSONObject;

//import org.json.simple.JSONObject;
//import org.json.JSONObject;
//import org.json.simple.JSONObject;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

@WebServlet("/Authentication")
public class Authentication extends HttpServlet {
    /*
     * -----------------------------------------------Authentication-----------------------------------------------
     * To check if a single user is exists |GET: http://localhost:8080/TheArenaServlet/Authentication 	|Done
     * To register a new user 			   |POST:http://localhost:8080/TheArenaServlet/Authentication	|Done
     * To reset a user password			   |PUT: http://localhost:8080/TheArenaServlet/Authentication	|Done
     */
    private static final long serialVersionUID = 1L;

    
    public static JSONObject bodyParams = null;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        Map<String,String> jsonMap = new HashMap<>();
        org.json.simple.JSONObject res ;
        Authentication.bodyParams = null;
        Authentication.bodyParams = getBodyParams(request);
        //org.json.JSONObject bodyParams = getBodyParams(request);
        String mail = null;
        String password = null;
		try {
			mail = Authentication.bodyParams.get("email").toString();
			password = Authentication.bodyParams.get("password").toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        
        if(checkHeader(request)) {
            Users signInUser = UsersManager.checkAuthentication(mail,password);

            if (signInUser != null) {
                jsonMap.put("Success", "success");
            } else {
                jsonMap.put("Error", "User is not exists");
            }
        }else {
            jsonMap.put("Error", "Please check your request!");
        }
        res = new org.json.simple.JSONObject(jsonMap);
       response.getWriter().append(res.toJSONString());
        
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
    	Authentication.bodyParams = null;
        Map<String,String> jsonMap = new HashMap<>();
        org.json.simple.JSONObject res;
        if (checkParameters(request, response))
            if (UsersManager.beforeInsertUser(request , response)){
                jsonMap.put("Success","New user is created!");
                res = new org.json.simple.JSONObject(jsonMap);
                response.getWriter().append(res.toJSONString());
            }
    }

    /**
     * @see HttpServlet#doPut(HttpServletRequest, HttpServletResponse)
     */
    
//==================================
    //PUT requests to update password
   // If it runs into an error it returns error else returns nothing if everything went ok
//==================================    
    protected void doPut(HttpServletRequest request, HttpServletResponse response) {
		 Map<String,String> jsonMap = new HashMap<>();
		 org.json.simple.JSONObject res;
	     
		 org.json.JSONObject bodyParams = getBodyParams(request);
	     String mail = null;
	     String newPassword = null;
		try {
			mail = bodyParams.get("email").toString();
			newPassword = bodyParams.get("newPassword").toString();
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	     

    	
    	if(!UsersManager.updatePassword(mail, newPassword)) 
    	    jsonMap.put("error","could not update password");
    	else 
    		jsonMap.put("Success","Success");
       
    	res = new org.json.simple.JSONObject(jsonMap);
        try {
			response.getWriter().append(res.toJSONString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

    }

    //----------------------------------------Other----------------------------------------

    protected boolean checkParameters(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
    	System.out.println("checkParameters1: ttt");
        Map<String,String> jsonMap = new HashMap<>();
        org.json.simple.JSONObject res ;
        
        
        	Authentication.bodyParams = getBodyParams(request);

        
        
        Iterator paramsObjects = bodyParams.keys();
        ArrayList<String> values = new ArrayList<>();

        
        while(paramsObjects.hasNext()) {
        	Object paramsObject = paramsObjects.next();
            try {
				if (bodyParams.get((String) paramsObject) != null
				        && !bodyParams.get((String) paramsObject).toString().isBlank()) {
				    values.add(bodyParams.get((String) paramsObject).toString());
				} else {
				    values.add(null);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
//        for (Object paramsObject : paramsObjects) {
//            try {
//				if (bodyParams.get((String) paramsObject) != null
//				        && !bodyParams.get((String) paramsObject).toString().isBlank()) {
//				    values.add(bodyParams.get((String) paramsObject).toString());
//				} else {
//				    values.add(null);
//				}
//			} catch (JSONException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//        }

        if (values.contains(null) || values.isEmpty()) {
            jsonMap.put("Error", "Missing some fields");
            res = new org.json.simple.JSONObject(jsonMap);
            response.getWriter().append(res.toJSONString());
            return false;
        }
        return true;
    }

    public static HashMap<String,String> checkParameters(HttpServletRequest request) {
    	System.out.println("checkParameters2:  tetsing");

    	
    	
    	System.out.println(Authentication.bodyParams.keys());
    	Iterator paramsKeys = Authentication.bodyParams.keys();
    	ArrayList<String> paramsObjects = new ArrayList<String>();
    	  while (paramsKeys.hasNext()) {
    		  paramsObjects.add(paramsKeys.next().toString());
    		  }
    	
    	//Object[] paramsObjects = bodyParams.keys();
        Object[] values = new Object[paramsObjects.size()];
        HashMap<String,String> val = new HashMap<String,String>();
        
        for (int i = 0; i < paramsObjects.size(); i++) {
            try {
				if (Authentication.bodyParams.get((String) paramsObjects.get(i)) != null
				        && !Authentication.bodyParams.get((String) paramsObjects.get(i)).toString().isBlank())
					val.put((String) paramsObjects.get(i), Authentication.bodyParams.get((String) paramsObjects.get(i)).toString());
				   // values[i] = (request.getParameter((String) paramsObjects.get(i)));
				else
					val.put("null","null");
//				    values[i] = null;
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        

        if(val.containsKey("null"))
        	return null;
        return val;
//        // Check if there is any blanks or null in the fields..
//        for (Object value : values)
//            if (value == null)
//                return null;

        

    }

    public static boolean checkHeader(HttpServletRequest request) {
//    	Authentication.bodyParams = getBodyParams(request);
        String email = null;
        String pass = null;
		try {
			email = Authentication.bodyParams.get("email").toString();
			pass = Authentication.bodyParams.get("password").toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        

        if (email != null)
            return UsersManager.emailValidation(email) && pass != null && !pass.isBlank();
        return false;

    }
    
    
//============================================================================	
//	this function extracts to body of the request 
//  and returns it as JSONObject	
//============================================================================		
	protected static org.json.JSONObject getBodyParams(HttpServletRequest request) {
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
				System.out.println("getBodyParams:  cant cast");
				e.printStackTrace();
			}

		System.out.println(json);
		return json;
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
//		 StringBuilder sb = new StringBuilder();
//		 String line = null;
//		 org.json.JSONObject json = null;
// 
//		 BufferedReader reader;
//		try {
//			reader = request.getReader();
//			 while ((line = reader.readLine()) != null) {
//				 sb.append(line);
//				 
//			 }
//				 
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		
//
//
//			try {
//				json = new org.json.JSONObject(sb.toString());
//				//System.out.println(sb.toString());
//				
//			} catch (JSONException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//
////			JSONParser parser = new JSONParser(); 
////			try {
////				json = (org.json.JSONObject) parser.parse(sb.toString());
////			} catch (ParseException e) {
////				// TODO Auto-generated catch block
////				e.printStackTrace();
////			}
//			
//
//		return json;
		 
	}
    
    
}