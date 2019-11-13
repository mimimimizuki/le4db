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

//cordinatorが削除(patient)
//donor が削除(donor)
@SuppressWarnings("serial")
public class DeleteDonorServlet extends HttpServlet {
    // donor can delete his infomation
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

        String deleteuser_data = request.getParameter("delete_user_id");

        out.println("<html>");
        out.println("<body>");
        out.println("<style>");
        out.println("body {color : dimgray; }");
        out.println("</style>");
        Connection conn = null;
        Statement stmt = null;
        Statement stmt_hla = null;
        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection("jdbc:postgresql://" + _hostname + ":5432/" + _dbname, _username,
                    _password);
            conn.setAutoCommit(false);
            stmt = conn.createStatement();

            stmt.executeUpdate("DELETE FROM relationship WHERE user_id ='" + deleteuser_data + "'");

            stmt.executeUpdate("DELETE FROM address WHERE user_id ='" + deleteuser_data + "'");
            stmt.executeUpdate("DELETE FROM contact WHERE user_id ='" + deleteuser_data + "'");
            stmt.executeUpdate("DELETE FROM login WHERE user_id ='" + deleteuser_data + "'");
            ResultSet rs = stmt.executeQuery("SELECT * FROM register WHERE user_id ='" + deleteuser_data + "'");
            rs.next();
            int hla_id = rs.getInt("hla_id");
            stmt.executeUpdate("DELETE FROM register WHERE user_id= '" + deleteuser_data + "'");
            stmt.executeUpdate("DELETE FROM user_data WHERE user_id= '" + deleteuser_data + "'");
            rs.close();
            stmt_hla = conn.createStatement();
            ResultSet rs_hla = stmt_hla.executeQuery("SELECT * FROM register WHERE hla_id ='" + hla_id + "'");
            String same_user = "";
            if (rs_hla.next()) {
                same_user = rs_hla.getString("user_id");
            }
            if (same_user == "") {
                stmt_hla.executeUpdate("DELETE FROM hla WHERE hla_id = '" + hla_id + "'");
            }
            rs_hla.close();
            conn.commit();
            out.println("<h3>以下のユーザを削除しました。</h3><br/>");
            out.println("ユーザID: " + deleteuser_data + "<br/>");

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
        out.println("<a href=\"donor.html?user_id=" + deleteuser_data + "\">" + "前ページに戻る" + "</a>");

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
