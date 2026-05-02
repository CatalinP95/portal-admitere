package com.campus.admissions.model;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Entity(name = "addmissioncontract")
@Data
public class Contract implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Date date;
    private Integer studyYear;
    private Integer formfunding;
    private Integer approved;
    private Integer copyCi;
    private Integer copyBirthcertificate;
    private Integer bacCertificate;
    private Integer medicalCertificate;

    @ManyToOne(targetEntity = Application.class)
    @JoinColumn(name = "admissionapplic_id")
    private Application application;

    @ManyToOne(targetEntity = Session.class)
    @JoinColumn(name = "session_id")
    private Session session;

    private Long userId;
    private Integer enabled;
    private Long createdBy;
    private Date createdAt;
    private Long modifiedBy;
    private Date modifiedAt;

    public Contract() {}

}
