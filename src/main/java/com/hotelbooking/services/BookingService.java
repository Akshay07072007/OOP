package com.hotelbooking.services;

import com.hotelbooking.model.Booking;
import com.hotelbooking.utils.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * BookingService class for handling booking-related operations
 * Demonstrates OOP with database operations and business logic
 */
public class BookingService {
    private Connection connection;

    public BookingService() {
        this.connection = DatabaseConnection.getConnection();
    }

    /**
     * Create a new booking
     * @param customerId The customer ID
     * @param roomNumber The room number
     * @param checkInDate Check-in date
     * @param checkOutDate Check-out date
     * @param roomPrice Room price per night
     * @return The created Booking object
     */
    public Booking makeBooking(int customerId, int roomNumber, 
                             LocalDate checkInDate, LocalDate checkOutDate, 
                             double roomPrice) {
        String sql = "INSERT INTO bookings (customer_id, room_number, check_in_date, check_out_date, total_amount, status) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            // Calculate total amount
            long numberOfDays = java.time.temporal.ChronoUnit.DAYS.between(checkInDate, checkOutDate);
            double totalAmount = numberOfDays * roomPrice;
            
            statement.setInt(1, customerId);
            statement.setInt(2, roomNumber);
            statement.setDate(3, Date.valueOf(checkInDate));
            statement.setDate(4, Date.valueOf(checkOutDate));
            statement.setDouble(5, totalAmount);
            statement.setString(6, Booking.STATUS_CONFIRMED);
            
            int affectedRows = statement.executeUpdate();
            
            if (affectedRows > 0) {
                // Get the generated booking ID
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int bookingId = generatedKeys.getInt(1);
                        
                        // Update room availability
                        updateRoomAvailability(roomNumber, false);
                        
                        System.out.println("✅ Booking created successfully! Booking ID: " + bookingId);
                        return new Booking(bookingId, customerId, roomNumber, checkInDate, checkOutDate, Booking.STATUS_CONFIRMED);
                    }
                }
            }
            
        } catch (SQLException e) {
            System.out.println("❌ Error creating booking: " + e.getMessage());
        }
        
        return null;
    }

    /**
     * Create booking with customer email (finds or creates customer)
     * @param customerName Customer name
     * @param customerEmail Customer email
     * @param customerPhone Customer phone
     * @param roomNumber Room number
     * @param checkInDate Check-in date
     * @param checkOutDate Check-out date
     * @param roomPrice Room price per night
     * @return The created Booking object
     */
    public Booking makeBooking(String customerName, String customerEmail, String customerPhone,
                             int roomNumber, LocalDate checkInDate, LocalDate checkOutDate, 
                             double roomPrice) {
        try {
            // First, find or create customer
            CustomerService customerService = new CustomerService();
            int customerId = customerService.findOrCreateCustomer(customerName, customerEmail, customerPhone);
            
            if (customerId > 0) {
                return makeBooking(customerId, roomNumber, checkInDate, checkOutDate, roomPrice);
            }
            
        } catch (Exception e) {
            System.out.println("❌ Error in booking process: " + e.getMessage());
        }
        
        return null;
    }

    /**
     * Cancel a booking
     * @param bookingId The booking ID to cancel
     * @return true if cancellation successful, false otherwise
     */
    public boolean cancelBooking(int bookingId) {
        String sql = "UPDATE bookings SET status = ? WHERE booking_id = ? AND status = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, Booking.STATUS_CANCELLED);
            statement.setInt(2, bookingId);
            statement.setString(3, Booking.STATUS_CONFIRMED);
            
            int affectedRows = statement.executeUpdate();
            
            if (affectedRows > 0) {
                // Get room number to make it available again
                int roomNumber = getRoomNumberFromBooking(bookingId);
                if (roomNumber > 0) {
                    updateRoomAvailability(roomNumber, true);
                }
                
                System.out.println("✅ Booking " + bookingId + " cancelled successfully!");
                return true;
            } else {
                System.out.println("❌ Booking " + bookingId + " not found or already cancelled!");
            }
            
        } catch (SQLException e) {
            System.out.println("❌ Error cancelling booking: " + e.getMessage());
        }
        
        return false;
    }

    /**
     * Get all bookings for a specific customer
     * @param customerId The customer ID
     * @return List of customer's bookings
     */
    public List<Booking> getBookingsByCustomer(int customerId) {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM bookings WHERE customer_id = ? ORDER BY check_in_date DESC";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, customerId);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Booking booking = extractBookingFromResultSet(resultSet);
                    bookings.add(booking);
                }
            }
            
        } catch (SQLException e) {
            System.out.println("❌ Error fetching customer bookings: " + e.getMessage());
        }
        
        return bookings;
    }

    /**
     * Get bookings by customer email
     * @param customerEmail The customer email
     * @return List of customer's bookings
     */
    public List<Booking> getBookingsByCustomerEmail(String customerEmail) {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT b.* FROM bookings b " +
                    "JOIN customers c ON b.customer_id = c.customer_id " +
                    "WHERE c.email = ? ORDER BY b.check_in_date DESC";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, customerEmail);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Booking booking = extractBookingFromResultSet(resultSet);
                    bookings.add(booking);
                }
            }
            
        } catch (SQLException e) {
            System.out.println("❌ Error fetching bookings by email: " + e.getMessage());
        }
        
        return bookings;
    }

    /**
     * Get all bookings
     * @return List of all bookings
     */
    public List<Booking> getAllBookings() {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM bookings ORDER BY booking_id DESC";
        
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            
            while (resultSet.next()) {
                Booking booking = extractBookingFromResultSet(resultSet);
                bookings.add(booking);
            }
            
        } catch (SQLException e) {
            System.out.println("❌ Error fetching all bookings: " + e.getMessage());
        }
        
        return bookings;
    }

    /**
     * Get booking by ID
     * @param bookingId The booking ID
     * @return Booking object if found, null otherwise
     */
    public Booking getBookingById(int bookingId) {
        String sql = "SELECT * FROM bookings WHERE booking_id = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, bookingId);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return extractBookingFromResultSet(resultSet);
                }
            }
            
        } catch (SQLException e) {
            System.out.println("❌ Error fetching booking: " + e.getMessage());
        }
        
        return null;
    }

    /**
     * Update room availability
     * @param roomNumber The room number
     * @param isAvailable New availability status
     */
    private void updateRoomAvailability(int roomNumber, boolean isAvailable) {
        RoomService roomService = new RoomService();
        roomService.updateRoomAvailability(roomNumber, isAvailable);
    }

    /**
     * Get room number from booking
     * @param bookingId The booking ID
     * @return Room number
     */
    private int getRoomNumberFromBooking(int bookingId) {
        String sql = "SELECT room_number FROM bookings WHERE booking_id = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, bookingId);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("room_number");
                }
            }
            
        } catch (SQLException e) {
            System.out.println("❌ Error getting room number from booking: " + e.getMessage());
        }
        
        return -1;
    }

    /**
     * Extract Booking object from ResultSet
     */
    private Booking extractBookingFromResultSet(ResultSet resultSet) throws SQLException {
        int bookingId = resultSet.getInt("booking_id");
        int customerId = resultSet.getInt("customer_id");
        int roomNumber = resultSet.getInt("room_number");
        LocalDate checkInDate = resultSet.getDate("check_in_date").toLocalDate();
        LocalDate checkOutDate = resultSet.getDate("check_out_date").toLocalDate();
        double totalAmount = resultSet.getDouble("total_amount");
        String status = resultSet.getString("status");
        
        Booking booking = new Booking(bookingId, customerId, roomNumber, checkInDate, checkOutDate, status);
        booking.setTotalAmount(totalAmount);
        
        return booking;
    }

    /**
     * Check if room is available for given dates
     * @param roomNumber The room number
     * @param checkInDate Check-in date
     * @param checkOutDate Check-out date
     * @return true if available, false otherwise
     */
    public boolean isRoomAvailable(int roomNumber, LocalDate checkInDate, LocalDate checkOutDate) {
        String sql = "SELECT COUNT(*) FROM bookings " +
                    "WHERE room_number = ? AND status = 'CONFIRMED' " +
                    "AND ((check_in_date BETWEEN ? AND ?) OR (check_out_date BETWEEN ? AND ?) " +
                    "OR (check_in_date <= ? AND check_out_date >= ?))";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, roomNumber);
            statement.setDate(2, Date.valueOf(checkInDate));
            statement.setDate(3, Date.valueOf(checkOutDate.minusDays(1)));
            statement.setDate(4, Date.valueOf(checkInDate.plusDays(1)));
            statement.setDate(5, Date.valueOf(checkOutDate));
            statement.setDate(6, Date.valueOf(checkInDate));
            statement.setDate(7, Date.valueOf(checkOutDate));
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) == 0;
                }
            }
            
        } catch (SQLException e) {
            System.out.println("❌ Error checking room availability: " + e.getMessage());
        }
        
        return false;
    }

    /**
     * Get booking statistics
     */
    public void displayBookingStatistics() {
        String sql = "SELECT " +
                    "COUNT(*) as total_bookings, " +
                    "COUNT(CASE WHEN status = 'CONFIRMED' THEN 1 END) as confirmed_bookings, " +
                    "COUNT(CASE WHEN status = 'CANCELLED' THEN 1 END) as cancelled_bookings, " +
                    "SUM(CASE WHEN status = 'CONFIRMED' THEN total_amount ELSE 0 END) as total_revenue " +
                    "FROM bookings";
        
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            
            if (resultSet.next()) {
                System.out.println("=== Booking Statistics ===");
                System.out.println("Total Bookings: " + resultSet.getInt("total_bookings"));
                System.out.println("Confirmed: " + resultSet.getInt("confirmed_bookings"));
                System.out.println("Cancelled: " + resultSet.getInt("cancelled_bookings"));
                System.out.println("Total Revenue: $" + resultSet.getDouble("total_revenue"));
            }
            
        } catch (SQLException e) {
            System.out.println("❌ Error fetching booking statistics: " + e.getMessage());
        }
    }

    /**
     * Close the database connection
     */
    public void close() {
        DatabaseConnection.closeConnection();
    }
                     }
