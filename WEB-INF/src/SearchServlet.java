import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class SearchServlet extends HttpServlet {
	// コーディネータがユーザIDと一致する人の連絡先などを知るために検索する <- cordinator
	// patientが自分と一致するHLA型の人を探すために検索する(SearchPatient)
	private String _hostname = null;
	private String _dbname = null;
	private String _username = null;
	private String _password = null;

	public void init() throws ServletException {
		// iniファイルから自分のデータベース情報を読み込む
		String iniFilePath = getServletConfig().getServletContext().getRealPath("WEB-INF/le4db.ini");
		try {
			FileInputStream fis = new FileInputStream(iniFilePath);
			Properties prop = new Properties();
			prop.load(fis);
			_hostname = prop.getProperty("hostname");
			_dbname = prop.getProperty("dbname");
			_username = prop.getProperty("username");
			_password = prop.getProperty("password");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();

		String searchID = request.getParameter("search_user_id");

		out.println("<html>");
		out.println("<body>");
		out.println("<style>");
		out.println("body {color : dimgray; }");
		out.println("</style>");

		out.println("<h3>ドナー情報</h3>");
		Connection conn = null;
		Statement stmt = null;
		try {
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection("jdbc:postgresql://" + _hostname + ":5432/" + _dbname, _username,
					_password);
			stmt = conn.createStatement();

			out.println(
					"<table border=\"1\" width=\"500\" cellspacing=\"0\" cellpadding=\"5\" bordercolor=\"#333333\">");
			out.println(
					"<tr><th bgcolor=\"gray\"><font color=\"#FFFFFF\">user_id</th><th bgcolor=\"gray\"><font color=\"#FFFFFF\">氏名</th><th bgcolor=\"gray\"><font color=\"#FFFFFF\">年齢</th><th bgcolor=\"gray\"><font color=\"#FFFFFF\">連絡先</th><th bgcolor=\"gray\"><font color=\"#FFFFFF\">居場所</th></tr>");
			// ResultSet rs = stmt.executeQuery("SELECT * FROM address");
			ResultSet rs = stmt.executeQuery(
					"SELECT * FROM (user_data NATURAL JOIN address) AS FOO WHERE user_id = '" + searchID + "'");

			while (rs.next()) {
				int user_id = rs.getInt("user_id");
				String name = rs.getString("name");
				int age = rs.getInt("age");
				int tel = rs.getInt("tel");
				String prefecture = rs.getString("prefecture");

				out.println("<tr>");
				out.println("<td><a href=\"item?user_id=" + user_id + "\">" + user_id + "</a></td>");
				out.println("<td>" + name + "</td>");
				out.println("<td>" + age + "</td>");
				out.println("<td>" + tel + "</td>");
				out.println("<td>" + prefecture + "</td>");
				out.println("</tr>");
			}
			rs.close();

			out.println("</table>");

		} catch (Exception e) {
			out.println("SQLException:" + e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		out.println("<br/>");
		out.println("<a href=\"cordinator.html\">検索ページに戻る</a>");
		out.println("</body>");
		out.println("</html>");
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

	public void destroy() {
	}

}
