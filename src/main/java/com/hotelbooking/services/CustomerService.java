package com.hotelbooking.services;

import com.hotelbooking.model.Customer;
import com.hotelbooking.utils.DatabaseConnection;

import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * CustomerService class for handling customer-related operations
 * Demonstrates OOP with database CRUD operations and business logic
 */
public class CustomerService {
    private Connection connection;

    public CustomerService() {
        this.connection = DatabaseConnection.getConnection();
    }

    /**
     * Create a new customer
     * @param name Customer name
     * @param email Customer email
     * @param phone Customer phone
     * @return The created Customer object
     */
    public Customer createCustomer(String name, String email, String phone) {
        String sql = "INSERT INTO customers (name, email, phone) VALUES (?, ?, ?)";
        
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, name);
            statement.setString(2, email);
            statement.setString(3, phone);
            
            int affectedRows = statement.executeUpdate();
            
            if (affectedRows > 0) {
                // Get the generated customer ID
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int customerId = generatedKeys.getInt(1);
                        System.out.println("✅ Customer created successfully! Customer ID: " + customerId);
                        return new Customer(customerId, name, email, phone);
                    }
                }
            }
            
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) { // Duplicate entry error code
                System.out.println("❌ Customer with email '" + email + "' already exists!");
            } else {
                System.out.println("❌ Error creating customer: " + e.getMessage());
            }
        }
        
        return null;
    }

    /**
     * Find customer by email
     * @param email Customer email
     * @return Customer object if found, null otherwise
     */
    public Customer findCustomerByEmail(String email) {
        String sql = "SELECT * FROM customers WHERE email = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return extractCustomerFromResultSet(resultSet);
                }
            }
            
        } catch (SQLException e) {
            System.out.println("❌ Error finding customer by email: " + e.getMessage());
        }
        
        return null;
    }

    /**
     * Find customer by ID
     * @param customerId Customer ID
     * @return Customer object if found, null otherwise
     */
    public Customer findCustomerById(int customerId) {
        String sql = "SELECT * FROM customers WHERE customer_id = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, customerId);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return extractCustomerFromResultSet(resultSet);
                }
            }
            
        } catch (SQLException e) {
            System.out.println("❌ Error finding customer by ID: " + e.getMessage());
        }
        
        return null;
    }

    /**
     * Find or create customer - used by booking system
     * @param name Customer name
     * @param email Customer email
     * @param phone Customer phone
     * @return Customer ID (existing or new)
     */
    public int findOrCreateCustomer(String name, String email, String phone) {
        // First, try to find existing customer by email
        Customer existingCustomer = findCustomerByEmail(email);
        if (existingCustomer != null) {
            System.out.println("✅ Found existing customer: " + existingCustomer.getName());
            return existingCustomer.getCustomerId();
        }
        
        // If not found, create new customer
        Customer newCustomer = createCustomer(name, email, phone);
        if (newCustomer != null) {
            return newCustomer.getCustomerId();
        }
        
        return -1; // Error case
    }

    /**
     * Update customer information
     * @param customerId Customer ID
     * @param name New name
     * @param email New email
     * @param phone New phone
     * @return true if update successful, false otherwise
     */
    public boolean updateCustomer(int customerId, String name, String email, String phone) {
        String sql = "UPDATE customers SET name = ?, email = ?, phone = ? WHERE customer_id = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, name);
            statement.setString(2, email);
            statement.setString(3, phone);
            statement.setInt(4, customerId);
            
            int affectedRows = statement.executeUpdate();
            boolean success = affectedRows > 0;
            
            if (success) {
                System.out.println("✅ Customer " + customerId + " updated successfully!");
            } else {
                System.out.println("❌ Customer " + customerId + " not found!");
            }
            
            return success;
            
        } catch (SQLException e) {
            System.out.println("❌ Error updating customer: " + e.getMessage());
            return false;
        }
    }

    /**
     * Delete customer
     * @param customerId Customer ID to delete
     * @return true if deletion successful, false otherwise
     */
    public boolean deleteCustomer(int customerId) {
        // First check if customer has any bookings
        if (hasActiveBookings(customerId)) {
            System.out.println("❌ Cannot delete customer with active bookings!");
            return false;
        }
        
        String sql = "DELETE FROM customers WHERE customer_id = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, customerId);
            
            int affectedRows = statement.executeUpdate();
            boolean success = affectedRows > 0;
            
            if (success) {
                System.out.println("✅ Customer " + customerId + " deleted successfully!");
            } else {
                System.out.println("❌ Customer " + customerId + " not found!");
            }
            
            return success;
            
        } catch (SQLException e) {
            System.out.println("❌ Error deleting customer: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get all customers
     * @return List of all customers
     */
    public List<Customer> getAllCustomers() {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM customers ORDER BY name";
        
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            
            while (resultSet.next()) {
                Customer customer = extractCustomerFromResultSet(resultSet);
                customers.add(customer);
            }
            
            System.out.println("✅ Found " + customers.size() + " customers");
            
        } catch (SQLException e) {
            System.out.println("❌ Error fetching customers: " + e.getMessage());
        }
        
        return customers;
    }

    /**
     * Search customers by name
     * @param name Name to search for
     * @return List of matching customers
     */
    public List<Customer> searchCustomersByName(String name) {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM customers WHERE name LIKE ? ORDER BY name";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, "%" + name + "%");
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Customer customer = extractCustomerFromResultSet(resultSet);
                    customers.add(customer);
                }
            }
            
            System.out.println("✅ Found " + customers.size() + " customers matching '" + name + "'");
            
        } catch (SQLException e) {
            System.out.println("❌ Error searching customers: " + e.getMessage());
        }
        
        return customers;
    }

    /**
     * Check if customer has active bookings
     * @param customerId Customer ID
     * @return true if has active bookings, false otherwise
     */
    private boolean hasActiveBookings(int customerId) {
        String sql = "SELECT COUNT(*) FROM bookings WHERE customer_id = ? AND status = 'CONFIRMED'";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, customerId);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
            
        } catch (SQLException e) {
            System.out.println("❌ Error checking customer bookings: " + e.getMessage());
        }
        
        return false;
    }

    /**
     * Extract Customer object from ResultSet
     */
    private Customer extractCustomerFromResultSet(ResultSet resultSet) throws SQLException {
        int customerId = resultSet.getInt("customer_id");
        String name = resultSet.getString("name");
        String email = resultSet.getString("email");
        String phone = resultSet.getString("phone");
        
        return new Customer(customerId, name, email, phone);
    }

    /**
     * Validate customer data
     * @param name Customer name
     * @param email Customer email
     * @param phone Customer phone
     * @return true if valid, false otherwise
     */
    public boolean validateCustomerData(String name, String email, String phone) {
        if (name == null || name.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Name cannot be empty!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        if (email == null || !email.contains("@") || !email.contains(".")) {
            JOptionPane.showMessageDialog(null, "Please enter a valid email address!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        if (phone == null || !phone.matches("\\d{10}")) {
            JOptionPane.showMessageDialog(null, "Phone number must be 10 digits!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        return true;
    }

    /**
     * Get customer statistics
     */
    public void displayCustomerStatistics() {
        String sql = "SELECT " +
                    "COUNT(*) as total_customers, " +
                    "COUNT(DISTINCT b.customer_id) as customers_with_bookings, " +
                    "AVG(booking_count) as avg_bookings_per_customer " +
                    "FROM customers c " +
                    "LEFT JOIN (SELECT customer_id, COUNT(*) as booking_count FROM bookings GROUP BY customer_id) b " +
                    "ON c.customer_id = b.customer_id";
        
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            
            if (resultSet.next()) {
                System.out.println("=== Customer Statistics ===");
                System.out.println("Total Customers: " + resultSet.getInt("total_customers"));
                System.out.println("Customers with Bookings: " + resultSet.getInt("customers_with_bookings"));
                System.out.println("Avg Bookings per Customer: " + String.format("%.2f", resultSet.getDouble("avg_bookings_per_customer")));
            }
            
        } catch (SQLException e) {
            System.out.println("❌ Error fetching customer statistics: " + e.getMessage());
        }
    }

    /**
     * Close the database connection
     */
    public void close() {
        DatabaseConnection.closeConnection();
    }
        }
