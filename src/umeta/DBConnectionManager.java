package umeta;



import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;



public class DBConnectionManager {
	
	private static Connection connection = null;
	private static String user = null;
	private static String password = null;
	private static String url = null;
	
	static public void configConnection(String dbUrl, String userName, String passw){
		url = dbUrl;
		password = passw;
		user = userName;
	}

    static public Connection getConnection() throws SQLException, ClassNotFoundException 
    {
    	if (url == null)
    		throw new IllegalArgumentException("data base url is not configured");
    	
    	if (password == null)
    		throw new IllegalArgumentException("password is not configured");
    	
    	if (user == null)
    		throw new IllegalArgumentException("user is not configured");
    	
    	
        if (connection != null) 
        	return connection;
        Class.forName("org.postgresql.Driver");
             connection = null;
               connection = DriverManager.getConnection(
                  url,user, password);
        return connection;
    }
}