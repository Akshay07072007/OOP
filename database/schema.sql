-- Hotel Booking System Database Schema
-- Created for OOP Project - Team KNP24CS012 to KNP24CS052

CREATE DATABASE IF NOT EXISTS hotel_booking_system;
USE hotel_booking_system;

-- Customers table
CREATE TABLE customers (
    customer_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(15) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Rooms table
CREATE TABLE rooms (
    room_number INT PRIMARY KEY,
    room_type VARCHAR(50) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    is_available BOOLEAN DEFAULT TRUE,
    amenities TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Bookings table
CREATE TABLE bookings (
    booking_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT,
    room_number INT,
    check_in_date DATE NOT NULL,
    check_out_date DATE NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    status ENUM('CONFIRMED', 'CANCELLED') DEFAULT 'CONFIRMED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id),
    FOREIGN KEY (room_number) REFERENCES rooms(room_number)
);

-- Insert sample rooms data
INSERT INTO rooms (room_number, room_type, price, amenities) VALUES
(101, 'Standard', 100.00, 'WiFi, TV, AC, Bathroom'),
(102, 'Standard', 100.00, 'WiFi, TV, AC, Bathroom'),
(103, 'Standard', 100.00, 'WiFi, TV, AC, Bathroom'),
(201, 'Deluxe', 150.00, 'WiFi, TV, AC, Mini Bar, Bathroom'),
(202, 'Deluxe', 150.00, 'WiFi, TV, AC, Mini Bar, Bathroom'),
(301, 'Suite', 250.00, 'WiFi, TV, AC, Mini Bar, Jacuzzi, Living Room');

-- Insert sample customers
INSERT INTO customers (name, email, phone) VALUES
('John Doe', 'john.doe@example.com', '1234567890'),
('Jane Smith', 'jane.smith@example.com', '0987654321'),
('Robert Johnson', 'robert.j@example.com', '1122334455');

-- Insert sample bookings
INSERT INTO bookings (customer_id, room_number, check_in_date, check_out_date, total_amount, status) VALUES
(1, 101, '2024-02-01', '2024-02-05', 400.00, 'CONFIRMED'),
(2, 201, '2024-02-10', '2024-02-12', 300.00, 'CONFIRMED');
