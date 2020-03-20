package Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    public static Connection openConnection() throws ClassNotFoundException, SQLException {
        String driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
        String url = "jdbc:sqlserver://192.168.1.9:1433; databaseName=ItemDB; " + " user=sa; password=tdkktd3300";
        Class.forName(driver);
        return DriverManager.getConnection(url);
    }
}
