package com.agrirent;

import com.agrirent.model.*;
import com.agrirent.repository.EquipmentRepository;
import com.agrirent.repository.RentalRepository;
import com.agrirent.service.RentalWorkflowService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("RentalWorkflowService Role-Based Workflow Tests")
public class RentalWorkflowServiceTest {

    private EquipmentRepository equipmentRepository;
    private RentalRepository rentalRepository;
    private RentalWorkflowService rentalWorkflowService;

    @BeforeEach
    void setUp() {
        equipmentRepository = new EquipmentRepository();
        rentalRepository = new RentalRepository();
        rentalWorkflowService = new RentalWorkflowService(rentalRepository, equipmentRepository);
    }

    @Test
    @DisplayName("Should successfully create a rental request in PENDING status with calculated cost")
    void testCreateRentalRequest() {
        List<Equipment> all = equipmentRepository.findAll();
        Equipment eq = all.get(0);
        eq.setStatus(EquipmentStatus.AVAILABLE);
        equipmentRepository.save(eq);

        RentalRequest req = new RentalRequest(null, eq.getId(), null, "Farmer Mohan",
                "+91 98888 77777", "Baramati Farm Sector 2", LocalDate.now(), 4, null, null);

        RentalRequest created = rentalWorkflowService.createRentalRequest(req);

        assertNotNull(created.getId());
        assertEquals(RentalStatus.PENDING, created.getRentalStatus());
        assertEquals(eq.getName(), created.getEquipmentName());
        assertEquals(eq.getDailyRate() * 4, created.getTotalCost());
    }

    @Test
    @DisplayName("Should throw exception when renting unavailable equipment")
    void testCreateRentalUnavailableEquipment() {
        List<Equipment> all = equipmentRepository.findAll();
        Equipment eq = all.get(0);
        eq.setStatus(EquipmentStatus.RENTED);
        equipmentRepository.save(eq);

        RentalRequest req = new RentalRequest(null, eq.getId(), null, "Farmer Suresh",
                "+91 98888 66666", "Pune Farm", LocalDate.now(), 2, null, null);

        assertThrows(IllegalStateException.class, () -> rentalWorkflowService.createRentalRequest(req));
    }

    @Test
    @DisplayName("Owner should approve rental request and change equipment status to RESERVED")
    void testApproveRental() {
        List<Equipment> all = equipmentRepository.findAll();
        Equipment eq = all.get(1);
        eq.setStatus(EquipmentStatus.AVAILABLE);
        equipmentRepository.save(eq);

        RentalRequest req = new RentalRequest(null, eq.getId(), null, "Farmer Ganesh",
                "+91 97777 55555", "Indapur Farm 1", LocalDate.now(), 3, null, null);
        RentalRequest saved = rentalWorkflowService.createRentalRequest(req);

        Optional<RentalRequest> approved = rentalWorkflowService.approveRental(saved.getId());
        assertTrue(approved.isPresent());
        assertEquals(RentalStatus.APPROVED, approved.get().getRentalStatus());

        Equipment updatedEq = equipmentRepository.findById(eq.getId()).orElseThrow();
        assertEquals(EquipmentStatus.RESERVED, updatedEq.getStatus());
    }

    @Test
    @DisplayName("Starting rental usage should update status to IN_USE and equipment to RENTED")
    void testStartRentalUse() {
        List<Equipment> all = equipmentRepository.findAll();
        Equipment eq = all.get(2);
        eq.setStatus(EquipmentStatus.AVAILABLE);
        equipmentRepository.save(eq);

        RentalRequest req = new RentalRequest(null, eq.getId(), null, "Farmer Anita",
                "+91 96666 44444", "Shirur Farm 5", LocalDate.now(), 5, null, null);
        RentalRequest saved = rentalWorkflowService.createRentalRequest(req);
        rentalWorkflowService.approveRental(saved.getId());

        Optional<RentalRequest> inUse = rentalWorkflowService.startRentalUse(saved.getId());
        assertTrue(inUse.isPresent());
        assertEquals(RentalStatus.IN_USE, inUse.get().getRentalStatus());

        Equipment updatedEq = equipmentRepository.findById(eq.getId()).orElseThrow();
        assertEquals(EquipmentStatus.RENTED, updatedEq.getStatus());
    }

    @Test
    @DisplayName("Completing rental return should set status COMPLETED and restore equipment to AVAILABLE")
    void testCompleteRentalReturn() {
        List<Equipment> all = equipmentRepository.findAll();
        Equipment eq = all.get(3);
        eq.setStatus(EquipmentStatus.AVAILABLE);
        equipmentRepository.save(eq);

        RentalRequest req = new RentalRequest(null, eq.getId(), null, "Farmer Vikas",
                "+91 95555 33333", "Junnar Farm 8", LocalDate.now(), 1, null, null);
        RentalRequest saved = rentalWorkflowService.createRentalRequest(req);
        rentalWorkflowService.approveRental(saved.getId());
        rentalWorkflowService.startRentalUse(saved.getId());

        Optional<RentalRequest> completed = rentalWorkflowService.completeRentalReturn(saved.getId());
        assertTrue(completed.isPresent());
        assertEquals(RentalStatus.COMPLETED, completed.get().getRentalStatus());

        Equipment updatedEq = equipmentRepository.findById(eq.getId()).orElseThrow();
        assertEquals(EquipmentStatus.AVAILABLE, updatedEq.getStatus());
    }
}
