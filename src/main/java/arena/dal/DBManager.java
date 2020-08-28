package arena.dal;
import arena.bll.Users;

import java.sql.*;
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