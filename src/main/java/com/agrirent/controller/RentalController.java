package com.agrirent.controller;

import com.agrirent.model.RentalRequest;
import com.agrirent.model.RentalStatus;
import com.agrirent.service.RentalWorkflowService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rentals")
@CrossOrigin(origins = "*")
public class RentalController {

    private final RentalWorkflowService rentalWorkflowService;

    public RentalController(RentalWorkflowService rentalWorkflowService) {
        this.rentalWorkflowService = rentalWorkflowService;
    }

    @GetMapping
    public ResponseEntity<List<RentalRequest>> getAllRentals(@RequestParam(required = false) RentalStatus status) {
        return ResponseEntity.ok(rentalWorkflowService.getAllRentals(status));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RentalRequest> getRentalById(@PathVariable Long id) {
        return rentalWorkflowService.getRentalById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createRental(@Valid @RequestBody RentalRequest rentalRequest) {
        try {
            RentalRequest created = rentalWorkflowService.createRentalRequest(rentalRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<?> approveRental(@PathVariable Long id) {
        try {
            return rentalWorkflowService.approveRental(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<?> rejectRental(@PathVariable Long id) {
        return rentalWorkflowService.rejectRental(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/dispatch")
    public ResponseEntity<?> dispatchRental(@PathVariable Long id) {
        try {
            return rentalWorkflowService.startRentalUse(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}/return")
    public ResponseEntity<?> returnRental(@PathVariable Long id) {
        return rentalWorkflowService.completeRentalReturn(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String statusStr = body.get("status");
        if (statusStr == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Status field is required"));
        }
        try {
            RentalStatus targetStatus = RentalStatus.valueOf(statusStr.toUpperCase());
            return rentalWorkflowService.updateStatus(id, targetStatus)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
