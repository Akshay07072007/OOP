 package com.hotelbooking.gui;

import com.hotelbooking.model.Customer;
import com.hotelbooking.services.CustomerService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * GUI Dialog for managing customer registrations and information
 * Demonstrates OOP with CRUD operations and data validation
 */
public class CustomerManagementDialog extends JDialog {
    private CustomerService customerService;
    private JTable customersTable;
    private JTextField searchField;
    private JTextField nameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JButton searchButton;
    private JButton refreshButton;
    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton clearButton;
    private JLabel resultsLabel;
    
    private Customer selectedCustomer;

    public CustomerManagementDialog(JFrame parent) {
        super(parent, "Customer Management", true);
        this.customerService = new CustomerService();
        initializeComponents();
        setupLayout();
        setupEventListeners();
        loadAllCustomers();
        setDialogProperties();
    }

    private void initializeComponents() {
        // Search component
        searchField = new JTextField(20);
        searchField.setToolTipText("Search customers by name");
        
        // Customer form fields
        nameField = new JTextField(20);
        emailField = new JTextField(20);
        phoneField = new JTextField(15);
        
        // Buttons
        searchButton = new JButton("🔍 Search");
        refreshButton = new JButton("🔄 Refresh");
        addButton = new JButton("➕ Add Customer");
        updateButton = new JButton("✏️ Update");
        deleteButton = new JButton("🗑️ Delete");
        clearButton = new JButton("🧹 Clear");
        
        // Results label
        resultsLabel = new JLabel("Total Customers: 0");
        resultsLabel.setFont(new Font("Arial", Font.BOLD, 14));
        resultsLabel.setForeground(new Color(0, 100, 0));
        
        // Style buttons
        styleButton(searchButton, new Color(70, 130, 180));
        styleButton(refreshButton, new Color(255, 140, 0));
        styleButton(addButton, new Color(34, 139, 34));
        styleButton(updateButton, new Color(255, 165, 0));
        styleButton(deleteButton, new Color(220, 20, 60));
        styleButton(clearButton, new Color(128, 128, 128));
        
        // Table for displaying customers
        String[] columnNames = {"Customer ID", "Name", "Email", "Phone", "Registered Date"};
        
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };
        
        customersTable = new JTable(tableModel);
        customersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        customersTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        customersTable.setRowHeight(25);
        customersTable.setFont(new Font("Arial", Font.PLAIN, 11));
        
        // Set column widths
        customersTable.getColumnModel().getColumn(0).setPreferredWidth(80);   // Customer ID
        customersTable.getColumnModel().getColumn(1).setPreferredWidth(150);  // Name
        customersTable.getColumnModel().getColumn(2).setPreferredWidth(180);  // Email
        customersTable.getColumnModel().getColumn(3).setPreferredWidth(100);  // Phone
        customersTable.getColumnModel().getColumn(4).setPreferredWidth(120);  // Registered Date
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
        
        // Top panel with search and form
        JPanel topPanel = createTopPanel();
        
        // Table panel
        JPanel tablePanel = createTablePanel();
        
        // Button panel
        JPanel buttonPanel = createButtonPanel();
        
        // Add panels to main panel
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(tablePanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        
        // Search panel
        JPanel searchPanel = createSearchPanel();
        
        // Form panel
        JPanel formPanel = createFormPanel();
        
        topPanel.add(searchPanel, BorderLayout.NORTH);
        topPanel.add(formPanel, BorderLayout.CENTER);
        
        return topPanel;
    }

    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        searchPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)), 
            "Search Customers"
        ));
        searchPanel.setBackground(new Color(240, 240, 240));
        
        JLabel searchLabel = new JLabel("Search by Name:");
        searchLabel.setFont(new Font("Arial", Font.BOLD, 12));
        
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(refreshButton);
        
        return searchPanel;
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)), 
            "Customer Information"
        ));
        formPanel.setBackground(new Color(240, 240, 240));
        
        // Name row
        JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        namePanel.setBackground(new Color(240, 240, 240));
        namePanel.add(new JLabel("Name:"));
        namePanel.add(nameField);
        
        // Email row
        JPanel emailPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        emailPanel.setBackground(new Color(240, 240, 240));
        emailPanel.add(new JLabel("Email:"));
        emailPanel.add(emailField);
        
        // Phone row
        JPanel phonePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        phonePanel.setBackground(new Color(240, 240, 240));
        phonePanel.add(new JLabel("Phone:"));
        phonePanel.add(phoneField);
        
        // Button row
        JPanel formButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        formButtonPanel.setBackground(new Color(240, 240, 240));
        formButtonPanel.add(addButton);
        formButtonPanel.add(updateButton);
        formButtonPanel.add(deleteButton);
        formButtonPanel.add(clearButton);
        
        formPanel.add(namePanel);
        formPanel.add(emailPanel);
        formPanel.add(phonePanel);
        formPanel.add(formButtonPanel);
        
        return formPanel;
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)), 
            "Registered Customers"
        ));
        tablePanel.setBackground(Color.WHITE);
        
        JScrollPane tableScrollPane = new JScrollPane(customersTable);
        tableScrollPane.setPreferredSize(new Dimension(700, 250));
        
        // Results counter at top of table
        JPanel resultsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        resultsPanel.setBackground(Color.WHITE);
        resultsPanel.add(resultsLabel);
        
        tablePanel.add(resultsPanel, BorderLayout.NORTH);
        tablePanel.add(tableScrollPane, BorderLayout.CENTER);
        
        return tablePanel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(Color.WHITE);
        
        // Add some statistics
        JLabel statsLabel = new JLabel("Use this panel to manage customer registrations and information");
        statsLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        statsLabel.setForeground(Color.GRAY);
        buttonPanel.add(statsLabel);
        
        return buttonPanel;
    }

    private void setupEventListeners() {
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchCustomers();
            }
        });

        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadAllCustomers();
            }
        });

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addCustomer();
            }
        });

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateCustomer();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteCustomer();
            }
        });

        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearForm();
            }
        });
        
        // Table selection listener
        customersTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = customersTable.getSelectedRow();
                if (selectedRow != -1) {
                    loadCustomerFromTable(selectedRow);
                }
            }
        });
        
        // Enter key in search field triggers search
        searchField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchCustomers();
            }
        });
    }

    private void loadAllCustomers() {
        List<Customer> customers = customerService.getAllCustomers();
        updateTable(customers);
        clearForm();
    }

    private void searchCustomers() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            loadAllCustomers();
            return;
        }
        
        List<Customer> customers = customerService.searchCustomersByName(searchTerm);
        updateTable(customers);
        
        if (customers.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "No customers found matching: " + searchTerm, 
                "No Results", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void updateTable(List<Customer> customers) {
        DefaultTableModel model = (DefaultTableModel) customersTable.getModel();
        model.setRowCount(0); // Clear existing rows
        
        for (Customer customer : customers) {
            model.addRow(new Object[]{
                customer.getCustomerId(),
                customer.getName(),
                customer.getEmail(),
                customer.getPhone(),
                "2024-01-01" // Placeholder - you could add created_at to Customer model
            });
        }
        
        // Update results counter
        resultsLabel.setText("Total Customers: " + customers.size());
        resultsLabel.setForeground(customers.isEmpty() ? Color.RED : new Color(0, 100, 0));
    }

    private void loadCustomerFromTable(int row) {
        int customerId = (int) customersTable.getValueAt(row, 0);
        String name = (String) customersTable.getValueAt(row, 1);
        String email = (String) customersTable.getValueAt(row, 2);
        String phone = (String) customersTable.getValueAt(row, 3);
        
        nameField.setText(name);
        emailField.setText(email);
        phoneField.setText(phone);
        
        selectedCustomer = new Customer(customerId, name, email, phone);
        
        // Update button states
        updateButton.setEnabled(true);
        deleteButton.setEnabled(true);
        addButton.setEnabled(false);
    }

    private void addCustomer() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        
        // Validate input
        if (!customerService.validateCustomerData(name, email, phone)) {
            return;
        }
        
        // Check if customer already exists
        Customer existingCustomer = customerService.findCustomerByEmail(email);
        if (existingCustomer != null) {
            JOptionPane.showMessageDialog(this,
                "Customer with email '" + email + "' already exists!\n" +
                "Please use a different email or update the existing customer.",
                "Customer Exists",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Create new customer
        Customer newCustomer = customerService.createCustomer(name, email, phone);
        if (newCustomer != null) {
            JOptionPane.showMessageDialog(this,
                "Customer registered successfully!\n\n" +
                "Name: " + newCustomer.getName() + "\n" +
                "Email: " + newCustomer.getEmail() + "\n" +
                "Phone: " + newCustomer.getPhone() + "\n" +
                "Customer ID: " + newCustomer.getCustomerId(),
                "Registration Successful",
                JOptionPane.INFORMATION_MESSAGE);
            
            loadAllCustomers(); // Refresh table
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this,
                "Failed to register customer. Please try again.",
                "Registration Failed",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateCustomer() {
        if (selectedCustomer == null) {
            JOptionPane.showMessageDialog(this,
                "Please select a customer to update!",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        
        // Validate input
        if (!customerService.validateCustomerData(name, email, phone)) {
            return;
        }
        
        int confirmation = JOptionPane.showConfirmDialog(this,
            "Update customer information?\n\n" +
            "Customer ID: " + selectedCustomer.getCustomerId() + "\n" +
            "Name: " + selectedCustomer.getName() + " → " + name + "\n" +
            "Email: " + selectedCustomer.getEmail() + " → " + email + "\n" +
            "Phone: " + selectedCustomer.getPhone() + " → " + phone,
            "Confirm Update",
            JOptionPane.YES_NO_OPTION);
            
        if (confirmation == JOptionPane.YES_OPTION) {
            boolean success = customerService.updateCustomer(
                selectedCustomer.getCustomerId(), name, email, phone);
            
            if (success) {
                JOptionPane.showMessageDialog(this,
                    "Customer information updated successfully!",
                    "Update Successful",
                    JOptionPane.INFORMATION_MESSAGE);
                
                loadAllCustomers(); // Refresh table
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Failed to update customer information.",
                    "Update Failed",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteCustomer() {
        if (selectedCustomer == null) {
            JOptionPane.showMessageDialog(this,
                "Please select a customer to delete!",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirmation = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this customer?\n\n" +
            "Customer ID: " + selectedCustomer.getCustomerId() + "\n" +
            "Name: " + selectedCustomer.getName() + "\n" +
            "Email: " + selectedCustomer.getEmail() + "\n\n" +
            "This action cannot be undone!",
            "Confirm Deletion",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
            
        if (confirmation == JOptionPane.YES_OPTION) {
            boolean success = customerService.deleteCustomer(selectedCustomer.getCustomerId());
            
            if (success) {
                JOptionPane.showMessageDialog(this,
                    "Customer deleted successfully!",
                    "Deletion Successful",
                    JOptionPane.INFORMATION_MESSAGE);
                
                loadAllCustomers(); // Refresh table
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Cannot delete customer. They may have active bookings.",
                    "Deletion Failed",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearForm() {
        nameField.setText("");
        emailField.setText("");
        phoneField.setText("");
        selectedCustomer = null;
        
        // Update button states
        updateButton.setEnabled(false);
        deleteButton.setEnabled(false);
        addButton.setEnabled(true);
        
        // Clear table selection
        customersTable.clearSelection();
    }

    private void setDialogProperties() {
        setSize(800, 700);
        setLocationRelativeTo(getParent());
        setResizable(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }
                                    }
