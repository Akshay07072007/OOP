 package com.hotelbooking.gui;

import com.hotelbooking.model.Booking;
import com.hotelbooking.model.Customer;
import com.hotelbooking.model.Room;
import com.hotelbooking.services.BookingService;
import com.hotelbooking.services.CustomerService;
import com.hotelbooking.services.RoomService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * GUI Dialog for making hotel bookings
 * Demonstrates OOP with multiple service integrations
 */
public class BookingDialog extends JDialog {
    private BookingService bookingService;
    private CustomerService customerService;
    private RoomService roomService;
    
    private JTextField customerNameField;
    private JTextField customerEmailField;
    private JTextField customerPhoneField;
    private JTextField roomNumberField;
    private JFormattedTextField checkInField;
    private JFormattedTextField checkOutField;
    private JLabel roomDetailsLabel;
    private JLabel totalAmountLabel;
    private JButton searchCustomerButton;
    private JButton searchRoomButton;
    private JButton calculateButton;
    private JButton confirmBookingButton;
    private JButton cancelButton;
    
    private Room selectedRoom;
    private Customer selectedCustomer;
    private DateTimeFormatter dateFormatter;

    public BookingDialog(JFrame parent, Integer preSelectedRoom) {
        super(parent, "Make New Booking", true);
        this.bookingService = new BookingService();
        this.customerService = new CustomerService();
        this.roomService = new RoomService();
        this.dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        initializeComponents();
        setupLayout();
        setupEventListeners();
        setDialogProperties();
        
        // If room number is provided, pre-fill it
        if (preSelectedRoom != null) {
            roomNumberField.setText(preSelectedRoom.toString());
            searchRoom();
        }
    }

    private void initializeComponents() {
        // Customer fields
        customerNameField = new JTextField(20);
        customerEmailField = new JTextField(20);
        customerPhoneField = new JTextField(15);
        
        // Room fields
        roomNumberField = new JTextField(10);
        
        // Date fields with current date as default
        checkInField = new JFormattedTextField(dateFormatter);
        checkOutField = new JFormattedTextField(dateFormatter);
        checkInField.setValue(LocalDate.now().plusDays(1));
        checkOutField.setValue(LocalDate.now().plusDays(3));
        
        // Labels for displaying information
        roomDetailsLabel = new JLabel("Select a room to see details");
        roomDetailsLabel.setFont(new Font("Arial", Font.BOLD, 12));
        roomDetailsLabel.setForeground(new Color(70, 130, 180));
        
        totalAmountLabel = new JLabel("Total Amount: $0.00");
        totalAmountLabel.setFont(new Font("Arial", Font.BOLD, 14));
        totalAmountLabel.setForeground(new Color(34, 139, 34));
        
        // Buttons
        searchCustomerButton = new JButton("🔍 Find Customer");
        searchRoomButton = new JButton("🔍 Find Room");
        calculateButton = new JButton("🧮 Calculate Total");
        confirmBookingButton = new JButton("✅ Confirm Booking");
        cancelButton = new JButton("❌ Cancel");
        
        // Style buttons
        styleButton(searchCustomerButton, new Color(70, 130, 180));
        styleButton(searchRoomButton, new Color(70, 130, 180));
        styleButton(calculateButton, new Color(255, 140, 0));
        styleButton(confirmBookingButton, new Color(34, 139, 34));
        styleButton(cancelButton, new Color(220, 20, 60));
        
        // Set tooltips
        customerEmailField.setToolTipText("Enter customer email to search existing customer");
        roomNumberField.setToolTipText("Enter room number to book");
        checkInField.setToolTipText("Format: YYYY-MM-DD");
        checkOutField.setToolTipText("Format: YYYY-MM-DD");
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
        
        // Form panel
        JPanel formPanel = createFormPanel();
        
        // Info panel
        JPanel infoPanel = createInfoPanel();
        
        // Button panel
        JPanel buttonPanel = createButtonPanel();
        
        // Add panels to main panel
        mainPanel.add(formPanel, BorderLayout.NORTH);
        mainPanel.add(infoPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)), 
            "Booking Information"
        ));
        formPanel.setBackground(new Color(240, 240, 240));
        
        // Customer section
        formPanel.add(createLabel("Customer Name:"));
        formPanel.add(customerNameField);
        
        formPanel.add(createLabel("Customer Email:"));
        JPanel emailPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        emailPanel.setBackground(new Color(240, 240, 240));
        emailPanel.add(customerEmailField);
        emailPanel.add(searchCustomerButton);
        formPanel.add(emailPanel);
        
        formPanel.add(createLabel("Customer Phone:"));
        formPanel.add(customerPhoneField);
        
        // Room section
        formPanel.add(createLabel("Room Number:"));
        JPanel roomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        roomPanel.setBackground(new Color(240, 240, 240));
        roomPanel.add(roomNumberField);
        roomPanel.add(searchRoomButton);
        formPanel.add(roomPanel);
        
        // Dates section
        formPanel.add(createLabel("Check-in Date:"));
        formPanel.add(checkInField);
        
        formPanel.add(createLabel("Check-out Date:"));
        formPanel.add(checkOutField);
        
        return formPanel;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 12));
        return label;
    }

    private JPanel createInfoPanel() {
        JPanel infoPanel = new JPanel(new BorderLayout(10, 10));
        infoPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)), 
            "Booking Summary"
        ));
        infoPanel.setBackground(Color.WHITE);
        
        JPanel detailsPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        detailsPanel.setBackground(Color.WHITE);
        detailsPanel.add(roomDetailsLabel);
        detailsPanel.add(totalAmountLabel);
        
        JPanel calculatePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        calculatePanel.setBackground(Color.WHITE);
        calculatePanel.add(calculateButton);
        
        infoPanel.add(detailsPanel, BorderLayout.CENTER);
        infoPanel.add(calculatePanel, BorderLayout.SOUTH);
        
        return infoPanel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(confirmBookingButton);
        buttonPanel.add(cancelButton);
        return buttonPanel;
    }

    private void setupEventListeners() {
        searchCustomerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchCustomer();
            }
        });

        searchRoomButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchRoom();
            }
        });

        calculateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calculateTotal();
            }
        });

        confirmBookingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                confirmBooking();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }

    private void searchCustomer() {
        String email = customerEmailField.getText().trim();
        if (email.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please enter customer email to search!", 
                "Input Required", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Search for existing customer
        Customer customer = customerService.findCustomerByEmail(email);
        if (customer != null) {
            // Populate fields with customer data
            customerNameField.setText(customer.getName());
            customerPhoneField.setText(customer.getPhone());
            selectedCustomer = customer;
            
            JOptionPane.showMessageDialog(this,
                "Customer found!\n" +
                "Name: " + customer.getName() + "\n" +
                "Phone: " + customer.getPhone(),
                "Customer Found",
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                "No customer found with email: " + email + "\n" +
                "Please enter customer details to create a new customer.",
                "Customer Not Found",
                JOptionPane.INFORMATION_MESSAGE);
            selectedCustomer = null;
        }
    }

    private void searchRoom() {
        String roomNumberText = roomNumberField.getText().trim();
        if (roomNumberText.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please enter a room number!", 
                "Input Required", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            int roomNumber = Integer.parseInt(roomNumberText);
            Room room = roomService.getRoomByNumber(roomNumber);
            
            if (room == null) {
                roomDetailsLabel.setText("Room not found!");
                roomDetailsLabel.setForeground(Color.RED);
                selectedRoom = null;
            } else if (!room.isAvailable()) {
                roomDetailsLabel.setText("Room " + roomNumber + " is not available!");
                roomDetailsLabel.setForeground(Color.RED);
                selectedRoom = null;
            } else {
                selectedRoom = room;
                roomDetailsLabel.setText(
                    "Room " + roomNumber + " | " + room.getRoomType() + 
                    " | $" + room.getPrice() + "/night | " + room.getAmenities()
                );
                roomDetailsLabel.setForeground(new Color(0, 100, 0));
                calculateTotal(); // Auto-calculate when room is selected
            }
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "Please enter a valid room number!", 
                "Invalid Input", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void calculateTotal() {
        if (selectedRoom == null) {
            totalAmountLabel.setText("Total Amount: $0.00 - Select a room first");
            totalAmountLabel.setForeground(Color.RED);
            return;
        }
        
        try {
            LocalDate checkIn = LocalDate.parse(checkInField.getText().trim(), dateFormatter);
            LocalDate checkOut = LocalDate.parse(checkOutField.getText().trim(), dateFormatter);
            
            if (!checkOut.isAfter(checkIn)) {
                totalAmountLabel.setText("Error: Check-out must be after check-in!");
                totalAmountLabel.setForeground(Color.RED);
                return;
            }
            
            // Check room availability for selected dates
            if (!bookingService.isRoomAvailable(selectedRoom.getRoomNumber(), checkIn, checkOut)) {
                totalAmountLabel.setText("Room not available for selected dates!");
                totalAmountLabel.setForeground(Color.RED);
                return;
            }
            
            long days = java.time.temporal.ChronoUnit.DAYS.between(checkIn, checkOut);
            double totalAmount = days * selectedRoom.getPrice();
            
            totalAmountLabel.setText(String.format("Total Amount: $%.2f (%d nights)", totalAmount, days));
            totalAmountLabel.setForeground(new Color(34, 139, 34));
            
        } catch (Exception e) {
            totalAmountLabel.setText("Error: Invalid date format (use YYYY-MM-DD)");
            totalAmountLabel.setForeground(Color.RED);
        }
    }

    private void confirmBooking() {
        // Validate all fields
        if (customerNameField.getText().trim().isEmpty() ||
            customerEmailField.getText().trim().isEmpty() ||
            customerPhoneField.getText().trim().isEmpty() ||
            selectedRoom == null) {
            
            JOptionPane.showMessageDialog(this,
                "Please fill in all customer details and select a room!",
                "Incomplete Information",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            LocalDate checkIn = LocalDate.parse(checkInField.getText().trim(), dateFormatter);
            LocalDate checkOut = LocalDate.parse(checkOutField.getText().trim(), dateFormatter);
            
            if (!checkOut.isAfter(checkIn)) {
                JOptionPane.showMessageDialog(this,
                    "Check-out date must be after check-in date!",
                    "Invalid Dates",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Validate customer data
            String name = customerNameField.getText().trim();
            String email = customerEmailField.getText().trim();
            String phone = customerPhoneField.getText().trim();
            
            if (!customerService.validateCustomerData(name, email, phone)) {
                return; // Validation failed
            }
            
            // Check room availability
            if (!bookingService.isRoomAvailable(selectedRoom.getRoomNumber(), checkIn, checkOut)) {
                JOptionPane.showMessageDialog(this,
                    "Room is not available for the selected dates!",
                    "Room Not Available",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Calculate total amount
            long days = java.time.temporal.ChronoUnit.DAYS.between(checkIn, checkOut);
            double totalAmount = days * selectedRoom.getPrice();
            
            // Show confirmation dialog
            int confirmation = JOptionPane.showConfirmDialog(this,
                "Please confirm booking details:\n\n" +
                "Customer: " + name + "\n" +
                "Email: " + email + "\n" +
                "Phone: " + phone + "\n" +
                "Room: " + selectedRoom.getRoomNumber() + " (" + selectedRoom.getRoomType() + ")\n" +
                "Dates: " + checkIn + " to " + checkOut + " (" + days + " nights)\n" +
                "Total Amount: $" + String.format("%.2f", totalAmount) + "\n\n" +
                "Confirm booking?",
                "Confirm Booking",
                JOptionPane.YES_NO_OPTION);
                
            if (confirmation == JOptionPane.YES_OPTION) {
                // Create the booking
                Booking booking = bookingService.makeBooking(name, email, phone, 
                    selectedRoom.getRoomNumber(), checkIn, checkOut, selectedRoom.getPrice());
                
                if (booking != null) {
                    JOptionPane.showMessageDialog(this,
                        "✅ Booking confirmed successfully!\n\n" +
                        "Booking ID: " + booking.getBookingId() + "\n" +
                        "Room: " + selectedRoom.getRoomNumber() + "\n" +
                        "Total Amount: $" + String.format("%.2f", totalAmount) + "\n" +
                        "Status: " + booking.getStatus() + "\n\n" +
                        "Thank you for your booking!",
                        "Booking Confirmed",
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    dispose(); // Close dialog
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Failed to create booking. Please try again.",
                        "Booking Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error processing booking: " + e.getMessage(),
                "Booking Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void setDialogProperties() {
        setSize(600, 500);
        setLocationRelativeTo(getParent());
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }
    }
