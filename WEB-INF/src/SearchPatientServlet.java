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
public class SearchPatientServlet extends HttpServlet {
	// コーディネータがユーザIDと一致する人の連絡先などを知るために検索する <-
	// patientが自分と一致するHLA型の人を探すために検索する
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

		// String searchId = request.getParameter("search_user_id");
		int searchA = Integer.parseInt(request.getParameter("search_A"));
		int searchB = Integer.parseInt(request.getParameter("search_B"));
		int searchC = Integer.parseInt(request.getParameter("search_C"));
		int searchDR = Integer.parseInt(request.getParameter("search_DR"));

		out.println("<html>");
		out.println("<body>");

		out.println("<h3>適合するドナー一覧</h3>");
		Connection conn = null;
		Statement stmt = null;
		try {
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection("jdbc:postgresql://" + _hostname + ":5432/" + _dbname, _username,
					_password);
			stmt = conn.createStatement();

			ResultSet rs = stmt.executeQuery("SELECT * FROM (select * from hla natural join register) AS FOO WHERE a = "
					+ searchA + " and b = " + searchB + " and c = " + searchC + " and dr = " + searchDR
					+ " and patient_or_donor = 'D'");
			if (rs.next() == false) {
				out.println("適合するドナーは見つかりませんでした。");
			}
			out.println(
					"<table border=\"1\" width=\"500\" cellspacing=\"0\" cellpadding=\"5\" bordercolor=\"#333333\">");
			out.println(
					"<tr><th bgcolor=\"gray\"><font color=\"#FFFFFF\">hla_id</th><th bgcolor=\"gray\"><font color=\"#FFFFFF\">user_id</th><th bgcolor=\"gray\"><font color=\"#FFFFFF\">A</th><th bgcolor=\"gray\"><font color=\"#FFFFFF\">B</th><th bgcolor=\"gray\"><font color=\"#FFFFFF\">C</th><th bgcolor=\"gray\"><font color=\"#FFFFFF\">DR</th></tr>");
			while (rs.next()) {
				int hla_id = rs.getInt("hla_id");
				int user_id = rs.getInt("user_id");
				int A = rs.getInt("a");
				int B = rs.getInt("b");
				int C = rs.getInt("c");
				int DR = rs.getInt("dr");

				out.println("<tr>");
				out.println("<td>" + hla_id + "</td>");
				out.println("<td>" + user_id + "</td>");
				out.println("<td>" + A + "</td>");
				out.println("<td>" + B + "</td>");
				out.println("<td>" + C + "</td>");
				out.println("<td>" + DR + "</td>");
				out.println("</tr>");
			}
			rs.close();

			out.println("</table>");

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
		out.println("<a href=\"index.html\">検索ページに戻る</a>");

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
