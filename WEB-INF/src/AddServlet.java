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

//ドナーが自分の情報を追加する
@SuppressWarnings("serial")
public class AddServlet extends HttpServlet {

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

		String addName = request.getParameter("add_name");
		String addage = request.getParameter("add_age");
		String addA = request.getParameter("add_A");
		String addB = request.getParameter("add_B");
		String addC = request.getParameter("add_C");
		String addDR = request.getParameter("add_DR");
		String addAddress = request.getParameter("add_address");
		String addprefecture = request.getParameter("add_prefecture");
		String addrelationship = request.getParameter("add_relationship");
		int loginer = Integer.parseInt(request.getParameter("user_id"));

		out.println("<html>");
		out.println("<body>");

		Connection conn = null;
		Statement stmt = null;
		Statement stmt_fam = null;
		int max_user_data = 0;
		try {
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection("jdbc:postgresql://" + _hostname + ":5432/" + _dbname, _username,
					_password);
			stmt = conn.createStatement();

			ResultSet rs = stmt.executeQuery("SELECT MAX(user_id) AS max_user FROM user_data");
			while (rs.next()) {
				max_user_data = rs.getInt("max_user");// 53
			}
			rs.close();

			int adduser_data = max_user_data + 1;// 54
			stmt.executeUpdate(
					"INSERT INTO user_data VALUES( '" + adduser_data + "', '" + addName + "', '" + addage + "')");
			// stmt.executeUpdate("INSERT INTO hla VALUES(" + addA + ", '" + addB + ",'" +
			// addC + ",'" + addDR + ")");
			stmt.executeUpdate(
					"INSERT INTO contact VALUES( '" + addAddress + "', '" + adduser_data + "', '" + addName + "')");
			stmt.executeUpdate("INSERT INTO address VALUES( '" + adduser_data + "', '" + addAddress + "', '"
					+ addprefecture + "')");

			out.println("以下のユーザを追加しました。<br/><br/>");
			out.println("ユーザID: " + adduser_data + "<br/>");
			out.println("名前: " + addName + "<br/>");
			out.println("年齢: " + addage + "<br/>");
			out.println("居場所: " + addprefecture + "<br/>");
			out.println("電話番号: " + addAddress + "<br/>");
			stmt_fam = conn.createStatement();
			ResultSet rs_fam = stmt_fam.executeQuery("SELECT * FROM relationship WHERE user_id = '" + loginer + "'");
			rs_fam.next();
			int fam_id = rs_fam.getInt("family_id");
			stmt_fam.executeUpdate("INSERT INTO relationship VALUES( '" + adduser_data + "', '" + fam_id + "', '"
					+ addrelationship + "')");
			rs_fam.close();
			out.println("家族ID: " + addrelationship + "<br/>");
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
		out.println("<a href=\"donor.html\">前ページへ戻る</a>");

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
