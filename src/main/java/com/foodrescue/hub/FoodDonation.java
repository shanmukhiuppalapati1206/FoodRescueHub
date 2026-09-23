package com.foodrescue.hub;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.util.ArrayList;
import java.util.List;

@Entity
public class FoodDonation implements Comparable<FoodDonation> {

    @Id
    private String donationId;
    private String donorId;
    private String donorName;
    private String foodItem;
    private int quantity;
    private int expiryValue;
    private String expiryUnit;
    private String dietaryType;
    private double donorLatitude;
    private double donorLongitude;
    private String donorLocation;

    private String status = "AVAILABLE";
    private String trackingStage = "AVAILABLE";
    private String acceptedNgoId;

    private long absoluteExpiryTimeMs;

    @ElementCollection
    private List<String> requestedByNgoIds = new ArrayList<>();

    @ElementCollection
    private List<String> rejectedNgoIds = new ArrayList<>();

    public FoodDonation() {}

    public FoodDonation(String donationId, String donorId, String donorName, String foodItem, int quantity, int expiryValue, String expiryUnit, String dietaryType, double donorLatitude, double donorLongitude, String donorLocation) {
        this.donationId = donationId;
        this.donorId = donorId;
        this.donorName = donorName;
        this.foodItem = foodItem;
        this.quantity = quantity;
        this.expiryValue = expiryValue;
        this.expiryUnit = expiryUnit;
        this.dietaryType = dietaryType;
        this.donorLatitude = donorLatitude;
        this.donorLongitude = donorLongitude;
        this.donorLocation = donorLocation;

        long multiplier = 3600_000L;
        if ("Days".equalsIgnoreCase(expiryUnit)) multiplier = 86400_000L;
        else if ("Weeks".equalsIgnoreCase(expiryUnit)) multiplier = 604800_000L;

        this.absoluteExpiryTimeMs = System.currentTimeMillis() + (long) expiryValue * multiplier;
    }

    @Override
    public int compareTo(FoodDonation other) {
        return Long.compare(this.absoluteExpiryTimeMs, other.absoluteExpiryTimeMs);
    }

    public String getDonationId() { return donationId; }
    public String getDonorId() { return donorId; }
    public String getDonorName() { return donorName; }
    public String getFoodItem() { return foodItem; }
    public int getQuantity() { return quantity; }
    public int getExpiryValue() { return expiryValue; }
    public String getExpiryUnit() { return expiryUnit; }
    public String getDietaryType() { return dietaryType; }
    public double getDonorLatitude() { return donorLatitude; }
    public double getDonorLongitude() { return donorLongitude; }
    public String getDonorLocation() { return donorLocation; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getTrackingStage() { return trackingStage; }
    public void setTrackingStage(String trackingStage) { this.trackingStage = trackingStage; }

    public String getAcceptedNgoId() { return acceptedNgoId; }
    public void setAcceptedNgoId(String acceptedNgoId) { this.acceptedNgoId = acceptedNgoId; }

    public long getAbsoluteExpiryTimeMs() { return absoluteExpiryTimeMs; }

    public List<String> getRequestedByNgoIds() { return requestedByNgoIds; }
    public List<String> getRejectedNgoIds() { return rejectedNgoIds; }
}