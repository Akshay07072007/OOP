package com.hotelbooking;

import com.hotelbooking.gui.MainFrame;
import com.hotelbooking.utils.DatabaseConnection;

import javax.swing.*;
import java.awt.*;

/**
 * Application Launcher - Main class to start the Hotel Booking System
 * Demonstrates OOP with application initialization and error handling
 */
public class ApplicationLauncher {
    
    /**
     * Main method - Entry point of the application
     */
    public static void main(String[] args) {
        // Set modern look and feel for better UI appearance
        setModernLookAndFeel();
        
        // Display splash screen or loading message
        showWelcomeMessage();
        
        // Test database connection before starting GUI
        if (testDatabaseConnection()) {
            // Launch the main application GUI
            launchMainApplication();
        } else {
            // Show error and exit if database connection fails
            showDatabaseErrorAndExit();
        }
    }
    
    /**
     * Set modern look and feel for the application
     */
    private static void setModernLookAndFeel() {
        try {
            // Use system look and feel for native appearance
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeel());
            
            // Optional: Set some UI improvements
            UIManager.put("Button.foreground", new Color(255, 255, 255));
            UIManager.put("Button.select", new Color(70, 130, 180));
            UIManager.put("ProgressBar.foreground", new Color(34, 139, 34));
            
        } catch (Exception e) {
            System.err.println("Warning: Could not set system look and feel. Using default.");
            e.printStackTrace();
        }
    }
    
    /**
     * Show welcome message and application info
     */
    private static void showWelcomeMessage() {
        System.out.println("=========================================");
        System.out.println("    🏨 Hotel Room Booking System");
        System.out.println("=========================================");
        System.out.println("Developed by: OOP Project Team");
        System.out.println("Team Members:");
        System.out.println("  - ABHISHEK ANIL (KNP24CS012)");
        System.out.println("  - ADITHYAKRISHNAN S (KNP24CS018)");
        System.out.println("  - AKSHAY S RAJAN (KNP24CS029)");
        System.out.println("  - ANIRUDH P (KNP24CS038)");
        System.out.println("  - ARSHA A L (KNP24CS047)");
        System.out.println("  - ATHUL KRISHNAN M (KNP24CS052)");
        System.out.println("=========================================");
        System.out.println("Initializing application...");
    }
    
    /**
     * Test database connection before starting application
     * @return true if connection successful, false otherwise
     */
    private static boolean testDatabaseConnection() {
        System.out.println("🔗 Testing database connection...");
        
        boolean connectionSuccessful = DatabaseConnection.testConnection();
        
        if (connectionSuccessful) {
            System.out.println("✅ Database connection established successfully!");
            return true;
        } else {
            System.out.println("❌ Database connection failed!");
            return false;
        }
    }
    
    /**
     * Launch the main application GUI
     */
    private static void launchMainApplication() {
        System.out.println("🚀 Launching application GUI...");
        
        // Use SwingUtilities to ensure thread safety for GUI
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    // Create and display the main application frame
                    MainFrame mainFrame = new MainFrame();
                    mainFrame.setVisible(true);
                    
                    System.out.println("✅ Application started successfully!");
                    System.out.println("🎯 Ready for hotel room bookings!");
                    
                } catch (Exception e) {
                    System.err.println("❌ Error starting application: " + e.getMessage());
                    e.printStackTrace();
                    showErrorAndExit("Failed to start application GUI: " + e.getMessage());
                }
            }
        });
    }
    
    /**
     * Show database error message and exit
     */
    private static void showDatabaseErrorAndExit() {
        String errorMessage = 
            "❌ Database Connection Failed!\n\n" +
            "The application cannot connect to the database.\n\n" +
            "Please ensure:\n" +
            "• MySQL server is running\n" +
            "• Database 'hotel_booking_system' exists\n" +
            "• Correct username/password in DatabaseConnection.java\n" +
            "• MySQL Connector/J is in classpath\n\n" +
            "Application will now exit.";
        
        showErrorAndExit(errorMessage);
    }
    
    /**
     * Show error message dialog and exit application
     */
    private static void showErrorAndExit(String message) {
        JOptionPane.showMessageDialog(
            null,
            message,
            "Application Error",
            JOptionPane.ERROR_MESSAGE
        );
        
        System.exit(1);
    }
    
    /**
     * Display application version and system info
     */
    public static void displaySystemInfo() {
        System.out.println("\n=== System Information ===");
        System.out.println("Java Version: " + System.getProperty("java.version"));
        System.out.println("JVM Version: " + System.getProperty("java.vm.version"));
        System.out.println("OS: " + System.getProperty("os.name"));
        System.out.println("Architecture: " + System.getProperty("os.arch"));
        System.out.println("==========================\n");
    }
    
    /**
     * Method to gracefully shutdown the application
     */
    public static void shutdown() {
        System.out.println("🛑 Shutting down application...");
        
        // Close database connection
        DatabaseConnection.closeConnection();
        
        System.out.println("✅ Application shutdown complete.");
        System.exit(0);
    }
  }
