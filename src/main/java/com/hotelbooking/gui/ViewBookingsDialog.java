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
 * GUI Dialog for viewing and managing bookings
 * Demonstrates OOP with data display and user interactions
 */
public class ViewBookingsDialog extends JDialog {
    private BookingService bookingService;
    private CustomerService customerService;
    private JTable bookingsTable;
    private JTextField searchEmailField;
    private JButton searchButton;
    private JButton refreshButton;
    private JButton cancelBookingButton;
    private JButton viewDetailsButton;
    private JLabel resultsLabel;
    private JComboBox<String> filterComboBox;

    public ViewBookingsDialog(JFrame parent) {
        super(parent, "View and Manage Bookings", true);
        this.bookingService = new BookingService();
        this.customerService = new CustomerService();
        initializeComponents();
        setupLayout();
        setupEventListeners();
        loadAllBookings();
        setDialogProperties();
    }

    private void initializeComponents() {
        // Search components
        searchEmailField = new JTextField(20);
        searchEmailField.setToolTipText("Enter customer email to search bookings");
        
        filterComboBox = new JComboBox<>(new String[]{
            "All Bookings", "Confirmed Only", "Cancelled Only"
        });
        
        // Buttons
        searchButton = new JButton("🔍 Search by Email");
        refreshButton = new JButton("🔄 Refresh All");
        cancelBookingButton = new JButton("❌ Cancel Booking");
        viewDetailsButton = new JButton("📋 View Details");
        
        // Results label
        resultsLabel = new JLabel("Total Bookings: 0");
        resultsLabel.setFont(new Font("Arial", Font.BOLD, 14));
        resultsLabel.setForeground(new Color(0, 100, 0));
        
        // Style buttons
        styleButton(searchButton, new Color(70, 130, 180));
        styleButton(refreshButton, new Color(255, 140, 0));
        styleButton(cancelBookingButton, new Color(220, 20, 60));
        styleButton(viewDetailsButton, new Color(34, 139, 34));
        
        // Table for displaying bookings
        String[] columnNames = {
            "Booking ID", "Customer", "Room", "Check-in", "Check-out", 
            "Amount", "Status", "Days"
        };
        
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
            
            @Override
            public Class<?> getColumnClass(int column) {
                return String.class; // All columns as strings for simplicity
            }
        };
        
        bookingsTable = new JTable(tableModel);
        bookingsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bookingsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        bookingsTable.setRowHeight(25);
        bookingsTable.setFont(new Font("Arial", Font.PLAIN, 11));
        
        // Set column widths
        bookingsTable.getColumnModel().getColumn(0).setPreferredWidth(80);  // Booking ID
        bookingsTable.getColumnModel().getColumn(1).setPreferredWidth(120); // Customer
        bookingsTable.getColumnModel().getColumn(2).setPreferredWidth(60);  // Room
        bookingsTable.getColumnModel().getColumn(3).setPreferredWidth(90);  // Check-in
        bookingsTable.getColumnModel().getColumn(4).setPreferredWidth(90);  // Check-out
        bookingsTable.getColumnModel().getColumn(5).setPreferredWidth(80);  // Amount
        bookingsTable.getColumnModel().getColumn(6).setPreferredWidth(100); // Status
        bookingsTable.getColumnModel().getColumn(7).setPreferredWidth(60);  // Days
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
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        searchPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)), 
            "Search and Filter"
        ));
        searchPanel.setBackground(new Color(240, 240, 240));
        
        JLabel emailLabel = new JLabel("Customer Email:");
        emailLabel.setFont(new Font("Arial", Font.BOLD, 12));
        
        JLabel filterLabel = new JLabel("Filter by Status:");
        filterLabel.setFont(new Font("Arial", Font.BOLD, 12));
        
        searchPanel.add(emailLabel);
        searchPanel.add(searchEmailField);
        searchPanel.add(searchButton);
        searchPanel.add(Box.createHorizontalStrut(20));
        searchPanel.add(filterLabel);
        searchPanel.add(filterComboBox);
        searchPanel.add(refreshButton);
        
        return searchPanel;
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)), 
            "Bookings"
        ));
        tablePanel.setBackground(Color.WHITE);
        
        JScrollPane tableScrollPane = new JScrollPane(bookingsTable);
        tableScrollPane.setPreferredSize(new Dimension(800, 300));
        
        // Results counter at top of table
        JPanel resultsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        resultsPanel.setBackground(Color.WHITE);
        resultsPanel.add(resultsLabel);
        
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
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchBookingsByEmail();
            }
        });

        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadAllBookings();
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
        
        filterComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                applyFilter();
            }
        });
        
        // Enter key in search field triggers search
        searchEmailField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchBookingsByEmail();
            }
        });
    }

    private void loadAllBookings() {
        List<Booking> bookings = bookingService.getAllBookings();
        updateTable(bookings);
    }

    private void searchBookingsByEmail() {
        String email = searchEmailField.getText().trim();
        if (email.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please enter customer email to search!", 
                "Input Required", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        List<Booking> bookings = bookingService.getBookingsByCustomerEmail(email);
        updateTable(bookings);
        
        if (bookings.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "No bookings found for email: " + email, 
                "No Results", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void applyFilter() {
        String filter = (String) filterComboBox.getSelectedItem();
        List<Booking> allBookings = bookingService.getAllBookings();
        List<Booking> filteredBookings = allBookings;
        
        if ("Confirmed Only".equals(filter)) {
            filteredBookings = allBookings.stream()
                .filter(booking -> "CONFIRMED".equals(booking.getStatus()))
                .collect(java.util.stream.Collectors.toList());
        } else if ("Cancelled Only".equals(filter)) {
            filteredBookings = allBookings.stream()
                .filter(booking -> "CANCELLED".equals(booking.getStatus()))
                .collect(java.util.stream.Collectors.toList());
        }
        
        updateTable(filteredBookings);
    }

    private void updateTable(List<Booking> bookings) {
        DefaultTableModel model = (DefaultTableModel) bookingsTable.getModel();
        model.setRowCount(0); // Clear existing rows
        
        for (Booking booking : bookings) {
            // Get customer name for display
            String customerName = getCustomerName(booking.getCustomerId());
            long days = booking.getNumberOfDays();
            
            model.addRow(new Object[]{
                booking.getBookingId(),
                customerName,
                booking.getRoomNumber(),
                booking.getCheckInDate(),
                booking.getCheckOutDate(),
                String.format("$%.2f", booking.getTotalAmount()),
                booking.getStatus(),
                days + " days"
            });
        }
        
        // Update results counter
        resultsLabel.setText("Total Bookings: " + bookings.size());
        resultsLabel.setForeground(bookings.isEmpty() ? Color.RED : new Color(0, 100, 0));
    }

    private String getCustomerName(int customerId) {
        // In a real application, you might want to cache this or join in SQL
        try {
            var customer = customerService.findCustomerById(customerId);
            return customer != null ? customer.getName() : "Unknown";
        } catch (Exception e) {
            return "Unknown";
        }
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
        String status = (String) bookingsTable.getValueAt(selectedRow, 6);
        
        if ("CANCELLED".equals(status)) {
            JOptionPane.showMessageDialog(this, 
                "This booking is already cancelled!", 
                "Already Cancelled", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        int confirmation = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to cancel booking #" + bookingId + "?\n" +
            "This action cannot be undone.",
            "Confirm Cancellation",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
            
        if (confirmation == JOptionPane.YES_OPTION) {
            boolean success = bookingService.cancelBooking(bookingId);
            if (success) {
                JOptionPane.showMessageDialog(this,
                    "Booking #" + bookingId + " has been cancelled successfully!",
                    "Cancellation Successful",
                    JOptionPane.INFORMATION_MESSAGE);
                loadAllBookings(); // Refresh the table
            } else {
                JOptionPane.showMessageDialog(this,
                    "Failed to cancel booking #" + bookingId + ".\n" +
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
            
            String details = String.format(
                "📋 Booking Details\n\n" +
                "Booking ID: %d\n" +
                "Customer: %s (ID: %d)\n" +
                "Room: %d\n" +
                "Check-in: %s\n" +
                "Check-out: %s\n" +
                "Duration: %d nights\n" +
                "Total Amount: $%.2f\n" +
                "Status: %s\n\n" +
                "Created: %s",
                booking.getBookingId(),
                customerName,
                booking.getCustomerId(),
                booking.getRoomNumber(),
                booking.getCheckInDate(),
                booking.getCheckOutDate(),
                booking.getNumberOfDays(),
                booking.getTotalAmount(),
                booking.getStatus(),
                "Database timestamp" // You could add created_at to Booking model
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
