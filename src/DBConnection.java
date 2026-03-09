import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

public class DBConnection {
    private static Connection connection = null;
    public static Connection getConnection() throws LibraryException {
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
            return connection;
        } catch (ClassNotFoundException e) {
            throw new LibraryException("MySQL JDBC Driver not found. Add the connector jar to classpath.", e);
        } catch (SQLException e) {
            throw new LibraryException("Failed to connect to database: " + e.getMessage(), e);
        }
    }

    public static void main(String[] args) {
        try {
            getConnection();
        } catch (LibraryException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
