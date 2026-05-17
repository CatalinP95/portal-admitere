package com.campus.dormitory.repository.jpa;

import com.campus.dormitory.model.RentalAgreement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RentalAgreementRepository extends JpaRepository<RentalAgreement, Integer> {

    List<RentalAgreement> findByUserIdAndEnabled(Long userId, int enabled);
    Optional<RentalAgreement> findFirstByUserIdAndEnabled(Long userId, int enabled);
    List<RentalAgreement> findByEnabled(int enabled);
}
