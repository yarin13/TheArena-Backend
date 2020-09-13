package arena.dal;
import arena.bll.Users;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public final class DBManager {
	
	private final static String USERNAME = "root";
	private final static String PASSWORD = null;
	private final static String URL = "jdbc:mysql://localhost:3306/TheArena?useSSL=false";
	
	private static Connection connection = null;
	
	//-----------------------------void-----------------------------
	
	public static void closeConnection() {
		/*
		 * this function is responsible to close the connection to the Database
		 */
		if (connection != null)
			try {
				connection.close();
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}

	}
	
	public static void runSelect(String query, Consumer<ResultSet> con) {
		/*
		 * this function get a query and functional interface,
		 *  and get the data back to the functional interface.
		 */
		Statement statement = null;

		try {
			connection = DBManager.getConnection();			 //initializing connection
			statement = connection.createStatement();
			ResultSet res = statement.executeQuery(query);
			while(res.next()) {
				con.accept(res);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (statement != null)
				try {
					statement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
	}
	
	//-----------------------------Connection-----------------------------
	
	public static Connection getConnection() {
		/*
		 * this function is initialize the connection 
		 */
		try {
			Class.forName("com.mysql.jdbc.Driver");
			return connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return null;
		} 
	}
	
	//-----------------------------int-----------------------------
	
	public static int isExists(String query) {
		/*
		 * this function that get a query and check if there are any result,
		 * if there is any result it will return 1,
		 * else
		 * it will return 0.
		 * 
		 * In case of exception it will return -1.
		 */
		Statement statement = null;
		
		try {
			connection = DBManager.getConnection(); 		//initializing connection
			statement = connection.createStatement();
			ResultSet res=statement.executeQuery(query);
			
			if(res.next()) {
				res.close();
				return 1;
			}else {
				res.close();
				return 0;
			}
			
		}
		catch(SQLException e) {
			e.printStackTrace();
			
			return -1;
		} finally {
			if (statement != null)
				try {
					
					statement.close();
				} catch (SQLException e) {
					e.printStackTrace();
					
				}
		}
		
	}
	
	public static int runExecute(String query) {
		/*
		 * this function get a query and return the number of row in the result,
		 * mostly used on insert , update or delete queries
		 */
		PreparedStatement statement = null;

		try {
			connection = DBManager.getConnection();			 //initializing connection 
			statement = connection.prepareStatement(query);
			return statement.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (statement != null)
				try {
					statement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		return -1;
	}
	
	
	
	public static boolean insertImage(int userId,InputStream image) {
		/*
		 * this function get a query and return the number of row in the result,
		 * mostly used on insert , update or delete queries
		 */
		PreparedStatement pstmt = null;

		String query = "INSERT INTO usersPhotos(userId,photo) VALUES (?, ?)";
		
		try {
			connection = DBManager.getConnection();			 //initializing connection 
			pstmt = connection.prepareStatement(query);
			pstmt.setInt(1, userId);
		    pstmt.setBinaryStream(2, image);
		    System.out.println("testing");
		    return pstmt.execute();
		      
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (pstmt != null)
				try {
					pstmt.close();
				
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		return false;
	}

	public static FileOutputStream selectImage(String query,OutputStream os) throws FileNotFoundException {
		Statement pstmt = null;
	Blob image = null;
    byte[] imgData = null;
 
   int i;
   ResultSet rs =null;

   try {
	   connection = DBManager.getConnection();			 //initializing connection 
	   pstmt = connection.createStatement();
	   rs =  pstmt.executeQuery(query);
    
	   if (rs.next()) {
              image = rs.getBlob("photo");//getting image from database 
              imgData = image.getBytes(1,(int)image.length()); //extra info about image
              
       	   	  os.write(imgData);//sending the image 
       	   	  
            } 
	   os.flush();
	   os.close();

   }
   catch(Exception e)
     {
         e.printStackTrace();

     }
return null;
}
	
//	public static FileOutputStream selectImage(String query,OutputStream os) throws FileNotFoundException {
//		ResultSet result = null;
//		Statement pstmt = null;
//		
//		InputStream input = null;
//		FileOutputStream output = null;
//		try {
//			connection = DBManager.getConnection();			 //initializing connection 
//			pstmt = connection.createStatement();
//			result =  pstmt.executeQuery(query);
//			
//			File image = new File("userPhotos");
//			output = new FileOutputStream(image);
//		      
//			if(result.next()) {
//				input = result.getBinaryStream("photo");
//				
//				byte[] buffer = new byte[1024];
//				while(input.read(buffer) > 0) {
//					output.write(buffer);
//					os.write(buffer);
//				}
//				return output;
//				
//			}
//			
//		} catch (SQLException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} finally {
//			if (pstmt != null)
//				try {
//					pstmt.close();
//				} catch (SQLException e) {
//					e.printStackTrace();
//				}
//		}
//		return null;
//	}
	
	
	//---------------------------------------------------------Users---------------------------------------------------------
	
	public static Users getUserInfo(String query) {
		/*
		 * this function get a query and return a User object
		 */
		Statement statement = null;
		
		try {
			connection = DBManager.getConnection();			 //initializing connection
			statement = connection.createStatement();
			ResultSet res=statement.executeQuery(query);
			
			if(res.next()) {
				return new Users(res.getString("email"),res.getString("firstName"),res.getString("lastName"),res.getString("phoneNumber")
						,res.getInt("age"),res.getString("gender"),res.getString("interestedin"),res.getInt("score"));
			}
		}catch (SQLException e ) {
	        System.out.println("e: "+e);
	        return null;
	        
	    } finally {
	        if (statement != null) { closeConnection();}
	    }
		return null;
	}
	
	public static Users getUserInfoWithId(String query) {
		/*
		 * this function get a query and return a User object including UserId.
		 */
		Statement statement = null;
		
		try {
			connection = DBManager.getConnection();			 //initializing connection
			statement = connection.createStatement();
			ResultSet res=statement.executeQuery(query);
			
			if(res.next()) {
				return new Users(res.getInt("id"),res.getString("email"),res.getString("firstName"),res.getString("lastName"),res.getString("phoneNumber")
						,res.getInt("age"),res.getString("gender"),res.getString("interestedin"),res.getInt("score"));
			}
		}catch (SQLException e ) {
	        System.out.println("e: "+e);
	        return null;
	        
	    } finally {
	        if (statement != null) { closeConnection();}
	    }
		return null;
	}

}