package arena.servlets;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import arena.bll.PhotosManager;

/**
 * Servlet implementation class Test
 */
@WebServlet("/Test")
@javax.servlet.annotation.MultipartConfig
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
		// TODO Auto-generated method stub
//		request.getParts()
		
		PhotosManager.selectPhoto(request.getParameter("mail"));
		
		
		response.getWriter().append(PhotosManager.json.toJSONString());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		   	Collection<Part> parts = request.getParts();
//		      if (parts.size() != 3) {
//		         //can write error page saying all details are not entered
//		       }

		   	String mail = request.getParameter("mail");
		   	
		   	
		       Part filePart = request.getPart("photo");
		       InputStream imageInputStream = filePart.getInputStream();
		       //String testing = imageInputStream.toString();
		       PhotosManager.insertPhoto(mail,imageInputStream);
		       //read imageInputStream
		  //     filePart.write("somefiepath");
		       //can also write the photo to local storage

		       //Read Name, String Type 
//		       Part namePart = request.getPart("name");
//		       if(namePart.getSize() > 20){
//		           //write name cannot exceed 20 chars
//		       }
		       //use nameInputStream if required        
//		       InputStream nameInputStream = namePart.getInputStream();
		       //name , String type can also obtained using Request parameter 
//		       String nameParameter = request.getParameter("name");
		       //Similialrly can read age properties
//		       Part agePart = request.getPart("age");
//		       int ageParameter = Integer.parseInt(request.getParameter("age"));
	}

}
