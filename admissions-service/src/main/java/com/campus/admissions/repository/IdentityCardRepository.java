package com.campus.admissions.repository;

import com.campus.admissions.model.IdentityCard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IdentityCardRepository extends JpaRepository<IdentityCard, Integer> {
    Optional<IdentityCard> findByCreatedBy(Long userId);

}
