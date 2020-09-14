package arena.servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;

import arena.bll.PhotosManager;





/**
 * Servlet implementation class Test
 */
@WebServlet("/Test")
@MultipartConfig
public class Test extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Test() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//		 System.out.println("testing");
//	   	Collection<Part> parts = request.getParts();
//		
//	       for (Part filePart2 : parts) {
//
//	    	   System.out.println(filePart2.getName());
//	           
//	           
//	       }
		 System.out.println("testing");
	        JSONObject params = getBodyParams(request);
	  	    Map<String,String> jsonMap = new HashMap<>();
	  	    org.json.simple.JSONObject res ;
	  	    
			try {
				String mail = params.getString("userEmail");
				System.out.println(mail);
			} catch (JSONException e1) {
			System.out.println("error");
			}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	
	 public void doPost(HttpServletRequest request, HttpServletResponse response)
	 throws ServletException, IOException {
	 
		 StringBuilder sb = new StringBuilder();
		 String line = null;
			 
		 BufferedReader reader = request.getReader();
			  while ((line = reader.readLine()) != null) {
				  sb.append(line);
			  }
			  try {
				  
				JSONObject json = new JSONObject(sb.toString());
				
				System.out.println(json);
				System.out.println(json.get("mail"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 

}
	
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

