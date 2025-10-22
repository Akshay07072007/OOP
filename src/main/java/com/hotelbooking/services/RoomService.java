package com.hotelbooking.services;

import com.hotelbooking.model.Room;
import com.hotelbooking.utils.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * RoomService class for handling room-related operations
 * Demonstrates OOP principles and JDBC database operations
 */
public class RoomService {
    private Connection connection;

    // Constructor
    public RoomService() {
        this.connection = DatabaseConnection.getConnection();
    }

    /**
     * Get all available rooms
     * @return List of available rooms
     */
    public List<Room> getAvailableRooms() {
        List<Room> availableRooms = new ArrayList<>();
        String sql = "SELECT * FROM rooms WHERE is_available = true ORDER BY room_number";

        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Room room = extractRoomFromResultSet(resultSet);
                availableRooms.add(room);
            }
            
            System.out.println("✅ Found " + availableRooms.size() + " available rooms");
            
        } catch (SQLException e) {
            System.out.println("❌ Error fetching available rooms: " + e.getMessage());
        }
        
        return availableRooms;
    }

    /**
     * Get all rooms (both available and occupied)
     * @return List of all rooms
     */
    public List<Room> getAllRooms() {
        List<Room> allRooms = new ArrayList<>();
        String sql = "SELECT * FROM rooms ORDER BY room_number";

        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Room room = extractRoomFromResultSet(resultSet);
                allRooms.add(room);
            }
            
        } catch (SQLException e) {
            System.out.println("❌ Error fetching all rooms: " + e.getMessage());
        }
        
        return allRooms;
    }

    /**
     * Find room by room number
     * @param roomNumber the room number to search for
     * @return Room object if found, null otherwise
     */
    public Room getRoomByNumber(int roomNumber) {
        String sql = "SELECT * FROM rooms WHERE room_number = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, roomNumber);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return extractRoomFromResultSet(resultSet);
                }
            }
            
        } catch (SQLException e) {
            System.out.println("❌ Error fetching room " + roomNumber + ": " + e.getMessage());
        }
        
        return null;
    }

    /**
     * Search rooms by type and maximum price
     * @param roomType type of room to search for (can be null for any type)
     * @param maxPrice maximum price (can be 0 for any price)
     * @return List of matching rooms
     */
    public List<Room> searchRooms(String roomType, double maxPrice) {
        List<Room> matchingRooms = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM rooms WHERE is_available = true");
        List<Object> parameters = new ArrayList<>();

        if (roomType != null && !roomType.trim().isEmpty()) {
            sql.append(" AND room_type = ?");
            parameters.add(roomType);
        }

        if (maxPrice > 0) {
            sql.append(" AND price <= ?");
            parameters.add(maxPrice);
        }

        sql.append(" ORDER BY price");

        try (PreparedStatement statement = connection.prepareStatement(sql.toString())) {
            // Set parameters
            for (int i = 0; i < parameters.size(); i++) {
                statement.setObject(i + 1, parameters.get(i));
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Room room = extractRoomFromResultSet(resultSet);
                    matchingRooms.add(room);
                }
            }
            
            System.out.println("✅ Found " + matchingRooms.size() + " matching rooms");
            
        } catch (SQLException e) {
            System.out.println("❌ Error searching rooms: " + e.getMessage());
        }
        
        return matchingRooms;
    }

    /**
     * Update room availability
     * @param roomNumber the room number to update
     * @param isAvailable new availability status
     * @return true if update successful, false otherwise
     */
    public boolean updateRoomAvailability(int roomNumber, boolean isAvailable) {
        String sql = "UPDATE rooms SET is_available = ? WHERE room_number = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setBoolean(1, isAvailable);
            statement.setInt(2, roomNumber);
            
            int rowsAffected = statement.executeUpdate();
            boolean success = rowsAffected > 0;
            
            if (success) {
                System.out.println("✅ Room " + roomNumber + " availability updated to: " + 
                                 (isAvailable ? "Available" : "Occupied"));
            } else {
                System.out.println("❌ Room " + roomNumber + " not found");
            }
            
            return success;
            
        } catch (SQLException e) {
            System.out.println("❌ Error updating room availability: " + e.getMessage());
            return false;
        }
    }

    /**
     * Add a new room to the system
     * @param room the room to add
     * @return true if successful, false otherwise
     */
    public boolean addRoom(Room room) {
        String sql = "INSERT INTO rooms (room_number, room_type, price, is_available, amenities) VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, room.getRoomNumber());
            statement.setString(2, room.getRoomType());
            statement.setDouble(3, room.getPrice());
            statement.setBoolean(4, room.isAvailable());
            statement.setString(5, room.getAmenities());
            
            int rowsAffected = statement.executeUpdate();
            boolean success = rowsAffected > 0;
            
            if (success) {
                System.out.println("✅ Room " + room.getRoomNumber() + " added successfully");
            }
            
            return success;
            
        } catch (SQLException e) {
            System.out.println("❌ Error adding room: " + e.getMessage());
            return false;
        }
    }

    /**
     * Helper method to extract Room object from ResultSet
     */
    private Room extractRoomFromResultSet(ResultSet resultSet) throws SQLException {
        int roomNumber = resultSet.getInt("room_number");
        String roomType = resultSet.getString("room_type");
        double price = resultSet.getDouble("price");
        boolean isAvailable = resultSet.getBoolean("is_available");
        String amenities = resultSet.getString("amenities");
        
        return new Room(roomNumber, roomType, price, isAvailable, amenities);
    }

    /**
     * Display all available rooms in a formatted way
     */
    public void displayAvailableRooms() {
        List<Room> availableRooms = getAvailableRooms();
        
        if (availableRooms.isEmpty()) {
            System.out.println("❌ No available rooms found.");
            return;
        }
        
        System.out.println("\n=== AVAILABLE ROOMS ===");
        System.out.println("+------------+-----------+---------+------------+----------------------+");
        System.out.println("| Room No.   | Type      | Price   | Status     | Amenities            |");
        System.out.println("+------------+-----------+---------+------------+----------------------+");
        
        for (Room room : availableRooms) {
            System.out.printf("| %-10d | %-9s | $%-6.2f | %-10s | %-20s |\n",
                    room.getRoomNumber(),
                    room.getRoomType(),
                    room.getPrice(),
                    room.isAvailable() ? "Available" : "Occupied",
                    room.getAmenities());
        }
        
        System.out.println("+------------+-----------+---------+------------+----------------------+");
        System.out.println("Total available rooms: " + availableRooms.size());
    }

    /**
     * Close the database connection
     */
    public void close() {
        DatabaseConnection.closeConnection();
    }
          }
