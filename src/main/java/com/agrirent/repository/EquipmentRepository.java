package com.agrirent.repository;

import com.agrirent.model.Equipment;
import com.agrirent.model.EquipmentCategory;
import com.agrirent.model.EquipmentStatus;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class EquipmentRepository {

    private final Map<Long, Equipment> store = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(100);

    public EquipmentRepository() {
        seedInitialData();
    }

    private void seedInitialData() {
        save(new Equipment(null, "John Deere 5050D Tractor", EquipmentCategory.TRACTOR, 2500.0, 350.0,
                "Nashik Central Hub", "Gurpreet Singh", "+91 98220 11223", EquipmentStatus.AVAILABLE,
                "50 HP heavy-duty tractor, ideal for plowing, haulage, and sowing operations."));

        save(new Equipment(null, "Mahindra 575 DI Sarpanch", EquipmentCategory.TRACTOR, 2200.0, 300.0,
                "Pune Rural Depot", "Rameshwar Rao", "+91 98220 44556", EquipmentStatus.AVAILABLE,
                "45 HP fuel-efficient workhorse with high back-up torque for black cotton soils."));

        save(new Equipment(null, "Kubota Harvester DC-68G", EquipmentCategory.COMBINE_HARVESTER, 8500.0, 1100.0,
                "Kolhapur Agri Zone", "Balwant Deshmukh", "+91 98220 77889", EquipmentStatus.AVAILABLE,
                "High capacity paddy and grain combine harvester with minimal grain loss."));

        save(new Equipment(null, "Shaktiman Rotary Tiller (Rotavator)", EquipmentCategory.ROTAVATOR, 1400.0, 200.0,
                "Aurangabad Yard", "Kishore Jadhav", "+91 98220 99001", EquipmentStatus.AVAILABLE,
                "7-feet wide rotary tiller for fine seedbed preparation and weed eradication."));

        save(new Equipment(null, "Fieldking Multi-Crop Seed Drill", EquipmentCategory.SEED_DRILL, 1200.0, 180.0,
                "Solapur Farm Center", "Anand Kulkarni", "+91 98220 33445", EquipmentStatus.AVAILABLE,
                "9-tyne precision seed and fertilizer drill ensuring uniform seed spacing."));

        save(new Equipment(null, "Aspee Tractor-Mounted Boom Sprayer", EquipmentCategory.BOOM_SPRAYER, 1800.0, 250.0,
                "Nagpur Citrus Cluster", "Vikas Thakre", "+91 98220 55667", EquipmentStatus.AVAILABLE,
                "400-liter chemical tank with 12m spray boom for pesticide/fertilizer application."));
    }

    public List<Equipment> findAll() {
        return new ArrayList<>(store.values());
    }

    public Optional<Equipment> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    public Equipment save(Equipment equipment) {
        if (equipment.getId() == null) {
            equipment.setId(idGenerator.incrementAndGet());
        }
        store.put(equipment.getId(), equipment);
        return equipment;
    }

    public boolean deleteById(Long id) {
        return store.remove(id) != null;
    }

    public List<Equipment> search(String query, EquipmentCategory category, EquipmentStatus status) {
        return store.values().stream()
                .filter(eq -> {
                    if (query != null && !query.trim().isEmpty()) {
                        String q = query.trim().toLowerCase();
                        boolean matchesName = eq.getName() != null && eq.getName().toLowerCase().contains(q);
                        boolean matchesLoc = eq.getLocation() != null && eq.getLocation().toLowerCase().contains(q);
                        boolean matchesDesc = eq.getDescription() != null && eq.getDescription().toLowerCase().contains(q);
                        if (!matchesName && !matchesLoc && !matchesDesc) {
                            return false;
                        }
                    }
                    if (category != null && eq.getCategory() != category) {
                        return false;
                    }
                    if (status != null && eq.getStatus() != status) {
                        return false;
                    }
                    return true;
                })
                .sorted(Comparator.comparing(Equipment::getId))
                .collect(Collectors.toList());
    }
}
