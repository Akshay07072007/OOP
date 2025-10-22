 package com.hotelbooking.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Main GUI Frame for Hotel Booking System
 * Demonstrates OOP with GUI components
 */
public class MainFrame extends JFrame {
    private JPanel mainPanel;
    private JButton searchRoomsButton;
    private JButton makeBookingButton;
    private JButton cancelBookingButton;
    private JButton viewBookingsButton;
    private JButton exitButton;
    private JLabel titleLabel;

    public MainFrame() {
        initializeComponents();
        setupLayout();
        setupEventListeners();
        setFrameProperties();
    }

    private void initializeComponents() {
        mainPanel = new JPanel();
        titleLabel = new JLabel("🏨 Hotel Room Booking System");
        searchRoomsButton = new JButton("🔍 Search Available Rooms");
        makeBookingButton = new JButton("📅 Make Booking");
        cancelBookingButton = new JButton("❌ Cancel Booking");
        viewBookingsButton = new JButton("📋 View My Bookings");
        exitButton = new JButton("🚪 Exit");
    }

    private void setupLayout() {
        mainPanel.setLayout(new GridLayout(6, 1, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        
        // Style the title
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setForeground(new Color(0, 100, 200));
        
        // Style buttons
        styleButton(searchRoomsButton, new Color(70, 130, 180));
        styleButton(makeBookingButton, new Color(34, 139, 34));
        styleButton(cancelBookingButton, new Color(220, 20, 60));
        styleButton(viewBookingsButton, new Color(255, 140, 0));
        styleButton(exitButton, new Color(128, 128, 128));
        
        // Add components to panel
        mainPanel.add(titleLabel);
        mainPanel.add(searchRoomsButton);
        mainPanel.add(makeBookingButton);
        mainPanel.add(cancelBookingButton);
        mainPanel.add(viewBookingsButton);
        mainPanel.add(exitButton);
        
        add(mainPanel);
    }

    private void styleButton(JButton button, Color color) {
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createRaisedBevelBorder());
        button.setPreferredSize(new Dimension(300, 50));
    }

    private void setupEventListeners() {
        searchRoomsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(MainFrame.this, 
                    "Search Rooms feature will be implemented here!", 
                    "Feature Coming Soon", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
        });

        makeBookingButton.addActionListener(new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        BookingDialog bookingDialog = new BookingDialog(MainFrame.this, null);
        bookingDialog.setVisible(true);
    }
});
        cancelBookingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(MainFrame.this, 
                    "Cancel Booking feature will be implemented here!", 
                    "Feature Coming Soon", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
        });

        viewBookingsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(MainFrame.this, 
                    "View Bookings feature will be implemented here!", 
                    "Feature Coming Soon", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
        });

        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int confirm = JOptionPane.showConfirmDialog(MainFrame.this,
                    "Are you sure you want to exit?", 
                    "Confirm Exit", 
                    JOptionPane.YES_NO_OPTION);
                
                if (confirm == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });
    }

    private void setFrameProperties() {
        setTitle("Hotel Room Booking System - OOP Project");
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window
        setResizable(false);
    }

    /**
     * Main method to launch the application
     */
    public static void main(String[] args) {
        // Set modern look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeel());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Create and show GUI
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainFrame().setVisible(true);
            }
        });
    }
          }
