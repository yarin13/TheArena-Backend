package arena.servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//import org.json.simple.JSONObject;
import org.json.JSONObject;

import arena.bll.Users;
import arena.bll.UsersManager;

/**
 * Servlet implementation class UserServlet
 */
@WebServlet("/UserServlet")
public class UserServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	
	
	/* -------------------------------------------UserServlet---------------
	 * http://localhost:8080/TheArenaServlet/UserServlet
	 */
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UserServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		HashMap<String,String> userDetails = new HashMap<>();
		try {
			String header = request.getHeader("userId");
			if (header.equals("") || header.isEmpty() || header.isBlank())
				throw new Exception();

			int userId = Integer.parseInt(header);

			Users currentUser = UsersManager.returnUserId(userId);
			if(currentUser != null)
			{
				userDetails.put("email", currentUser.getEmail());
				userDetails.put("firstName", currentUser.getFirstName());
				userDetails.put("lastName", currentUser.getLastName());
				userDetails.put("phoneNumber", currentUser.getPhoneNumber());
				userDetails.put("age", String.valueOf(currentUser.getAge()));
				userDetails.put("gender", currentUser.getGender());
				userDetails.put("interestedIn", currentUser.getInterestedIn());

				response.setStatus(200);
			}
			else
				throw new Exception();

		}
		catch (Exception e){
			response.setStatus(400);
			userDetails.put("Error", "Couldn't find user");
		}finally {
			response.getWriter().append(new JSONObject(userDetails).toString());
		}
	}
	
	 protected void doPost(HttpServletRequest request, HttpServletResponse response)
	            throws ServletException, IOException {
		 JSONObject params = getBodyParams(request);
		 HashMap<String,String> resault = new HashMap<>();
		 int userId = Integer.parseInt(params.getString("userId"));
		 
		 if(params.has("firstName"))
		 {
			 String firstName = params.getString("firstName").toLowerCase();
			 if(UsersManager.updateFirstName(userId,firstName) != -1)
			 {
				 resault.put("Success", "Success");			 
			 }
			 else
			 {
				 resault.put("firstNameError", "firstName");
			 }
		 }
		 if(params.has("lastName"))
		 {
			 String lastName = params.getString("lastName").toLowerCase();
			 if(UsersManager.updateLastName(userId,lastName) != -1)
			 {
				 resault.put("Success", "Success");
			 }
			 else
			 {
				 resault.put("lastNameError", "lastName");
			 }
		 }
		 if(params.has("phoneNumber"))
		 {
			 String phoneNumber = params.getString("phoneNumber");
			 if(UsersManager.updatePhoneNumber(userId,phoneNumber) != -1)
			 {
				 resault.put("Success", "Success");
			 }
			 else
			 {
				 resault.put("phoneNumberError", "phoneNumber");
			 }
			 
		 }
		 if(params.has("age"))
		 {
			 int age = Integer.parseInt(params.getString("age"));
			 if(UsersManager.updateAge(userId,age) != -1)
			 {
				 resault.put("Success", "Success");
			 }
			 else
			 {
				 resault.put("ageError", "age");
			 }
		 }
		 if(params.has("gender"))
		 {
			 String gender = params.getString("gender").toLowerCase();
			 if(UsersManager.updateGender(userId,gender) != -1)
			 {
				 resault.put("Success", "Success");
			 }
			 else
			 {
				 resault.put("genderError", "gender");
			 }
		 }
		 if(params.has("interestedIn"))
		 {
			 String interestedIn = params.getString("interestedIn").toLowerCase();
			 if(UsersManager.updateInterestedIn(userId,interestedIn) != -1)
			 {
				 resault.put("Success", "Success");
			 }
			 else
			 {
				 resault.put("interestedInError", "interestedIn");
			 }
		 }
		 if(params.has("email"))
		 {
			 String email = params.getString("email").toLowerCase();
			 if(UsersManager.updateUserEmail(userId,email) != -1)
			 {
				 resault.put("Success", "Success");
			 }
			 else
			 {
				 resault.put("emailError", "email");
			 }
			 
		 }
		 response.getWriter().append(new JSONObject(resault).toString());
	 }
	 
	 
		protected JSONObject getBodyParams(HttpServletRequest request) {
			//============================================================================
			//	this function extracts to body of the request
			//  and returns it as JSONObject
			//============================================================================
			StringBuilder sb = new StringBuilder();
			String line = null;
			JSONObject json = null;

			BufferedReader reader;
			try {
				reader = request.getReader();
				while ((line = reader.readLine()) != null)
					sb.append(line);
				json = new JSONObject(sb.toString());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			return json;

		}

}
