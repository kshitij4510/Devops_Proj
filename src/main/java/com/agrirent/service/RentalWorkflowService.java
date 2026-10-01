package com.agrirent.service;

import com.agrirent.model.Equipment;
import com.agrirent.model.EquipmentStatus;
import com.agrirent.model.RentalRequest;
import com.agrirent.model.RentalStatus;
import com.agrirent.repository.EquipmentRepository;
import com.agrirent.repository.RentalRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RentalWorkflowService {

    private final RentalRepository rentalRepository;
    private final EquipmentRepository equipmentRepository;

    public RentalWorkflowService(RentalRepository rentalRepository, EquipmentRepository equipmentRepository) {
        this.rentalRepository = rentalRepository;
        this.equipmentRepository = equipmentRepository;
    }

    public List<RentalRequest> getAllRentals(RentalStatus status) {
        if (status != null) {
            return rentalRepository.findByStatus(status);
        }
        return rentalRepository.findAll();
    }

    public Optional<RentalRequest> getRentalById(Long id) {
        return rentalRepository.findById(id);
    }

    public RentalRequest createRentalRequest(RentalRequest request) {
        Equipment equipment = equipmentRepository.findById(request.getEquipmentId())
                .orElseThrow(() -> new IllegalArgumentException("Equipment not found with ID: " + request.getEquipmentId()));

        if (equipment.getStatus() != EquipmentStatus.AVAILABLE) {
            throw new IllegalStateException("Equipment is not available for rental. Current status: " + equipment.getStatus());
        }

        request.setEquipmentName(equipment.getName());
        double calculatedCost = equipment.getDailyRate() * (request.getDurationDays() != null ? request.getDurationDays() : 1);
        request.setTotalCost(calculatedCost);
        request.setRentalStatus(RentalStatus.PENDING);

        return rentalRepository.save(request);
    }

    public Optional<RentalRequest> approveRental(Long rentalId) {
        return rentalRepository.findById(rentalId).map(rental -> {
            if (rental.getRentalStatus() != RentalStatus.PENDING) {
                throw new IllegalStateException("Only PENDING rentals can be approved");
            }
            rental.setRentalStatus(RentalStatus.APPROVED);
            equipmentRepository.findById(rental.getEquipmentId()).ifPresent(eq -> {
                eq.setStatus(EquipmentStatus.RESERVED);
                equipmentRepository.save(eq);
            });
            return rentalRepository.save(rental);
        });
    }

    public Optional<RentalRequest> rejectRental(Long rentalId) {
        return rentalRepository.findById(rentalId).map(rental -> {
            rental.setRentalStatus(RentalStatus.REJECTED);
            equipmentRepository.findById(rental.getEquipmentId()).ifPresent(eq -> {
                eq.setStatus(EquipmentStatus.AVAILABLE);
                equipmentRepository.save(eq);
            });
            return rentalRepository.save(rental);
        });
    }

    public Optional<RentalRequest> startRentalUse(Long rentalId) {
        return rentalRepository.findById(rentalId).map(rental -> {
            if (rental.getRentalStatus() != RentalStatus.APPROVED && rental.getRentalStatus() != RentalStatus.PENDING) {
                throw new IllegalStateException("Cannot start rental from status: " + rental.getRentalStatus());
            }
            rental.setRentalStatus(RentalStatus.IN_USE);
            equipmentRepository.findById(rental.getEquipmentId()).ifPresent(eq -> {
                eq.setStatus(EquipmentStatus.RENTED);
                equipmentRepository.save(eq);
            });
            return rentalRepository.save(rental);
        });
    }

    public Optional<RentalRequest> completeRentalReturn(Long rentalId) {
        return rentalRepository.findById(rentalId).map(rental -> {
            rental.setRentalStatus(RentalStatus.COMPLETED);
            equipmentRepository.findById(rental.getEquipmentId()).ifPresent(eq -> {
                eq.setStatus(EquipmentStatus.AVAILABLE);
                equipmentRepository.save(eq);
            });
            return rentalRepository.save(rental);
        });
    }

    public Optional<RentalRequest> updateStatus(Long rentalId, RentalStatus targetStatus) {
        return switch (targetStatus) {
            case APPROVED -> approveRental(rentalId);
            case REJECTED -> rejectRental(rentalId);
            case IN_USE -> startRentalUse(rentalId);
            case COMPLETED -> completeRentalReturn(rentalId);
            case CANCELLED -> {
                yield rentalRepository.findById(rentalId).map(r -> {
                    r.setRentalStatus(RentalStatus.CANCELLED);
                    equipmentRepository.findById(r.getEquipmentId()).ifPresent(eq -> {
                        eq.setStatus(EquipmentStatus.AVAILABLE);
                        equipmentRepository.save(eq);
                    });
                    return rentalRepository.save(r);
                });
            }
            default -> rentalRepository.findById(rentalId);
        };
    }
}
