package com.campus.dormitory.api;

import com.campus.dormitory.model.RentalAgreement;
import com.campus.dormitory.service.RentalAgreementService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dormitory/agreements")
@Tag(name = "RentalAgreements", description = "Contracte cazare")
public class RentalAgreementController {

    private final RentalAgreementService service;

    public RentalAgreementController(RentalAgreementService service) {
        this.service = service;
    }

    @GetMapping
    public List<RentalAgreement> getAll() { return service.findAll(); }

    @GetMapping("/{id}")
    public RentalAgreement getOne(@PathVariable Integer id) { return service.findById(id); }

    @GetMapping("/by-user/{userId}")
    public List<RentalAgreement> getByUser(@PathVariable Long userId) {
        return service.findByUserId(userId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> terminate(@PathVariable Integer id) {
        service.terminate(id);
        return ResponseEntity.noContent().build();
    }
}
