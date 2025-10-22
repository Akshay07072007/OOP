 package com.hotelbooking.model;

/**
 * Room class representing a hotel room entity
 * Demonstrates OOP Encapsulation with private fields and public getters/setters
 */ 
public class Room {
    // Private fields - Encapsulation
    private int roomNumber;
    private String roomType;
    private double price;
    private boolean isAvailable;
    private String amenities;

    // Default constructor
    public Room() {
    }

    // Parameterized constructor
    public Room(int roomNumber, String roomType, double price, boolean isAvailable, String amenities) {
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.price = price;
        this.isAvailable = isAvailable;
        this.amenities = amenities;
    }

    // Getter and Setter methods - Encapsulation
    public int getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public String getAmenities() {
        return amenities;
    }

    public void setAmenities(String amenities) {
        this.amenities = amenities;
    }

    // toString method for displaying room information
    @Override
    public String toString() {
        return String.format("Room %d | Type: %s | Price: $%.2f | Status: %s | Amenities: %s",
                roomNumber, roomType, price, 
                isAvailable ? "Available" : "Occupied", 
                amenities);
    }

    // Utility method to check if room is affordable
    public boolean isAffordable(double budget) {
        return price <= budget;
    }

    // Utility method to check if room has specific amenity
    public boolean hasAmenity(String amenity) {
        return amenities != null && amenities.toLowerCase().contains(amenity.toLowerCase());
    }
}
