package com.agrirent.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class Equipment {
    private Long id;

    @NotBlank(message = "Equipment name is required")
    private String name;

    @NotNull(message = "Category is required")
    private EquipmentCategory category;

    @NotNull(message = "Daily rate is required")
    @Min(value = 100, message = "Daily rate must be at least ₹100")
    private Double dailyRate;

    @NotNull(message = "Hourly rate is required")
    @Min(value = 20, message = "Hourly rate must be at least ₹20")
    private Double hourlyRate;

    @NotBlank(message = "Location is required")
    private String location;

    @NotBlank(message = "Owner name is required")
    private String ownerName;

    private String ownerContact;

    private EquipmentStatus status = EquipmentStatus.AVAILABLE;

    private String description;

    public Equipment() {
    }

    public Equipment(Long id, String name, EquipmentCategory category, Double dailyRate, Double hourlyRate,
                     String location, String ownerName, String ownerContact, EquipmentStatus status, String description) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.dailyRate = dailyRate;
        this.hourlyRate = hourlyRate;
        this.location = location;
        this.ownerName = ownerName;
        this.ownerContact = ownerContact;
        this.status = status;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public EquipmentCategory getCategory() {
        return category;
    }

    public void setCategory(EquipmentCategory category) {
        this.category = category;
    }

    public Double getDailyRate() {
        return dailyRate;
    }

    public void setDailyRate(Double dailyRate) {
        this.dailyRate = dailyRate;
    }

    public Double getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(Double hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerContact() {
        return ownerContact;
    }

    public void setOwnerContact(String ownerContact) {
        this.ownerContact = ownerContact;
    }

    public EquipmentStatus getStatus() {
        return status;
    }

    public void setStatus(EquipmentStatus status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
