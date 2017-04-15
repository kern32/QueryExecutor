package web.application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class QueryServlet extends HttpServlet {

	private static final long serialVersionUID = 2340359477923172804L;

	private static Logger log = Logger.getLogger("file");

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		log.debug("doGet: come get request");
		List<Query> queryList = getAllQueryExamples(request);
		request.setAttribute("list", queryList);
		
		String queryName = request.getRequestURI().replace("/sql/", "");
		if (!queryName.isEmpty()) {
			log.debug("doGet: get query example by name: " + queryName);
			request.setAttribute("sql", getQueryExample(queryName));
		}
		request.getRequestDispatcher("/WEB-INF/pages/index.jsp").forward(request, response);
		log.debug("doGet: exit from get request");
	}

	private String getQueryExample(String queryName) {
		log.debug("getQueryExample: getting example of query " + queryName);
		String sqlExample = null;
		try {
			sqlExample = getSqlScript(queryName);
		} catch (ClassNotFoundException | SQLException e) {
			log.error(e.getStackTrace()[0].toString());
			e.printStackTrace();
		}
		log.debug("getQueryExample: return query: " + sqlExample);
		return sqlExample;
	}

	private String getSqlScript(String queryName) throws ClassNotFoundException, SQLException {
		log.debug("getSqlScript: getting script...");
		Connection conn = null;
		PreparedStatement statement = null;
		String queryText = null;
		try {
			conn = DBConnectionManager.getConnection();
			statement = conn.prepareStatement("SELECT \"sql\" FROM \"queries\" where \"name\" = ?");
			statement.setString(1, queryName);
			ResultSet result = statement.executeQuery();
			if (result.next()) {
				queryText = result.getString(1);
			}
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					log.error(e.getStackTrace()[0].toString());
					e.printStackTrace();
				}
			}
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
					log.error(e.getStackTrace()[0].toString());
					e.printStackTrace();
				}
			}
		}
		log.debug("getSqlScript: return script.. ");
		return queryText;
	}

	private List<Query> getAllQueryExamples(HttpServletRequest request) {
		log.debug("getAllQueryExamples: getting all query examples");
		Connection conn = null;
		Statement statement = null;

		ArrayList<Query> list = new ArrayList<>();
		try {
			conn = DBConnectionManager.getConnection();
			statement = conn.createStatement();
			ResultSet result = statement.executeQuery("SELECT \"name\" , \"link\", \"sql\" FROM \"queries\"");
			while (result.next()) {
				list.add(new Query(result.getString("name"), result.getString("link"), result.getString("sql")));
			}
		} catch (ClassNotFoundException e) {
			log.error(e.getStackTrace()[0].toString());
			e.printStackTrace();
		} catch (SQLException e) {
			log.error(e.getStackTrace()[0].toString());
			e.printStackTrace();
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					log.error(e.getStackTrace()[0].toString());
					e.printStackTrace();
				}
			}
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
					log.error(e.getStackTrace()[0].toString());
					e.printStackTrace();
				}
			}
		}
		log.debug("getAllQueryExamples: return all query examples: " + list);
		return list;
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		log.debug("doPost: come post request");
		String parameter = request.getParameter("isListOperation");
		if (Boolean.valueOf(parameter)) {
			log.debug("doPost: run query request...");
			runSql(request, response);
		} else {
			log.debug("doPost: save query request...");
			saveSql(request, response);
		}
		log.debug("doPost: exit from post request");
	}

	private void saveSql(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		log.debug("saveSql: saving query...");
		boolean isSaved = true;
		try {
			saveQueryExample(request);
		} catch (ClassNotFoundException | SQLException e) {
			isSaved = false;
			log.error(e.getStackTrace()[0].toString());
			e.printStackTrace();
		}

		PrintWriter out = response.getWriter();
		Gson gson = new Gson();
		JsonObject jsonObject = new JsonObject();

		JsonElement jsonElement = gson.toJsonTree("Query successfully saved");
		if (!isSaved) {
			jsonObject.addProperty("success", false);
		} else {
			jsonObject.addProperty("success", true);
		}

		jsonObject.add("result", jsonElement);
		log.debug(jsonObject.toString());
		out.println(jsonObject.toString());
		out.close();
		log.debug("saveSql: query saved...");
	}

	private void saveQueryExample(HttpServletRequest request) throws SQLException, ClassNotFoundException {
		log.debug("saveQueryExample: saving query in DB");
		Connection conn = null;
		PreparedStatement statement = null;
		try {
			conn = DBConnectionManager.getConnection();
			statement = conn.prepareStatement("INSERT INTO \"queries\" values (?, ?, ?)");
			statement.setString(1, request.getParameter("name").replace(" ", "-"));
			statement.setString(2, "./" + request.getParameter("name").replace(" ", "-"));
			statement.setString(3, request.getParameter("sql"));
			statement.executeUpdate();
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					log.error(e.getStackTrace()[0].toString());
					e.printStackTrace();
				}
			}
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
					log.error(e.getStackTrace()[0].toString());
					e.printStackTrace();
				}
			}
		}
		log.debug("saveQueryExample: successfully saved query in DB");
	}

	private void runSql(HttpServletRequest request, HttpServletResponse response) throws IOException {
		log.debug("runSql: running query...");
		ServletContext context = getServletConfig().getServletContext();
		String scriptlocation = DBConnectionManager.getScriptLocation();
		String querylocation = DBConnectionManager.getQueryLocation();
		String scriptPath = null;

		if (querylocation == null) {
			log.error("Query location not found. Check Servlet config parameters");
			throw new FileNotFoundException("Query location not found");
		}
		if (scriptlocation == null) {
			log.error("Bash script location not found. Check Servlet config parameters");
			throw new FileNotFoundException("Bash script location not found");
		}

		String webAppPath = context.getRealPath(File.separator);
		scriptPath = webAppPath + scriptlocation;
		log.debug("runSql: bash script under: " + scriptPath);
		File scriptFile = new File(scriptPath);

		if (scriptFile.exists()) {
			saveSqlScript(querylocation, request.getParameter("sql"));
			runSqlScript(response, scriptPath);
		} else {
			log.error("Bash script file " + scriptPath + " not found");
			throw new FileNotFoundException("Bash script file " + scriptPath + " not found");
		}
		log.debug("runSql: exit after running query");
	}

	private void saveSqlScript(String queryPath, String sql) {
		log.debug("saveSqlScript: saving sql script in file");
		try {
			PrintWriter writer = new PrintWriter(queryPath);
			writer.println(sql);
			writer.close();
		} catch (IOException e) {
			log.error(e.getStackTrace()[0].toString());
			e.printStackTrace();
		}
		log.debug("saveSqlScript: file with script successfully saved");
	}

	private void runSqlScript(HttpServletResponse response, String scriptPath) throws IOException {
		log.debug("runSqlScript: start to run query...");
		String resultSQL = getQueryResult(scriptPath);
		PrintWriter out = response.getWriter();
		Gson gson = new Gson();
		JsonObject jsonObject = new JsonObject();

		JsonElement jsonElement = gson.toJsonTree(resultSQL);
		if (resultSQL.isEmpty()) {
			jsonObject.addProperty("success", false);
		} else {
			jsonObject.addProperty("success", true);
		}
		
		jsonObject.add("result", jsonElement);
		log.debug(jsonObject.toString());
		out.println(jsonObject.toString());
		out.close();
		log.debug("runSqlScript: query successfully executed...");
	}

	private String getQueryResult(String scriptPath) throws IOException {
        String queryResult = null;
		Process process = new ProcessBuilder(scriptPath).start();
		do{
		}while (!waitTillComplete(process));
		queryResult = getFormattedSQLResult();
		log.debug("getQueryResult: return query execution result...");
		return queryResult;
	}

	private boolean waitTillComplete(Process process) {
		try {
			if(process.waitFor() == 0)
				return true;
			return false;
		} catch (InterruptedException e){
			return false;
		}
	}

	private String getFormattedSQLResult() throws IOException {
		log.debug("getFormattedSQLResult: getting formatted SQL result...");
		BufferedReader r = new BufferedReader(new FileReader(DBConnectionManager.getResultQueryLocation()));
		String line = "", resultSQL = "";
		while ((line = r.readLine()) != null) {
			if (excludeRow(line)) {
				continue;
			} else {
				resultSQL = resultSQL + line;
			}
		}
		if (r != null) {
			r.close();
		}
		log.debug("getFormattedSQLResult: return formatted SQL result: " + resultSQL);
		return resultSQL;
	}

	private boolean excludeRow(String line) {
		return line.startsWith("SQL>") || line.startsWith("<br");
	}
}
