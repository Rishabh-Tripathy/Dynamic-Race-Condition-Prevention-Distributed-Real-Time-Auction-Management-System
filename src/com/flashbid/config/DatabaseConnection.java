package com.flashbid.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static Connection connection = null;

    private DatabaseConnection() {}

    public static synchronized Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                // Default XAMPP credentials: root with no password
                String url = "jdbc:mysql://localhost:3306/flashbid_db?useSSL=false&allowPublicKeyRetrieval=true";
                String user = "root";
                String password = ""; // Leave blank for default XAMPP, or enter your password
                connection = DriverManager.getConnection(url, user, password);
            }
        } catch (ClassNotFoundException e) {
            System.err.println("[ERROR] MySQL Driver not found. Ensure mysql-connector-j.jar is in lib/: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("[ERROR] Database connection failed: " + e.getMessage());
        }
        return connection;
    }
}