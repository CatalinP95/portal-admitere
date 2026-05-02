package com.campus.admissions.service;

import com.campus.admissions.model.Contract;
import com.campus.admissions.repository.ContractRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Date;
import java.util.List;

@Service
public class ContractService {

    private final ContractRepository contractRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public ContractService(ContractRepository contractRepository) {
        this.contractRepository = contractRepository;
    }

    public List<Contract> findByUserIdAndEnabled(Long userId) {
        return contractRepository.findByUserIdAndEnabled(userId, 1);
    }

    public List<Contract> findTopByUserIdAndEnabledOrderByDateDesc(Long userId) {
        return contractRepository.findTopByUserIdAndEnabledOrderByDateDesc(userId, 1);
    }

    public List<Contract> findByUserId(Long userId) {
        return contractRepository.findByUserId(userId);
    }

    @Transactional
    public Contract save(Contract contract, Long userId) {
        Date now = new Date();
        if (contract.getId() == null) {
            contract.setCreatedBy(userId);
            contract.setCreatedAt(now);
            contract.setEnabled(1);
        }
        contract.setModifiedBy(userId);
        contract.setModifiedAt(now);
        return contractRepository.save(contract);
    }

    // genereaza contractul dupa confirmare cerere
    @Transactional
    public Contract generateAfterConfirmation(Contract contract, Integer applicationId, Long userId) {
        Contract saved = save(contract, userId);
        if (applicationId != null) {
            entityManager.createNativeQuery(
                "update addmissionapplic set status = 'CONFIRMED' where id = " + applicationId
            ).executeUpdate();
        }
        return saved;
    }

    public void delete(Contract contract) {
        contractRepository.delete(contract);
    }
}
