package arena.servlets;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import arena.bll.PhotosManager;

/**
 * Servlet implementation class Test
 */
@WebServlet("/PhotosServlet")
//@javax.servlet.annotation.MultipartConfig
@MultipartConfig
public class PhotosServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public PhotosServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
//	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//		// TODO Auto-generated method stub
////		request.getParts()
//		
//		PhotosManager.selectPhoto(request.getParameter("mail"),response);
//		response.setContentType(((ServletRequest) PhotosManager.photos).getContentType());
//		response.getWriter().append(PhotosManager.json.toJSONString());
//		
//		
//		ServletContext sc = getServletContext();
//
//	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		   	Collection<Part> parts = request.getParts();

		   	String mail = request.getParameter("mail");
		   	
		   	
//		       Part filePart = request.getPart("photo");
//		       InputStream imageInputStream = filePart.getInputStream();
//		       //String testing = imageInputStream.toString();
//		       PhotosManager.insertPhoto(mail,imageInputStream);


		       List<Part> fileParts = (List<Part>) request.getParts();

		       for (Part filePart2 : parts) {
		           InputStream fileContent = filePart2.getInputStream();
		           if(!filePart2.getName().equals("mail"))
		        	   PhotosManager.insertPhoto(mail,fileContent);
		           
		           
		       }
		       

	}
	
	
	
	
	
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        ServletContext sc = getServletContext();

  //      try (InputStream is = sc.getResourceAsStream(PhotosManager.selectPhoto(request.getParameter("mail")))) {

            // it is the responsibility of the container to close output stream
            OutputStream os = response.getOutputStream();

//            if (is == null) {
//
//                response.setContentType("text/plain");
//                os.write("Failed to send image".getBytes());
//            } else {

                response.setContentType("image/jpeg");
                PhotosManager.selectPhoto(request.getParameter("mail"),os);
                byte[] buffer = new byte[1024];
                int bytesRead;

           //     while ((bytesRead = is.read(buffer)) != -1) {

           //         os.write(buffer, 0, bytesRead);
           //     }
         //   }
 //       }
    }
}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	


