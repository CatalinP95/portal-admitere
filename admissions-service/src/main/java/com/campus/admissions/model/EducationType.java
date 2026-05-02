package com.campus.admissions.model;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Entity(name = "educationtype")
@Data
public class EducationType implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
    private Integer nrBuget;
    private Integer nrTaxa;
    private Integer nrEtnieroma;
    private Integer nrMoldovaBursa;
    private Integer nrMoldovaTaxa;
    private Integer nrDiasporaTaxa;

    @ManyToOne(targetEntity = Faculty.class)
    @JoinColumn(name = "profile_id")
    private Faculty faculty;

    private Integer enabled;
    private Long createdBy;
    private Date createdAt;
    private Long modifiedBy;
    private Date modifiedAt;

    public EducationType() {}

}
