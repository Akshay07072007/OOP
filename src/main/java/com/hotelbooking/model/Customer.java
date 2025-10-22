 package com.hotelbooking.model;

/**
 * Customer class representing a hotel customer entity
 * Demonstrates OOP Encapsulation and data validation
 */
public class Customer {
    // Private fields - Encapsulation
    private int customerId;
    private String name;
    private String email;
    private String phone;

    // Constructors
    public Customer() {
        // Default constructor
    }

    public Customer(int customerId, String name, String email, String phone) {
        this.customerId = customerId;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    // Constructor without ID (for new customers)
    public Customer(String name, String email, String phone) {
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    // Getter and Setter methods
    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name != null && !name.trim().isEmpty()) {
            this.name = name;
        } else {
            System.out.println("Error: Name cannot be empty");
        }
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (isValidEmail(email)) {
            this.email = email;
        } else {
            System.out.println("Error: Invalid email format");
        }
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        if (phone != null && phone.matches("\\d{10}")) {
            this.phone = phone;
        } else {
            System.out.println("Error: Phone must be 10 digits");
        }
    }

    // Validation methods
    private boolean isValidEmail(String email) {
        return email != null && email.contains("@") && email.contains(".");
    }

    // Business logic methods
    public boolean hasValidInfo() {
        return name != null && !name.trim().isEmpty() &&
               isValidEmail(email) &&
               phone != null && phone.matches("\\d{10}");
    }

    // toString method
    @Override
    public String toString() {
        return String.format("Customer ID: %d | Name: %s | Email: %s | Phone: %s",
                customerId, name, email, phone);
    }

    // equals method for comparing customers
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Customer customer = (Customer) obj;
        return customerId == customer.customerId ||
               email.equals(customer.email);
    }
    }
