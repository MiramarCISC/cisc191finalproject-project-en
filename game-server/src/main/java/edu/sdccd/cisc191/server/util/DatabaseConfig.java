package edu.sdccd.cisc191.server.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConfig {
    private static final String URL = "jdbc:h2:./data/matchdb";

    public static Connection getConnection() throws SQLException {
        //  returns a DriverManager connection
        return DriverManager.getConnection(URL);
    }
}