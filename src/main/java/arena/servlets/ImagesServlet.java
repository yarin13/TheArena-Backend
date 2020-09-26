package arena.servlets;

import arena.bll.ImagesManager;
import arena.bll.Users;
import arena.bll.UsersManager;
import arena.dal.DBManager;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;

@WebServlet("/ImagesServlet")
public class ImagesServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JSONObject params = getBodyParams(request);
        String email = params.getString("email");
        response.setContentType("image/jpeg");
        OutputStream os = response.getOutputStream();
        try {
            ImagesManager.getAllImages(request,response,email,os);
        } catch (Exception throwables) {
            throwables.printStackTrace();
        }


    }

    //---------------------------other---------------------------

    protected JSONObject getBodyParams(HttpServletRequest request) {
        // ============================================================================
        //	this function extracts to body of the request
        //  and returns it as JSONObject
        //============================================================================
        StringBuilder sb = new StringBuilder();
        String line;
        JSONObject json = null;

        BufferedReader reader;
        try {
            reader = request.getReader();
            while ((line = reader.readLine()) != null)
                sb.append(line);
            json = new JSONObject(sb.toString());
        } catch (Exception e1) {

            e1.printStackTrace();
        }
        return json;
    }
}
