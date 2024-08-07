package library.project.conn;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {
    public static Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://127.0.0.1:3306/?user=UserJava";
        String username = "UserJava";
        String password = "Userjava@";

        return DriverManager.getConnection(url, username, password);
    }
}
