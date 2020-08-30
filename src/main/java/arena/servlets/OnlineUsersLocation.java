package arena.servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;

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
	private static JSONArray jsonResponse = new JSONArray();
 
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
//=====================================================    
//    added synchronized because of the jsonResponse
//	  probably can be removed if we create the jsonResponse locally
//    Headers to pass:"lat","lng","mail"
//	  The get also updated the user current location in the DB    
//=====================================================    
    
	protected synchronized void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String lat = request.getHeader("lat");
		String lng = request.getHeader("lng");
		String mail = request.getHeader("mail");
		
		LocationManager.updateUsersStatus(mail, lat, lng);
		
		jsonResponse.clear();
		LocationManager.getOnlineUsersLocation(lat,lng);
		
        if (!LocationManager.location.isEmpty()) {
            jsonResponse.add(LocationManager.json);
            response.getWriter().append(jsonResponse.toJSONString());
        } else {
            jsonResponse.add(null);
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
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String mail = request.getParameter("email");
		LocationManager.logOutUser(mail);
		//doGet(request, response);
	}

}
