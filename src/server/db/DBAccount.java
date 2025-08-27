package server.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Utility class providing connections to the SQL Server database.
 */
public class DBAccount {

    /** JDBC connection URL for the game database. */
    private static final String url = "jdbc:sqlserver://DESKTOP-K8GJSDN\\SQLEXPRESS;databaseName=GameDB;encrypt=true;trustServerCertificate=true;";

    /** Database user name. */
    private static final String user = "sa";

    /** Database password. */
    private static final String password = "123456";

    /** Fully qualified class name of the SQLServer JDBC driver. */
    private static final String classForName = "com.microsoft.sqlserver.jdbc.SQLServerDriver";

    /**
     * Open a new connection to the SQL Server database.
     *
     * @return active {@link Connection}
     */
    public static Connection getConnectDB() throws ClassNotFoundException, SQLException {
        Class.forName(classForName);
        return DriverManager.getConnection(url, user, password);
    }
}
