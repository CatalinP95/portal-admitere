package com.campus.admissions.service;

import com.campus.admissions.model.FormFunding;
import com.campus.admissions.repository.FormFundingRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class FormFundingService {

    private final FormFundingRepository formFundingRepository;

    public FormFundingService(FormFundingRepository formFundingRepository) {
        this.formFundingRepository = formFundingRepository;
    }

    public List<FormFunding> findAll() { return formFundingRepository.findAll(); }
    public FormFunding findOne(Integer id) { return formFundingRepository.findById(id).orElse(null); }
    public FormFunding save(FormFunding ff) { return formFundingRepository.save(ff); }
}
