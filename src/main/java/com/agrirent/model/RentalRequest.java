package com.agrirent.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class RentalRequest {
    private Long id;

    @NotNull(message = "Equipment ID is required")
    private Long equipmentId;

    private String equipmentName;

    @NotBlank(message = "Farmer / Renter name is required")
    private String renterName;

    @NotBlank(message = "Contact phone is required")
    private String renterContact;

    @NotBlank(message = "Farm location is required")
    private String farmLocation;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "Duration in days is required")
    @Min(value = 1, message = "Duration must be at least 1 day")
    private Integer durationDays;

    private Double totalCost;

    private RentalStatus rentalStatus = RentalStatus.PENDING;

    private LocalDateTime requestTimestamp = LocalDateTime.now();

    public RentalRequest() {
    }

    public RentalRequest(Long id, Long equipmentId, String equipmentName, String renterName, String renterContact,
                         String farmLocation, LocalDate startDate, Integer durationDays, Double totalCost,
                         RentalStatus rentalStatus) {
        this.id = id;
        this.equipmentId = equipmentId;
        this.equipmentName = equipmentName;
        this.renterName = renterName;
        this.renterContact = renterContact;
        this.farmLocation = farmLocation;
        this.startDate = startDate;
        this.durationDays = durationDays;
        this.totalCost = totalCost;
        this.rentalStatus = rentalStatus;
        this.requestTimestamp = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(Long equipmentId) {
        this.equipmentId = equipmentId;
    }

    public String getEquipmentName() {
        return equipmentName;
    }

    public void setEquipmentName(String equipmentName) {
        this.equipmentName = equipmentName;
    }

    public String getRenterName() {
        return renterName;
    }

    public void setRenterName(String renterName) {
        this.renterName = renterName;
    }

    public String getRenterContact() {
        return renterContact;
    }

    public void setRenterContact(String renterContact) {
        this.renterContact = renterContact;
    }

    public String getFarmLocation() {
        return farmLocation;
    }

    public void setFarmLocation(String farmLocation) {
        this.farmLocation = farmLocation;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public Integer getDurationDays() {
        return durationDays;
    }

    public void setDurationDays(Integer durationDays) {
        this.durationDays = durationDays;
    }

    public Double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(Double totalCost) {
        this.totalCost = totalCost;
    }

    public RentalStatus getRentalStatus() {
        return rentalStatus;
    }

    public void setRentalStatus(RentalStatus rentalStatus) {
        this.rentalStatus = rentalStatus;
    }

    public LocalDateTime getRequestTimestamp() {
        return requestTimestamp;
    }

    public void setRequestTimestamp(LocalDateTime requestTimestamp) {
        this.requestTimestamp = requestTimestamp;
    }
}
