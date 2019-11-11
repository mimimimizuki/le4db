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
public class AddAllServlet extends HttpServlet {

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
        String P_or_D = request.getParameter("patient_or_donor");

        out.println("<html>");
        out.println("<body>");

        Connection conn = null;
        Statement stmt = null;
        Statement stmt_hla = null;
        Statement stmt_max_hla = null;
        Statement stmt_login = null;
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
            String username = "";
            if (P_or_D == "P") {
                out.println("ユーザ名: patient0" + adduser_data + "<br/>");
                username = "patient0" + adduser_data;
            } else {
                out.println("ユーザ名: donor0" + adduser_data + "<br/>");
                username = "donor0" + adduser_data;
            }
            out.println("パスワード: test <br/>");
            stmt.executeUpdate("INSERT INTO login VALUES ('" + username + "', '" + adduser_data + "', 'test')"); // loginできるようにする
            rs.close();
            stmt_hla = conn.createStatement();
            ResultSet rs_hla = stmt_hla.executeQuery("SELECT * FROM hla WHERE a = " + addA + " and b = " + addB
                    + " and c = " + addC + " and dr = " + addDR);
            if (rs_hla.next()) { // 同じhla型の人はすでに登録されているなら
                int same_hla = rs_hla.getInt("hla_id");
                stmt_hla.executeUpdate(
                        "INSERT INTO register VALUES (" + same_hla + ", '" + P_or_D + "', " + adduser_data + ")");
                rs_hla.close();
            } else {
                int max_hla = 0;
                stmt_max_hla = conn.createStatement();
                ResultSet rs_max_hla = stmt_max_hla.executeQuery("SELECT MAX(hla_id) AS max_hla FROM hla");
                rs_max_hla.next();
                max_hla = rs_max_hla.getInt("max_hla");
                rs_max_hla.close();
                max_hla = max_hla + 1;
                out.println();
                stmt_max_hla.executeUpdate("INSERT INTO hla VALUES (" + max_hla + ", " + addA + ", " + addB + ", "
                        + addC + ", " + addDR + ")");
                stmt_max_hla.executeUpdate(
                        "INSERT INTO register VALUES (" + max_hla + ", '" + P_or_D + "', '" + adduser_data + "')");
            }
            stmt_fam = conn.createStatement();
            ResultSet rs_fam = stmt_fam.executeQuery("SELECT MAX(family_id) AS max_fam FROM family");
            rs_fam.next();
            int max_fam_id = rs_fam.getInt("max_fam");
            max_fam_id = max_fam_id + 1;
            String fam_name = addName.split(" ")[0];
            stmt_fam.executeUpdate("INSERT INTO family VALUES (" + max_fam_id + ", '" + fam_name + "', '" + addName + "' )");
            rs_fam.close();
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
        out.println("<a href=\"/login.html\">ログイン画面へ</a>");
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
