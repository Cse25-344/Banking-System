
package com.example.bankingsystemgui;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL = "jdbc:sqlite:banking.db";
    static {
        System.out.println("DATABASE LOCATION: " +
                new java.io.File("banking.db").getAbsolutePath());
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }
}
