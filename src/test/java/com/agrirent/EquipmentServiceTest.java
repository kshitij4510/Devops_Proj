package com.agrirent;

import com.agrirent.model.Equipment;
import com.agrirent.model.EquipmentCategory;
import com.agrirent.model.EquipmentStatus;
import com.agrirent.repository.EquipmentRepository;
import com.agrirent.service.EquipmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("EquipmentService Unit & Functional Tests")
public class EquipmentServiceTest {

    private EquipmentRepository equipmentRepository;
    private EquipmentService equipmentService;

    @BeforeEach
    void setUp() {
        equipmentRepository = new EquipmentRepository();
        equipmentService = new EquipmentService(equipmentRepository);
    }

    @Test
    @DisplayName("Should retrieve all initial seeded equipment")
    void testGetAllEquipment() {
        List<Equipment> all = equipmentService.getAllEquipment();
        assertNotNull(all);
        assertTrue(all.size() >= 6, "Expected at least 6 initial seed equipment items");
    }

    @Test
    @DisplayName("Should successfully create new equipment with default AVAILABLE status")
    void testCreateEquipment() {
        Equipment eq = new Equipment(null, "Sonalika DI 745 III", EquipmentCategory.TRACTOR, 2100.0, 280.0,
                "Satara Hub", "Pravin Patil", "+91 98220 88990", null, "High fuel efficiency tractor");

        Equipment created = equipmentService.createEquipment(eq);

        assertNotNull(created.getId());
        assertEquals("Sonalika DI 745 III", created.getName());
        assertEquals(EquipmentStatus.AVAILABLE, created.getStatus());
        assertEquals(2100.0, created.getDailyRate());
    }

    @Test
    @DisplayName("Should retrieve equipment by valid ID")
    void testGetEquipmentByIdFound() {
        Equipment eq = new Equipment(null, "Test Harvester", EquipmentCategory.COMBINE_HARVESTER, 5000.0, 700.0,
                "Field A", "Tester", "123456", EquipmentStatus.AVAILABLE, "Test machine");
        Equipment saved = equipmentService.createEquipment(eq);

        Optional<Equipment> found = equipmentService.getEquipmentById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("Test Harvester", found.get().getName());
    }

    @Test
    @DisplayName("Should return empty optional for non-existent equipment ID")
    void testGetEquipmentByIdNotFound() {
        Optional<Equipment> notFound = equipmentService.getEquipmentById(999999L);
        assertFalse(notFound.isPresent());
    }

    @Test
    @DisplayName("Should search equipment by keyword and match correctly")
    void testSearchEquipmentByKeyword() {
        List<Equipment> tractors = equipmentService.searchEquipment("John Deere", null, null);
        assertFalse(tractors.isEmpty());
        assertTrue(tractors.get(0).getName().contains("John Deere"));
    }

    @Test
    @DisplayName("Should update equipment status successfully")
    void testUpdateEquipmentStatus() {
        Equipment eq = new Equipment(null, "Escorts Farmtrac 60", EquipmentCategory.TRACTOR, 2400.0, 320.0,
                "Nashik", "Virendra", "9998887776", EquipmentStatus.AVAILABLE, "Rugged design");
        Equipment saved = equipmentService.createEquipment(eq);

        Optional<Equipment> updated = equipmentService.updateEquipmentStatus(saved.getId(), EquipmentStatus.RENTED);
        assertTrue(updated.isPresent());
        assertEquals(EquipmentStatus.RENTED, updated.get().getStatus());
    }
}
