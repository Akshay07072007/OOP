 package com.hotelbooking.gui;

import com.hotelbooking.model.Room;
import com.hotelbooking.services.RoomService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * GUI Dialog for searching and displaying available rooms
 * Demonstrates OOP with GUI components and event handling
 */
public class SearchRoomsDialog extends JDialog {
    private RoomService roomService;
    private JTable roomsTable;
    private JComboBox<String> roomTypeComboBox;
    private JTextField maxPriceField;
    private JButton searchButton;
    private JButton clearButton;
    private JButton bookButton;
    private JLabel resultsLabel;

    public SearchRoomsDialog(JFrame parent) {
        super(parent, "Search Available Rooms", true);
        this.roomService = new RoomService();
        initializeComponents();
        setupLayout();
        setupEventListeners();
        loadAvailableRooms();
        setDialogProperties();
    }

    private void initializeComponents() {
        // Room type filter
        roomTypeComboBox = new JComboBox<>(new String[]{"All Types", "Standard", "Deluxe", "Suite"});
        
        // Max price filter
        maxPriceField = new JTextField(10);
        maxPriceField.setToolTipText("Enter maximum price (0 for no limit)");
        
        // Buttons
        searchButton = new JButton("🔍 Search");
        clearButton = new JButton("🔄 Clear");
        bookButton = new JButton("📅 Book Selected Room");
        
        // Results label
        resultsLabel = new JLabel("Available Rooms: 0");
        resultsLabel.setFont(new Font("Arial", Font.BOLD, 14));
        resultsLabel.setForeground(new Color(0, 100, 0));
        
        // Style buttons
        styleButton(searchButton, new Color(70, 130, 180));
        styleButton(clearButton, new Color(255, 140, 0));
        styleButton(bookButton, new Color(34, 139, 34));
        
        // Table for displaying rooms
        String[] columnNames = {"Room No.", "Type", "Price", "Status", "Amenities"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };
        roomsTable = new JTable(tableModel);
        roomsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        roomsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        roomsTable.setRowHeight(25);
        roomsTable.setFont(new Font("Arial", Font.PLAIN, 12));
        
        // Set column widths
        roomsTable.getColumnModel().getColumn(0).setPreferredWidth(80);  // Room No
        roomsTable.getColumnModel().getColumn(1).setPreferredWidth(100); // Type
        roomsTable.getColumnModel().getColumn(2).setPreferredWidth(80);  // Price
        roomsTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Status
        roomsTable.getColumnModel().getColumn(4).setPreferredWidth(200); // Amenities
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
        setBackground(Color.WHITE);
        
        // Main container with padding
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(Color.WHITE);
        
        // Filter panel
        JPanel filterPanel = createFilterPanel();
        
        // Table panel
        JPanel tablePanel = createTablePanel();
        
        // Button panel
        JPanel buttonPanel = createButtonPanel();
        
        // Add panels to main panel
        mainPanel.add(filterPanel, BorderLayout.NORTH);
        mainPanel.add(tablePanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }

    private JPanel createFilterPanel() {
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        filterPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)), 
            "Search Filters"
        ));
        filterPanel.setBackground(new Color(240, 240, 240));
        
        JLabel typeLabel = new JLabel("Room Type:");
        typeLabel.setFont(new Font("Arial", Font.BOLD, 12));
        
        JLabel priceLabel = new JLabel("Max Price:");
        priceLabel.setFont(new Font("Arial", Font.BOLD, 12));
        
        filterPanel.add(typeLabel);
        filterPanel.add(roomTypeComboBox);
        filterPanel.add(Box.createHorizontalStrut(20));
        filterPanel.add(priceLabel);
        filterPanel.add(maxPriceField);
        filterPanel.add(Box.createHorizontalStrut(20));
        filterPanel.add(searchButton);
        filterPanel.add(clearButton);
        
        return filterPanel;
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)), 
            "Available Rooms"
        ));
        tablePanel.setBackground(Color.WHITE);
        
        JScrollPane tableScrollPane = new JScrollPane(roomsTable);
        tableScrollPane.setPreferredSize(new Dimension(700, 300));
        
        // Results counter at top of table
        JPanel resultsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        resultsPanel.setBackground(Color.WHITE);
        resultsPanel.add(resultsLabel);
        
        tablePanel.add(resultsPanel, BorderLayout.NORTH);
        tablePanel.add(tableScrollPane, BorderLayout.CENTER);
        
        return tablePanel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        buttonPanel.add(bookButton);
        return buttonPanel;
    }

    private void setupEventListeners() {
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchRooms();
            }
        });

        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearFilters();
            }
        });

        bookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                bookSelectedRoom();
            }
        });
        
        // Enter key in price field triggers search
        maxPriceField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchRooms();
            }
        });
    }

    private void loadAvailableRooms() {
        List<Room> rooms = roomService.getAvailableRooms();
        updateTable(rooms);
    }

    private void searchRooms() {
        try {
            String selectedType = (String) roomTypeComboBox.getSelectedItem();
            String priceText = maxPriceField.getText().trim();
            
            String roomType = null;
            if (!"All Types".equals(selectedType)) {
                roomType = selectedType;
            }
            
            double maxPrice = 0;
            if (!priceText.isEmpty()) {
                maxPrice = Double.parseDouble(priceText);
                if (maxPrice < 0) {
                    JOptionPane.showMessageDialog(this, 
                        "Price cannot be negative!", 
                        "Invalid Input", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            
            List<Room> rooms = roomService.searchRooms(roomType, maxPrice);
            updateTable(rooms);
            
            if (rooms.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "No rooms found matching your criteria!", 
                    "No Results", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "Please enter a valid number for price!", 
                "Invalid Input", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTable(List<Room> rooms) {
        DefaultTableModel model = (DefaultTableModel) roomsTable.getModel();
        model.setRowCount(0); // Clear existing rows
        
        for (Room room : rooms) {
            model.addRow(new Object[]{
                room.getRoomNumber(),
                room.getRoomType(),
                String.format("$%.2f", room.getPrice()),
                room.isAvailable() ? "Available" : "Occupied",
                room.getAmenities()
            });
        }
        
        // Update results counter
        resultsLabel.setText("Available Rooms: " + rooms.size());
        resultsLabel.setForeground(rooms.isEmpty() ? Color.RED : new Color(0, 100, 0));
    }

    private void clearFilters() {
        roomTypeComboBox.setSelectedIndex(0);
        maxPriceField.setText("");
        loadAvailableRooms();
    }

    private void bookSelectedRoom() {
        int selectedRow = roomsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a room from the table to book!", 
                "No Room Selected", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int roomNumber = (int) roomsTable.getValueAt(selectedRow, 0);
        String roomType = (String) roomsTable.getValueAt(selectedRow, 1);
        String price = (String) roomsTable.getValueAt(selectedRow, 2);
        
        int choice = JOptionPane.showConfirmDialog(this,
            "Book Room " + roomNumber + "?\n" +
            "Type: " + roomType + "\n" +
            "Price: " + price + "\n\n" +
            "Booking dialog will open next...",
            "Confirm Room Selection",
            JOptionPane.YES_NO_OPTION);
            
        if (choice == JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(this,
                "Booking feature for Room " + roomNumber + " will be implemented in the next update!\n" +
                "We'll create BookingDialog.java next.",
                "Feature Coming Soon",
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
