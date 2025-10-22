package com.hotelbooking.gui;

import com.hotelbooking.model.Booking;
import com.hotelbooking.services.BookingService;
import com.hotelbooking.services.CustomerService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * GUI Dialog for cancelling bookings with search and confirmation
 * Demonstrates OOP with focused functionality and user confirmation
 */
public class CancelBookingDialog extends JDialog {
    private BookingService bookingService;
    private CustomerService customerService;
    private JTable bookingsTable;
    private JTextField bookingIdField;
    private JTextField customerEmailField;
    private JButton searchByIdButton;
    private JButton searchByEmailButton;
    private JButton refreshButton;
    private JButton cancelBookingButton;
    private JButton viewDetailsButton;
    private JLabel resultsLabel;
    private JLabel instructionsLabel;

    public CancelBookingDialog(JFrame parent) {
        super(parent, "Cancel Booking", true);
        this.bookingService = new BookingService();
        this.customerService = new CustomerService();
        initializeComponents();
        setupLayout();
        setupEventListeners();
        loadActiveBookings();
        setDialogProperties();
    }

    private void initializeComponents() {
        // Search components
        bookingIdField = new JTextField(10);
        bookingIdField.setToolTipText("Enter booking ID to search");
        
        customerEmailField = new JTextField(20);
        customerEmailField.setToolTipText("Enter customer email to search bookings");
        
        // Buttons
        searchByIdButton = new JButton("🔍 Search by ID");
        searchByEmailButton = new JButton("📧 Search by Email");
        refreshButton = new JButton("🔄 Refresh");
        cancelBookingButton = new JButton("❌ Cancel Booking");
        viewDetailsButton = new JButton("📋 View Details");
        
        // Labels
        resultsLabel = new JLabel("Active Bookings: 0");
        resultsLabel.setFont(new Font("Arial", Font.BOLD, 14));
        resultsLabel.setForeground(new Color(0, 100, 0));
        
        instructionsLabel = new JLabel("Select a booking and click 'Cancel Booking' to cancel it");
        instructionsLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        instructionsLabel.setForeground(Color.GRAY);
        
        // Style buttons
        styleButton(searchByIdButton, new Color(70, 130, 180));
        styleButton(searchByEmailButton, new Color(70, 130, 180));
        styleButton(refreshButton, new Color(255, 140, 0));
        styleButton(cancelBookingButton, new Color(220, 20, 60));
        styleButton(viewDetailsButton, new Color(34, 139, 34));
        
        // Table for displaying bookings
        String[] columnNames = {
            "Booking ID", "Customer", "Room", "Check-in", "Check-out", 
            "Amount", "Status", "Days Left"
        };
        
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        bookingsTable = new JTable(tableModel);
        bookingsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bookingsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        bookingsTable.setRowHeight(25);
        bookingsTable.setFont(new Font("Arial", Font.PLAIN, 11));
        
        // Set column widths
        bookingsTable.getColumnModel().getColumn(0).setPreferredWidth(80);   // Booking ID
        bookingsTable.getColumnModel().getColumn(1).setPreferredWidth(120);  // Customer
        bookingsTable.getColumnModel().getColumn(2).setPreferredWidth(60);   // Room
        bookingsTable.getColumnModel().getColumn(3).setPreferredWidth(90);   // Check-in
        bookingsTable.getColumnModel().getColumn(4).setPreferredWidth(90);   // Check-out
        bookingsTable.getColumnModel().getColumn(5).setPreferredWidth(80);   // Amount
        bookingsTable.getColumnModel().getColumn(6).setPreferredWidth(100);  // Status
        bookingsTable.getColumnModel().getColumn(7).setPreferredWidth(80);   // Days Left
    }

    private void styleButton(JButton button, Color color) {
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
    }

    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        
        // Main container with padding
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(Color.WHITE);
        
        // Search panel
        JPanel searchPanel = createSearchPanel();
        
        // Table panel
        JPanel tablePanel = createTablePanel();
        
        // Button panel
        JPanel buttonPanel = createButtonPanel();
        
        // Add panels to main panel
        mainPanel.add(searchPanel, BorderLayout.NORTH);
        mainPanel.add(tablePanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }

    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        searchPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)), 
            "Search Bookings"
        ));
        searchPanel.setBackground(new Color(240, 240, 240));
        
        // First row - Booking ID search
        JPanel idSearchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        idSearchPanel.setBackground(new Color(240, 240, 240));
        idSearchPanel.add(new JLabel("Booking ID:"));
        idSearchPanel.add(bookingIdField);
        idSearchPanel.add(searchByIdButton);
        
        // Second row - Email search
        JPanel emailSearchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        emailSearchPanel.setBackground(new Color(240, 240, 240));
        emailSearchPanel.add(new JLabel("Customer Email:"));
        emailSearchPanel.add(customerEmailField);
        emailSearchPanel.add(searchByEmailButton);
        emailSearchPanel.add(refreshButton);
        
        searchPanel.add(idSearchPanel);
        searchPanel.add(emailSearchPanel);
        
        return searchPanel;
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout(10, 10));
        tablePanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)), 
            "Active Bookings"
        ));
        tablePanel.setBackground(Color.WHITE);
        
        JScrollPane tableScrollPane = new JScrollPane(bookingsTable);
        tableScrollPane.setPreferredSize(new Dimension(800, 250));
        
        // Results counter at top of table
        JPanel resultsPanel = new JPanel(new BorderLayout());
        resultsPanel.setBackground(Color.WHITE);
        resultsPanel.add(resultsLabel, BorderLayout.WEST);
        resultsPanel.add(instructionsLabel, BorderLayout.EAST);
        
        tablePanel.add(resultsPanel, BorderLayout.NORTH);
        tablePanel.add(tableScrollPane, BorderLayout.CENTER);
        
        return tablePanel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(viewDetailsButton);
        buttonPanel.add(cancelBookingButton);
        return buttonPanel;
    }

    private void setupEventListeners() {
        searchByIdButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchBookingById();
            }
        });

        searchByEmailButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchBookingsByEmail();
            }
        });

        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadActiveBookings();
            }
        });

        cancelBookingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancelSelectedBooking();
            }
        });

        viewDetailsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewBookingDetails();
            }
        });
        
        // Enter key listeners
        bookingIdField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchBookingById();
            }
        });
        
        customerEmailField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchBookingsByEmail();
            }
        });
    }

    private void loadActiveBookings() {
        List<Booking> allBookings = bookingService.getAllBookings();
        // Filter only confirmed bookings
        List<Booking> activeBookings = allBookings.stream()
            .filter(booking -> "CONFIRMED".equals(booking.getStatus()))
            .collect(java.util.stream.Collectors.toList());
        updateTable(activeBookings);
        clearSearchFields();
    }

    private void searchBookingById() {
        String bookingIdText = bookingIdField.getText().trim();
        if (bookingIdText.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please enter a booking ID!", 
                "Input Required", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            int bookingId = Integer.parseInt(bookingIdText);
            Booking booking = bookingService.getBookingById(bookingId);
            
            if (booking != null) {
                updateTable(List.of(booking));
            } else {
                JOptionPane.showMessageDialog(this, 
                    "No booking found with ID: " + bookingId, 
                    "Not Found", 
                    JOptionPane.INFORMATION_MESSAGE);
                loadActiveBookings();
            }
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "Please enter a valid booking ID number!", 
                "Invalid Input", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchBookingsByEmail() {
        String email = customerEmailField.getText().trim();
        if (email.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please enter customer email!", 
                "Input Required", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        List<Booking> bookings = bookingService.getBookingsByCustomerEmail(email);
        // Filter only confirmed bookings
        List<Booking> activeBookings = bookings.stream()
            .filter(booking -> "CONFIRMED".equals(booking.getStatus()))
            .collect(java.util.stream.Collectors.toList());
        updateTable(activeBookings);
        
        if (activeBookings.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "No active bookings found for email: " + email, 
                "No Results", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void updateTable(List<Booking> bookings) {
        DefaultTableModel model = (DefaultTableModel) bookingsTable.getModel();
        model.setRowCount(0); // Clear existing rows
        
        for (Booking booking : bookings) {
            String customerName = getCustomerName(booking.getCustomerId());
            long daysLeft = calculateDaysUntilCheckIn(booking.getCheckInDate());
            String daysLeftText = daysLeft >= 0 ? daysLeft + " days" : "Past";
            
            model.addRow(new Object[]{
                booking.getBookingId(),
                customerName,
                booking.getRoomNumber(),
                booking.getCheckInDate(),
                booking.getCheckOutDate(),
                String.format("$%.2f", booking.getTotalAmount()),
                booking.getStatus(),
                daysLeftText
            });
        }
        
        // Update results counter
        resultsLabel.setText("Active Bookings: " + bookings.size());
        resultsLabel.setForeground(bookings.isEmpty() ? Color.RED : new Color(0, 100, 0));
    }

    private String getCustomerName(int customerId) {
        try {
            var customer = customerService.findCustomerById(customerId);
            return customer != null ? customer.getName() : "Unknown";
        } catch (Exception e) {
            return "Unknown";
        }
    }

    private long calculateDaysUntilCheckIn(java.time.LocalDate checkInDate) {
        return java.time.temporal.ChronoUnit.DAYS.between(
            java.time.LocalDate.now(), checkInDate);
    }

    private void clearSearchFields() {
        bookingIdField.setText("");
        customerEmailField.setText("");
    }

    private void cancelSelectedBooking() {
        int selectedRow = bookingsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a booking to cancel!", 
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int bookingId = (int) bookingsTable.getValueAt(selectedRow, 0);
        String customerName = (String) bookingsTable.getValueAt(selectedRow, 1);
        int roomNumber = (int) bookingsTable.getValueAt(selectedRow, 2);
        String status = (String) bookingsTable.getValueAt(selectedRow, 6);
        
        if ("CANCELLED".equals(status)) {
            JOptionPane.showMessageDialog(this, 
                "This booking is already cancelled!", 
                "Already Cancelled", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Show detailed confirmation
        int confirmation = JOptionPane.showConfirmDialog(this,
            "🚨 Confirm Booking Cancellation\n\n" +
            "Booking ID: #" + bookingId + "\n" +
            "Customer: " + customerName + "\n" +
            "Room: " + roomNumber + "\n" +
            "Check-in: " + bookingsTable.getValueAt(selectedRow, 3) + "\n" +
            "Total Amount: " + bookingsTable.getValueAt(selectedRow, 5) + "\n\n" +
            "⚠️  This action cannot be undone!\n" +
            "The room will become available for new bookings.\n\n" +
            "Are you sure you want to cancel this booking?",
            "Confirm Cancellation",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
            
        if (confirmation == JOptionPane.YES_OPTION) {
            boolean success = bookingService.cancelBooking(bookingId);
            if (success) {
                JOptionPane.showMessageDialog(this,
                    "✅ Booking Cancelled Successfully!\n\n" +
                    "Booking #" + bookingId + " has been cancelled.\n" +
                    "Room " + roomNumber + " is now available for new bookings.\n" +
                    "A cancellation confirmation would be sent to the customer.",
                    "Cancellation Complete",
                    JOptionPane.INFORMATION_MESSAGE);
                loadActiveBookings(); // Refresh the table
            } else {
                JOptionPane.showMessageDialog(this,
                    "❌ Failed to cancel booking #" + bookingId + ".\n" +
                    "Please try again or contact support.",
                    "Cancellation Failed",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void viewBookingDetails() {
        int selectedRow = bookingsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a booking to view details!", 
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int bookingId = (int) bookingsTable.getValueAt(selectedRow, 0);
        Booking booking = bookingService.getBookingById(bookingId);
        
        if (booking != null) {
            String customerName = getCustomerName(booking.getCustomerId());
            long daysLeft = calculateDaysUntilCheckIn(booking.getCheckInDate());
            
            String details = String.format(
                "📋 Booking Details\n\n" +
                "Booking ID: %d\n" +
                "Customer: %s (ID: %d)\n" +
                "Room: %d\n" +
                "Check-in: %s\n" +
                "Check-out: %s\n" +
                "Duration: %d nights\n" +
                "Total Amount: $%.2f\n" +
                "Status: %s\n" +
                "Days until check-in: %s\n\n" +
                "Cancellation Policy:\n" +
                "• Free cancellation until 24 hours before check-in",
                booking.getBookingId(),
                customerName,
                booking.getCustomerId(),
                booking.getRoomNumber(),
                booking.getCheckInDate(),
                booking.getCheckOutDate(),
                booking.getNumberOfDays(),
                booking.getTotalAmount(),
                booking.getStatus(),
                daysLeft >= 0 ? daysLeft + " days" : "Past date"
            );
            
            JOptionPane.showMessageDialog(this,
                details,
                "Booking Details - #" + bookingId,
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void setDialogProperties() {
        setSize(900, 600);
        setLocationRelativeTo(getParent());
        setResizable(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }
          }
