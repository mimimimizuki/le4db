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
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// donor が自分の情報を追加
@SuppressWarnings("serial")
@WebServlet("/DonorServlet")
public class DonorServlet extends HttpServlet {

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
		request.setCharacterEncoding("UTF8");
		out.println("<html>");
		out.println("<body>");

		out.println("<form action=\"add\" method=\"GET\">");
		out.println("A： ");
		out.println("<input type=\"text\" name=\"add_a\" />");
		out.println("<br/>");
		out.println("B： ");
		out.println("<input type=\"text\" name=\"add_b\" />");
		out.println("<br/>");
		out.println("C： ");
		out.println("<input type=\"text\" name=\"add_c\" />");
		out.println("<br/>");
		out.println("DR： ");
		out.println("<input type=\"text\" name=\"add_dr\" />");
		out.println("<br/>");
		out.println("氏名： ");
		out.println("<input type=\"text\" name=\"add_name\" />");
		out.println("<br/>");
		out.println("年齢： ");
		out.println("<input type=\"text\" name=\"add_age\" />");
		out.println("<br/>");
		out.println("連絡先： ");
		out.println("<input type=\"text\" name=\"add_address\" />");
		out.println("<br/>");
		out.println("居場所： ");
		out.println("<input type=\"text\" name=\"add_prefecture\" />");
		out.println("<br/>");

		out.println("<input type=\"submit\" value=\"追加\"/>");

		out.println("<form action=\"update\" method=\"GET\">");
		out.println("連絡先： ");
		out.println("<input type=\"text\" name=\"update_tel\" />");
		out.println("<br/>");
		out.println("居場所： ");
		out.println("<input type=\"text\" name=\"update_prefecture\" />");
		out.println("<br/>");
		out.println("<input type=\"submit\" value=\"更新\"/>");

		out.println("<form action=\"delete\" method=\"GET\">");
		out.println("ユーザ名： ");
		out.println("<input type=\"text\" name=\"delete_user_data\" />");
		out.println("<br/>");
		out.println("<input type=\"submit\" value=\"削除\"/>");

		out.println("</html>");
		out.println("</body>");

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

	public void destroy() {
	}

}
