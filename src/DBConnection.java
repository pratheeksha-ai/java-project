import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

public class DBConnection {
    private static Connection connection = null;
    public static Connection getConnection() {
        // simple singleton connection
        try {
            if (connection != null && !connection.isClosed()) {
                return connection;
            }

            Class.forName("com.mysql.cj.jdbc.Driver"); // load JDBC driver

            Map<String, String> env = System.getenv();
            String url = env.getOrDefault("DB_URL", "jdbc:mysql://localhost:3306/library_db");
            String user = env.getOrDefault("DB_USER", "root");
            String pass = env.getOrDefault("DB_PASS", "Nns22bc030@");

            connection = DriverManager.getConnection(url, user, pass);
            System.out.println("Database connected successfully to: " + url);
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found. Add the connector jar to classpath.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Failed to connect to database:");
            e.printStackTrace();
        }
        return connection;
    }

    public static void main(String[] args) {
        getConnection();
    }
}
