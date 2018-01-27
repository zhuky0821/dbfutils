package dbfutils;

import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

public class DbUtils {
	public static Connection getConn(){
		Connection conn = null;
		
		try {
			
			File file = ConfigRead.readConfig("oracle.properties");
			Properties pass = new Properties();
			pass.load(new FileReader(file));
			
			String url = pass.getProperty("url");
			String user = pass.getProperty("user");
			String passwd = pass.getProperty("passwd");
			
			Class.forName("oracle.jdbc.driver.OracleDriver");
			conn = DriverManager.getConnection(url, user, passwd);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return conn;
	}
	
	public static void closeConn(Connection conn){
		try {
			if(conn != null)
				conn.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	public static void closePreStatement(PreparedStatement pst){
		try {
			if(pst != null)
				pst.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	public static void closeResultset(ResultSet rs){
		try {
			if(rs != null)
				rs.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}
