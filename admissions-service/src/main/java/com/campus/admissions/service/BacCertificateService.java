package com.campus.admissions.service;

import com.campus.admissions.model.BacCertificate;
import com.campus.admissions.repository.BacCertificateRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class BacCertificateService {

    private final BacCertificateRepository bacCertificateRepository;

    public BacCertificateService(BacCertificateRepository bacCertificateRepository) {
        this.bacCertificateRepository = bacCertificateRepository;
    }

    public List<BacCertificate> findAll() { return bacCertificateRepository.findAll(); }
    public BacCertificate findOne(Integer id) { return bacCertificateRepository.findById(id).orElse(null); }
    public BacCertificate save(BacCertificate bac) { return bacCertificateRepository.save(bac); }
}
