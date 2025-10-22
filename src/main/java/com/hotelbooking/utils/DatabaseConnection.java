package com.hotelbooking.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DatabaseConnection utility class for managing MySQL database connections
 * Demonstrates OOP Abstraction by hiding complex database connection details
 */
public class DatabaseConnection {
    // Database configuration - these should be in a config file in real applications
    private static final String URL = "jdbc:mysql://localhost:3306/hotel_booking_system";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "password"; // Change to your MySQL password
    
    // Static instance for Singleton pattern (optional enhancement)
    private static Connection connection = null;
    
    // Private constructor to prevent instantiation - Utility class pattern
    private DatabaseConnection() {
        // Utility class should not be instantiated
    }
    
    /**
     * Establishes and returns a database connection
     * @return Connection object or null if connection fails
     */
    public static Connection getConnection() {
        try {
            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Create and return connection
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.println("✅ Database connection established successfully!");
            return connection;
            
        } catch (ClassNotFoundException e) {
            System.out.println("❌ MySQL JDBC Driver not found!");
            System.out.println("Please add MySQL Connector/J to your classpath.");
            e.printStackTrace();
            return null;
        } catch (SQLException e) {
            System.out.println("❌ Database connection failed!");
            System.out.println("Error: " + e.getMessage());
            System.out.println("Please check:");
            System.out.println("1. Is MySQL server running?");
            System.out.println("2. Is database 'hotel_booking_system' created?");
            System.out.println("3. Are username and password correct?");
            return null;
        }
    }
    
    /**
     * Closes the database connection
     */
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("✅ Database connection closed successfully!");
            } catch (SQLException e) {
                System.out.println("❌ Error closing database connection: " + e.getMessage());
            }
        }
    }
    
    /**
     * Tests the database connection
     * @return true if connection is successful, false otherwise
     */
    public static boolean testConnection() {
        try (Connection testConn = getConnection()) {
            if (testConn != null && !testConn.isClosed()) {
                System.out.println("✅ Database connection test: PASSED");
                return true;
            }
        } catch (SQLException e) {
            System.out.println("❌ Database connection test: FAILED");
            System.out.println("Error: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Gets database connection info (for debugging)
     */
    public static void printConnectionInfo() {
        if (connection != null) {
            try {
                System.out.println("📊 Database Connection Info:");
                System.out.println("URL: " + URL);
                System.out.println("Database: " + connection.getCatalog());
                System.out.println("Auto Commit: " + connection.getAutoCommit());
                System.out.println("Is Closed: " + connection.isClosed());
            } catch (SQLException e) {
                System.out.println("Error getting connection info: " + e.getMessage());
            }
        } else {
            System.out.println("No active database connection.");
        }
    }
    
    /**
     * Alternative method to get connection with custom parameters
     */
    public static Connection getConnection(String url, String username, String password) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Custom connection failed: " + e.getMessage());
            return null;
        }
    }
              }
