package web.application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.servlet.ServletConfig;

import org.apache.log4j.Logger;

public class DBConnectionManager {

	private static Logger log = Logger.getLogger("file");
	
	private static String url = null;
	private static String dbUser = null;
	private static String dbPassword = null;
	private static String scriptLocation = null;
	private static String queryLocation = null;
	private static String resultQueryLocation = null;
	
	public static String getResultQueryLocation() {
		log.debug("DBConnectionManager.getResultQueryLocation: return result query location: " + resultQueryLocation);
		return resultQueryLocation;
	}
	
	public static String getScriptLocation() {
		log.debug("DBConnectionManager.getScriptLocation: return script location: " + scriptLocation);
		return scriptLocation;
	}

	public static String getQueryLocation() {
		log.debug("DBConnectionManager.getQueryLocation: return query location: " + queryLocation);
		return queryLocation;
	}

	static void setDBproperties(ServletConfig config) {
		url = config.getInitParameter("dbURL");
		dbUser = config.getInitParameter("dbUser");
		dbPassword = config.getInitParameter("dbPassword");
		scriptLocation = config.getInitParameter("script-location");
		queryLocation = config.getInitParameter("query-location");
		resultQueryLocation = config.getInitParameter("result-query-location");
		log.debug("setDBproperties: set properties -> " +
				"url: " + url + 
				", dbUser: " + dbUser + 
				", dbPassword: " + dbPassword +
				", scriptLocation: " + scriptLocation +
				", queryLocation: " + queryLocation +
				", resultQueryLocation: " + resultQueryLocation);
	}
	
	public static Connection getConnection() throws ClassNotFoundException, SQLException{
		log.debug("DBConnectionManager.getConnection: gettin connection...");
		Class.forName("oracle.jdbc.driver.OracleDriver");
		return DriverManager.getConnection(url, dbUser, dbPassword);
	}
}
