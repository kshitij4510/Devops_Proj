package com.agrirent.repository;

import com.agrirent.model.RentalRequest;
import com.agrirent.model.RentalStatus;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class RentalRepository {

    private final Map<Long, RentalRequest> store = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(500);

    public RentalRepository() {
        seedInitialRentals();
    }

    private void seedInitialRentals() {
        RentalRequest r1 = new RentalRequest(null, 101L, "John Deere 5050D Tractor",
                "Rajesh Shinde", "+91 97654 11223", "Dindori Farm 3",
                LocalDate.now().minusDays(5), 3, 7500.0, RentalStatus.COMPLETED);
        save(r1);

        RentalRequest r2 = new RentalRequest(null, 103L, "Kubota Harvester DC-68G",
                "Sunil Patil", "+91 97654 33445", "Bavda Block 12",
                LocalDate.now().minusDays(1), 2, 17000.0, RentalStatus.IN_USE);
        save(r2);
    }

    public List<RentalRequest> findAll() {
        return new ArrayList<>(store.values());
    }

    public Optional<RentalRequest> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    public RentalRequest save(RentalRequest rental) {
        if (rental.getId() == null) {
            rental.setId(idGenerator.incrementAndGet());
        }
        store.put(rental.getId(), rental);
        return rental;
    }

    public List<RentalRequest> findByStatus(RentalStatus status) {
        return store.values().stream()
                .filter(r -> r.getRentalStatus() == status)
                .sorted(Comparator.comparing(RentalRequest::getId).reversed())
                .collect(Collectors.toList());
    }

    public List<RentalRequest> findByEquipmentId(Long equipmentId) {
        return store.values().stream()
                .filter(r -> Objects.equals(r.getEquipmentId(), equipmentId))
                .sorted(Comparator.comparing(RentalRequest::getId).reversed())
                .collect(Collectors.toList());
    }
}
