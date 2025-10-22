 package com.hotelbooking.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Booking class representing a hotel booking transaction
 * Demonstrates OOP concepts with business logic methods
 */
public class Booking {
    // Private fields - Encapsulation
    private int bookingId;
    private int customerId;
    private int roomNumber;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private double totalAmount;
    private String status;

    // Constants for booking status
    public static final String STATUS_CONFIRMED = "CONFIRMED";
    public static final String STATUS_CANCELLED = "CANCELLED";

    // Constructors
    public Booking() {
        // Default constructor
    }

    public Booking(int bookingId, int customerId, int roomNumber, 
                  LocalDate checkInDate, LocalDate checkOutDate, String status) {
        this.bookingId = bookingId;
        this.customerId = customerId;
        this.roomNumber = roomNumber;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.status = status;
        this.totalAmount = calculateTotalAmount(100.0); // Default price, will be updated
    }

    // Constructor for new bookings
    public Booking(int customerId, int roomNumber, 
                  LocalDate checkInDate, LocalDate checkOutDate, double roomPrice) {
        this.customerId = customerId;
        this.roomNumber = roomNumber;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.status = STATUS_CONFIRMED;
        this.totalAmount = calculateTotalAmount(roomPrice);
    }

    // Getter and Setter methods
    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }

    public LocalDate getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(LocalDate checkInDate) {
        this.checkInDate = checkInDate;
    }

    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(LocalDate checkOutDate) {
        if (checkOutDate != null && checkInDate != null && 
            checkOutDate.isAfter(checkInDate)) {
            this.checkOutDate = checkOutDate;
        } else {
            System.out.println("Error: Check-out date must be after check-in date");
        }
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        if (STATUS_CONFIRMED.equals(status) || STATUS_CANCELLED.equals(status)) {
            this.status = status;
        } else {
            System.out.println("Error: Invalid booking status");
        }
    }

    // Business logic methods
    public double calculateTotalAmount(double roomPrice) {
        if (checkInDate != null && checkOutDate != null) {
            long numberOfDays = ChronoUnit.DAYS.between(checkInDate, checkOutDate);
            return numberOfDays * roomPrice;
        }
        return 0.0;
    }

    public long getNumberOfDays() {
        if (checkInDate != null && checkOutDate != null) {
            return ChronoUnit.DAYS.between(checkInDate, checkOutDate);
        }
        return 0;
    }

    public boolean isActive() {
        return STATUS_CONFIRMED.equals(status) && 
               checkInDate != null && 
               checkInDate.isAfter(LocalDate.now());
    }

    public boolean canBeCancelled() {
        return STATUS_CONFIRMED.equals(status) && 
               checkInDate != null && 
               checkInDate.isAfter(LocalDate.now().plusDays(1));
    }

    public void cancelBooking() {
        if (canBeCancelled()) {
            this.status = STATUS_CANCELLED;
            System.out.println("Booking cancelled successfully.");
        } else {
            System.out.println("Cannot cancel booking. Check cancellation policy.");
        }
    }

    // Validation method
    public boolean isValidBooking() {
        return customerId > 0 && 
               roomNumber > 0 && 
               checkInDate != null && 
               checkOutDate != null && 
               checkOutDate.isAfter(checkInDate) && 
               totalAmount >= 0;
    }

    // toString method
    @Override
    public String toString() {
        return String.format("Booking ID: %d | Customer: %d | Room: %d | Dates: %s to %s | Amount: $%.2f | Status: %s",
                bookingId, customerId, roomNumber, checkInDate, checkOutDate, totalAmount, status);
    }

    // Utility method to get booking summary
    public String getBookingSummary() {
        return String.format("Room %d booked from %s to %s. Total: $%.2f",
                roomNumber, checkInDate, checkOutDate, totalAmount);
    }
  }
