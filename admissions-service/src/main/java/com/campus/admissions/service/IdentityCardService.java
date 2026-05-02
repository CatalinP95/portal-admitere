package com.campus.admissions.service;

import com.campus.admissions.model.IdentityCard;
import com.campus.admissions.repository.IdentityCardRepository;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List;

@Service
public class IdentityCardService {

    private final IdentityCardRepository identityCardRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public IdentityCardService(IdentityCardRepository identityCardRepository) {
        this.identityCardRepository = identityCardRepository;
    }

    public List<IdentityCard> findAll() { return identityCardRepository.findAll(); }
    public IdentityCard findOne(Integer id) { return identityCardRepository.findById(id).orElse(null); }
    public IdentityCard save(IdentityCard identityCard) { return identityCardRepository.save(identityCard); }

    @Transactional
    public int updateDistance(Integer id, double distance) {
        return entityManager.createNativeQuery(
            "update identitycard set distance=" + distance + " where id=" + id).executeUpdate();
    }
}
