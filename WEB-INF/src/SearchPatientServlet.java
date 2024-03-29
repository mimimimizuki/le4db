import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class SearchPatientServlet extends HttpServlet {
	// コーディネータがユーザIDと一致する人の連絡先などを知るために検索する
	// patientが自分と一致するHLA型の人を探すために検索する<-(12,3,5,21)
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
		int loginer = Integer.parseInt(request.getParameter("user_id"));
		ArrayList<Integer> users = new ArrayList<>();
		out.println("<html>");
		out.println("<body>");
		out.println("<style>");
		out.println("body {color : dimgray; }");
		out.println("</style>");

		out.println("<h2 style=\"color : dimgray\">適合するドナー一覧</h2>");
		Connection conn = null;
		Statement stmt0 = null;
		Statement stmt = null;
		Statement stmt1 = null;
		Statement stmt2 = null;
		Statement stmt3 = null;
		Statement stmt4 = null;
		Statement stmt5 = null;
		Statement stmt_new = null;
		int loginer_fam = 0;
		try {
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection("jdbc:postgresql://" + _hostname + ":5432/" + _dbname, _username,
					_password);
			stmt0 = conn.createStatement();
			ResultSet rs0 = stmt0.executeQuery("SELECT family_id FROM relationship WHERE user_id = '" + loginer + "'");
			if (rs0.next()) {
				loginer_fam = rs0.getInt("family_id");
			}
			rs0.close();
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM (SELECT * FROM hla NATURAL JOIN register) AS FOO WHERE a = "
					+ searchA + " and b = " + searchB + " and c = " + searchC + " and dr = " + searchDR
					+ " and patient_or_donor = 'D'");
			out.println(
					"<table border=\"1\" width=\"500\" cellspacing=\"0\" cellpadding=\"5\" bordercolor=\"#333333\">");
			out.println(
					"<tr><th bgcolor=\"gray\"><font color=\"#FFFFFF\">hla_id</th><th bgcolor=\"gray\"><font color=\"#FFFFFF\">user_id</th><th bgcolor=\"gray\"><font color=\"#FFFFFF\">A</th><th bgcolor=\"gray\"><font color=\"#FFFFFF\">B</th><th bgcolor=\"gray\"><font color=\"#FFFFFF\">C</th><th bgcolor=\"gray\"><font color=\"#FFFFFF\">DR</th><th bgcolor=\"gray\"><font color=\"#FFFFFF\">血縁関係</th></tr>");
			while (rs.next()) {
				int hla_id = rs.getInt("hla_id");
				int user_id = rs.getInt("user_id");
				int A = rs.getInt("a");
				int B = rs.getInt("b");
				int C = rs.getInt("c");
				int DR = rs.getInt("dr");
				users.add(user_id);
				stmt_new = conn.createStatement();
				ResultSet rs_new = stmt_new
						.executeQuery("SELECT family_id FROM relationship WHERE user_id = '" + user_id + "'"); // 一致した人が血縁者がどうかを調べる。
				int fam_id = -1;
				if (rs_new.next()) {
					fam_id = rs_new.getInt("family_id");
				}
				rs_new.close();
				if (loginer_fam == fam_id) { // 一致した人が血縁者,一致している型だけ色をつける
					out.println("<tr>");
					out.println("<td >" + hla_id + "</td>");
					out.println("<td >" + user_id + "</td>");
					out.println("<td style=\"color : red;\">" + A + "</td>");
					out.println("<td style=\"color : red;\">" + B + "</td>");
					out.println("<td style=\"color : red;\">" + C + "</td>");
					out.println("<td style=\"color : red;\">" + DR + "</td>");
					out.println("<td>○</td>");
					out.println("</tr>");
				} else {
					out.println("<tr>");
					out.println("<td>" + hla_id + "</td>");
					out.println("<td>" + user_id + "</td>");
					out.println("<td style=\"color : red;\">" + A + "</td>");
					out.println("<td style=\"color : red;\">" + B + "</td>");
					out.println("<td style=\"color : red;\">" + C + "</td>");
					out.println("<td style=\"color : red;\">" + DR + "</td>");
					out.println("<td>×</td>");
					out.println("</tr>");
				}
			}
			rs.close();
			String execludeDR = "a = " + searchA + " and b = " + searchB + " and c = " + searchC
					+ " and patient_or_donor = 'D'";
			String execludeC = "dr = " + searchDR + " and b = " + searchB + " and c = " + searchC
					+ " and patient_or_donor = 'D'";
			String execludeB = "a = " + searchA + " and dr = " + searchDR + " and c = " + searchC
					+ " and patient_or_donor = 'D'";
			String execludeA = "a = " + searchA + " and b = " + searchB + " and dr = " + searchDR
					+ " and patient_or_donor = 'D'";

			stmt1 = conn.createStatement();
			ResultSet rs1 = stmt1
					.executeQuery("SELECT * FROM (SELECT * FROM hla NATURAL JOIN register) AS FOO WHERE " + execludeDR); // DRだけ違う
			while (rs1.next()) {
				showList(rs1, out, loginer_fam, stmt, "DR", users);
			}
			rs1.close();
			stmt2 = conn.createStatement();
			ResultSet rs2 = stmt2
					.executeQuery("SELECT * FROM (SELECT * FROM hla NATURAL JOIN register) AS FOO WHERE " + execludeC);
			while (rs2.next()) {
				showList(rs2, out, loginer_fam, stmt, "C", users);
			}
			rs2.close();
			stmt3 = conn.createStatement();
			ResultSet rs3 = stmt3
					.executeQuery("SELECT * FROM (SELECT * FROM hla NATURAL JOIN register) AS FOO WHERE " + execludeB);
			while (rs3.next()) {
				showList(rs3, out, loginer_fam, stmt, "B", users);
			}
			rs3.close();
			stmt4 = conn.createStatement();
			ResultSet rs4 = stmt4
					.executeQuery("SELECT * FROM (SELECT * FROM hla NATURAL JOIN register) AS FOO WHERE " + execludeA);
			while (rs4.next()) {
				showList(rs4, out, loginer_fam, stmt, "A", users);
			}
			rs4.close();
			// ここからは家族だけ
			stmt5 = conn.createStatement();
			ResultSet rs5 = stmt5.executeQuery(
					"SELECT * FROM (SELECT * FROM relationship NATURAL JOIN register NATURAL JOIN hla) AS FOO WHERE family_id = '"
							+ loginer_fam + "' and patient_or_donor = 'D'");
			while (rs5.next()) {
				int family_user = rs5.getInt("user_id"); // 患者の血縁者のユーザID
				int hla_id = rs5.getInt("hla_id");
				int A = rs5.getInt("a");
				int B = rs5.getInt("b");
				int C = rs5.getInt("c");
				int DR = rs5.getInt("dr");
				if (A == searchA) {// 型が一致すれば0にする
					A = 0;
				}
				if (B == searchB) {
					B = 0;
				}
				if (C == searchC) {
					C = 0;
				}
				if (DR == searchDR) {
					DR = 0;
				}
				if (A * B * C * DR == 0 && users.indexOf(family_user) == -1) { // もしどれかが0なら
					out.println("<tr>");
					out.println("<td >" + hla_id + "</td>");
					out.println("<td >" + family_user + "</td>");
					if (A == 0) {
						out.println("<td style=\"color : red;\">" + searchA + "</td>");
					} else {
						out.println("<td>" + A + "</td>");
					}
					if (B == 0) {
						out.println("<td style=\"color : red;\">" + searchB + "</td>");
					} else {
						out.println("<td>" + B + "</td>");
					}
					if (C == 0) {
						out.println("<td style=\"color : red;\">" + searchC + "</td>");
					} else {
						out.println("<td>" + C + "</td>");
					}
					if (DR == 0) {
						out.println("<td style=\"color : red;\">" + searchDR + "</td>");
					} else {
						out.println("<td>" + DR + "</td>");
					}
					out.println("<td>○</td>");
					out.println("</tr>");
				}
			}
			rs5.close();

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
		out.println("<a href=\"patient.html?user_id=" + loginer + "\">" + "検索ページに戻る" + "</a><br /><br />");
		out.println("<p>1. HLA型の適合する血縁関係者</p>");
		out.println("<p>2. HLA型の適合する非血縁者(骨髄バンクのドナー)</p>");
		out.println("<p>3. HLA型がひとつだけちがう血縁者</p>");
		out.println("<p>4. HLA型が2つか3つちがう血縁者、HLA型がひとつだけちがう非血縁者</p>");
		out.println("</body>");
		out.println("</html>");
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

	public void destroy() {
	}

	public static void showList(ResultSet rs, PrintWriter out, int loginer_fam, Statement stmt, String exclude,
			ArrayList<Integer> users) throws SQLException {
		int hla_id = rs.getInt("hla_id");
		int user_id = rs.getInt("user_id");
		int A = rs.getInt("a");
		int B = rs.getInt("b");
		int C = rs.getInt("c");
		int DR = rs.getInt("dr");
		if (users.indexOf(user_id) == -1) { // -1だったらリストusersにはまだ入っていないユーザ
			users.add(user_id);
			rs = stmt.executeQuery("SELECT family_id FROM relationship WHERE user_id = '" + user_id + "'"); // 一致した人が血縁者がどうかを調べる。
			int fam_id = -1;
			if (rs.next()) {
				fam_id = rs.getInt("family_id");
			}
			if (loginer_fam == fam_id) { // 一致した人が血縁者,一致している型だけ色をつける
				out.println("<tr>");
				out.println("<td >" + hla_id + "</td>");
				out.println("<td >" + user_id + "</td>");
				if (exclude.contains("A")) {
					out.println("<td>" + A + "</td>");
					out.println("<td style=\"color : red;\">" + B + "</td>");
					out.println("<td style=\"color : red;\">" + C + "</td>");
					out.println("<td style=\"color : red;\">" + DR + "</td>");
				} else if (exclude.contains("B")) {
					out.println("<td style=\"color : red;\">" + A + "</td>");
					out.println("<td>" + B + "</td>");
					out.println("<td style=\"color : red;\">" + C + "</td>");
					out.println("<td style=\"color : red;\">" + DR + "</td>");
				} else if (exclude.contains("C")) {
					out.println("<td style=\"color : red;\">" + A + "</td>");
					out.println("<td style=\"color : red;\">" + B + "</td>");
					out.println("<td>" + C + "</td>");
					out.println("<td style=\"color : red;\">" + DR + "</td>");
				} else {
					out.println("<td style=\"color : red;\">" + A + "</td>");
					out.println("<td style=\"color : red;\">" + B + "</td>");
					out.println("<td style=\"color : red;\">" + C + "</td>");
					out.println("<td>" + DR + "</td>");
				}
				out.println("<td>○</td>");
				out.println("</tr>");
			} else {
				out.println("<tr>");
				out.println("<td>" + hla_id + "</td>");
				out.println("<td>" + user_id + "</td>");
				if (exclude.contains("A")) {
					out.println("<td>" + A + "</td>");
					out.println("<td style=\"color : red;\">" + B + "</td>");
					out.println("<td style=\"color : red;\">" + C + "</td>");
					out.println("<td style=\"color : red;\">" + DR + "</td>");
				} else if (exclude.contains("B")) {
					out.println("<td style=\"color : red;\">" + A + "</td>");
					out.println("<td>" + B + "</td>");
					out.println("<td style=\"color : red;\">" + C + "</td>");
					out.println("<td style=\"color : red;\">" + DR + "</td>");
				} else if (exclude.contains("C")) {
					out.println("<td style=\"color : red;\">" + A + "</td>");
					out.println("<td style=\"color : red;\">" + B + "</td>");
					out.println("<td>" + C + "</td>");
					out.println("<td style=\"color : red;\">" + DR + "</td>");
				} else {
					out.println("<td style=\"color : red;\">" + A + "</td>");
					out.println("<td style=\"color : red;\">" + B + "</td>");
					out.println("<td style=\"color : red;\">" + C + "</td>");
					out.println("<td>" + DR + "</td>");
				}
				out.println("<td>×</td>");
				out.println("</tr>");
			}
		}
	}
}
