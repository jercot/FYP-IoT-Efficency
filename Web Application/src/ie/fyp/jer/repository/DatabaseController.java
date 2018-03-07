package ie.fyp.jer.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * Application Lifecycle Listener implementation class DatabaseController
 *
 */
@WebListener
public class DatabaseController implements ServletContextListener {

	private static Connection con;
	private static boolean connected;
    /**
     * Default constructor. 
     */
    public DatabaseController() {
    	
    }
    
    public static boolean isConnected() {
    	return connected;
    }

	/**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent arg0)  { 
      /*   try {
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}*/
    }

	/**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent arg0)  { 
    	/*try {
			Class.forName("org.postgresql.Driver");
		String dbName = "######";
		String userName = "######";
		String password = "######";
		String hostname = "######";
		String port = "######";
		String jdbcUrl = "jdbc:postgresql://" + hostname + ":" + port + "/" + dbName + "?user=" + userName + "&password="
				+ password;
		con = (Connection) DriverManager.getConnection(jdbcUrl);
		connected = true;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}*/
    }
    
    public static String insert(String input) {
		try {
	    	String query = "INSERT INTO public.\"Temp\" (\"string\") VALUES('" + input + "');";
			Statement compStmt = con.createStatement();
			compStmt.executeUpdate(query);
		} catch (SQLException e) {
			return e.toString();
		}
		return "Completed";
    }
    
    public static ArrayList<String> getTemp() {
    	String query = "SELECT id, string FROM public.\"Temp\";";
    	try {
    		Statement compStmt = con.createStatement();
			ResultSet rs = compStmt.executeQuery(query);
			ArrayList<String> temp = new ArrayList<>();
			while(rs.next()) {
				temp.add(rs.getString(2));
			}
			return temp;
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	return null;
    }
}
