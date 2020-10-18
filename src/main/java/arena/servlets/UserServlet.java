package arena.servlets;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;

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
			response.getWriter().append(new JSONObject(userDetails).toJSONString());
		}
	}

}
