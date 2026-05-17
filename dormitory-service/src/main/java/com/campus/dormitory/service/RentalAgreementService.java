package com.campus.dormitory.service;

import com.campus.dormitory.exception.ResourceNotFoundException;
import com.campus.dormitory.model.RentalAgreement;
import com.campus.dormitory.repository.jpa.RentalAgreementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class RentalAgreementService {

    private final RentalAgreementRepository repository;

    public RentalAgreementService(RentalAgreementRepository repository) {
        this.repository = repository;
    }

    public List<RentalAgreement> findAll() {
        return repository.findAll();
    }

    public RentalAgreement findById(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RentalAgreement", id));
    }

    public List<RentalAgreement> findByUserId(Long userId) {
        return repository.findByUserIdAndEnabled(userId, 1);
    }

    public Optional<RentalAgreement> findActiveByUserId(Long userId) {
        return repository.findFirstByUserIdAndEnabled(userId, 1);
    }

    @Transactional
    public RentalAgreement save(RentalAgreement ra) {
        return repository.save(ra);
    }

    @Transactional
    public void terminate(Integer id) {
        RentalAgreement existing = findById(id);
        existing.setEnabled(0);
        repository.save(existing);
    }
}
