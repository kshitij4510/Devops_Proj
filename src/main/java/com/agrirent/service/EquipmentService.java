package com.agrirent.service;

import com.agrirent.model.Equipment;
import com.agrirent.model.EquipmentCategory;
import com.agrirent.model.EquipmentStatus;
import com.agrirent.repository.EquipmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EquipmentService {

    private final EquipmentRepository equipmentRepository;

    public EquipmentService(EquipmentRepository equipmentRepository) {
        this.equipmentRepository = equipmentRepository;
    }

    public List<Equipment> getAllEquipment() {
        return equipmentRepository.findAll();
    }

    public Optional<Equipment> getEquipmentById(Long id) {
        return equipmentRepository.findById(id);
    }

    public Equipment createEquipment(Equipment equipment) {
        if (equipment.getStatus() == null) {
            equipment.setStatus(EquipmentStatus.AVAILABLE);
        }
        return equipmentRepository.save(equipment);
    }

    public Optional<Equipment> updateEquipment(Long id, Equipment updated) {
        return equipmentRepository.findById(id).map(existing -> {
            existing.setName(updated.getName());
            existing.setCategory(updated.getCategory());
            existing.setDailyRate(updated.getDailyRate());
            existing.setHourlyRate(updated.getHourlyRate());
            existing.setLocation(updated.getLocation());
            existing.setOwnerName(updated.getOwnerName());
            existing.setOwnerContact(updated.getOwnerContact());
            existing.setDescription(updated.getDescription());
            if (updated.getStatus() != null) {
                existing.setStatus(updated.getStatus());
            }
            return equipmentRepository.save(existing);
        });
    }

    public Optional<Equipment> updateEquipmentStatus(Long id, EquipmentStatus status) {
        return equipmentRepository.findById(id).map(existing -> {
            existing.setStatus(status);
            return equipmentRepository.save(existing);
        });
    }

    public boolean deleteEquipment(Long id) {
        return equipmentRepository.deleteById(id);
    }

    public List<Equipment> searchEquipment(String query, EquipmentCategory category, EquipmentStatus status) {
        return equipmentRepository.search(query, category, status);
    }
}
