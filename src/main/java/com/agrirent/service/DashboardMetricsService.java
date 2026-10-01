package com.agrirent.service;

import com.agrirent.model.Equipment;
import com.agrirent.model.EquipmentStatus;
import com.agrirent.model.RentalRequest;
import com.agrirent.model.RentalStatus;
import com.agrirent.repository.EquipmentRepository;
import com.agrirent.repository.RentalRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DashboardMetricsService {

    private final EquipmentRepository equipmentRepository;
    private final RentalRepository rentalRepository;

    public DashboardMetricsService(EquipmentRepository equipmentRepository, RentalRepository rentalRepository) {
        this.equipmentRepository = equipmentRepository;
        this.rentalRepository = rentalRepository;
    }

    public Map<String, Object> getDashboardMetrics() {
        List<Equipment> allEquipment = equipmentRepository.findAll();
        List<RentalRequest> allRentals = rentalRepository.findAll();

        int totalEquipment = allEquipment.size();
        long availableCount = allEquipment.stream().filter(e -> e.getStatus() == EquipmentStatus.AVAILABLE).count();
        long rentedCount = allEquipment.stream().filter(e -> e.getStatus() == EquipmentStatus.RENTED).count();
        long maintenanceCount = allEquipment.stream().filter(e -> e.getStatus() == EquipmentStatus.MAINTENANCE).count();

        long activeRentals = allRentals.stream().filter(r -> r.getRentalStatus() == RentalStatus.IN_USE).count();
        long pendingRentals = allRentals.stream().filter(r -> r.getRentalStatus() == RentalStatus.PENDING).count();
        long completedRentals = allRentals.stream().filter(r -> r.getRentalStatus() == RentalStatus.COMPLETED).count();

        double totalRevenue = allRentals.stream()
                .filter(r -> r.getRentalStatus() == RentalStatus.COMPLETED || r.getRentalStatus() == RentalStatus.IN_USE)
                .mapToDouble(r -> r.getTotalCost() != null ? r.getTotalCost() : 0.0)
                .sum();

        double utilizationRate = totalEquipment > 0
                ? Math.round(((double) (rentedCount + (totalEquipment - availableCount)) / totalEquipment) * 100.0)
                : 0.0;

        Map<String, Long> categoryCount = allEquipment.stream()
                .collect(Collectors.groupingBy(e -> e.getCategory().name(), Collectors.counting()));

        Map<String, Object> metrics = new HashMap<>();
        metrics.put("totalEquipment", totalEquipment);
        metrics.put("availableEquipment", availableCount);
        metrics.put("rentedEquipment", rentedCount);
        metrics.put("maintenanceEquipment", maintenanceCount);
        metrics.put("activeRentals", activeRentals);
        metrics.put("pendingRentals", pendingRentals);
        metrics.put("completedRentals", completedRentals);
        metrics.put("totalRevenue", totalRevenue);
        metrics.put("utilizationRate", utilizationRate);
        metrics.put("categoryBreakdown", categoryCount);

        return metrics;
    }
}
