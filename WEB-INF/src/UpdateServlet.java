import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//donorが場所と連絡先を更新
@SuppressWarnings("serial")
public class UpdateServlet extends HttpServlet {

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

		String updateuser_data = request.getParameter("update_user_data");
		int updateTEL = Integer.parseInt(request.getParameter("update_address")); // tel
		String updatePrefecture = request.getParameter("update_prefecture"); // prefecture

		out.println("<html>");
		out.println("<body>");
		out.println("<style>");
		out.println("body {color : dimgray; }");
		out.println("</style>");

		Connection conn = null;
		Statement stmt = null;
		try {
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection("jdbc:postgresql://" + _hostname + ":5432/" + _dbname, _username,
					_password);
			stmt = conn.createStatement();
			conn.setAutoCommit(false);

			stmt.executeUpdate("UPDATE address SET tel = " + updateTEL + ", prefecture = '" + updatePrefecture
					+ "' WHERE user_id = '" + updateuser_data + "'");
			stmt.executeUpdate(
					"UPDATE contact SET tel = '" + updateTEL + "' WHERE user_id = '" + updateuser_data + "'");
			conn.commit();
			out.println("<h3>以下のユーザを更新しました。</h3><br/>");
			out.println("ユーザID: " + updateuser_data + "<br/>");
			out.println("連絡先: " + updateTEL + "<br/>");
			out.println("居場所: " + updatePrefecture + "<br/>");

		} catch (Exception e) {
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
		out.println("<a href=\"donor.html?user_id=" + updateuser_data + "\">" + "前ページに戻る" + "</a>");

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
