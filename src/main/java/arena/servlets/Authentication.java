package arena.servlets;

import arena.bll.UsersManager;
import arena.bll.Users;
import arena.dal.DBManager;
import org.json.JSONException;
import org.json.JSONObject;
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

@WebServlet("/Authentication")
public class Authentication extends HttpServlet {
	/*
	 * -----------------------------------------------Authentication-----------------------------------------------
	 * To check if a single user is exists |POST: http://localhost:8080/TheArenaServlet/Authentication |Done
	 * To register a new user |POST:http://localhost:8080/TheArenaServlet/Authentication |Done
	 * To reset a user password |PUT: http://localhost:8080/TheArenaServlet/Authentication |Done
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected synchronized void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		//==================================
        //POST requests to Log in or Register password
        //If the request body have only "email" and "password" keys so its a login request
        //else it is a register request
        //==================================

		org.json.JSONObject bodyParams = getBodyParams(request);
		Map<String, String> jsonMap = new HashMap<>();
		org.json.simple.JSONObject res;

		if (bodyParams.length() != 2) {
			// register new user
			if (checkParameters(bodyParams, response))
				if (UsersManager.beforeInsertUser(bodyParams, response)) {

					jsonMap.put("Success", "New user is created!");
					res = new org.json.simple.JSONObject(jsonMap);
					response.getWriter().append(res.toJSONString());
				}
		} else {
			// signIn user
			String mail;
			String password;
			try {
				mail = bodyParams.get("email").toString();
				password = bodyParams.get("password").toString();
				Users signInUser = UsersManager.checkAuthentication(mail, password);

				if (signInUser != null) {
					jsonMap.put("Success", "success");
				} else {
					jsonMap.put("Error", "one or more fields are incorrect");
				}
			} catch (JSONException e) {
				jsonMap.put("Error", "one or more fields are missing");
				res = new org.json.simple.JSONObject(jsonMap);
				response.getWriter().append(res.toJSONString());
				return;
			}
			res = new org.json.simple.JSONObject(jsonMap);
			response.getWriter().append(res.toJSONString());
		}

	}

	/**
	 * @see HttpServlet#doPut(HttpServletRequest, HttpServletResponse)
	 */
	protected void doPut(HttpServletRequest request, HttpServletResponse response) {
		//==================================
		// PUT requests to update password
		// If it runs into an error it returns error else returns Success if everything went ok
        //==================================
		Map<String, String> jsonMap = new HashMap<>();
		org.json.simple.JSONObject res;

		org.json.JSONObject bodyParams = getBodyParams(request);
		String mail = null;
		String newPassword = null;
		try {
			mail = bodyParams.get("email").toString();
			newPassword = bodyParams.get("newPassword").toString();
		} catch (JSONException e1) {
			e1.printStackTrace();
		}

		assert mail != null;
		if (newPassword == null || !UsersManager.updatePassword(mail, newPassword))
			jsonMap.put("error", "could not update password");
		else
			jsonMap.put("Success", "Success");

		res = new org.json.simple.JSONObject(jsonMap);
		try {
			response.getWriter().append(res.toJSONString());
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	// ----------------------------------------Other----------------------------------------

	protected boolean checkParameters(org.json.JSONObject bodyParams, HttpServletResponse response) throws IOException {

		Map<String, String> jsonMap = new HashMap<>();
		org.json.simple.JSONObject res;

		Iterator paramsObjects = bodyParams.keys();
		ArrayList<String> values = new ArrayList<>();

		while (paramsObjects.hasNext()) {
			Object paramsObject = paramsObjects.next();
			try {
				if (bodyParams.get((String) paramsObject) != null
						&& !bodyParams.get((String) paramsObject).toString().isBlank()) {
					values.add(bodyParams.get((String) paramsObject).toString());
				} else {
					values.add(null);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		if (values.contains(null) || values.isEmpty()) {
			jsonMap.put("Error", "Missing some fields");
			res = new org.json.simple.JSONObject(jsonMap);
			response.getWriter().append(res.toJSONString());
			return false;
		}
		return true;
	}

	public static HashMap<String, String> checkParameters(org.json.JSONObject bodyParams) {
		Iterator paramsKeys = bodyParams.keys();
		ArrayList<String> paramsObjects = new ArrayList<>();
		while (paramsKeys.hasNext()) {
			paramsObjects.add(paramsKeys.next().toString());
		}
		HashMap<String, String> val = new HashMap<>();

        for (String paramsObject : paramsObjects) {
            try {
                if (bodyParams.get( paramsObject) != null && !bodyParams.get(paramsObject).toString().isBlank())
                    val.put( paramsObject, bodyParams.get( paramsObject).toString());

                else
                    val.put("null", "null");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

		if (val.containsKey("null"))
			return null;
		return val;

	}

	static JSONObject getBodyParams(HttpServletRequest request) {
		StringBuilder sb = new StringBuilder();
		String line;
		JSONObject json = null;

		BufferedReader reader;
		try {
			reader = request.getReader();
			while ((line = reader.readLine()) != null)
				sb.append(line);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			json = new JSONObject(sb.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}
}