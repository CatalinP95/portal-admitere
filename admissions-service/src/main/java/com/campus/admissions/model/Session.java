package com.campus.admissions.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Entity(name = "session")
@Data
public class Session implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer type;
    private Date endDate;
    private Date startDate;
    private Integer enabled;
    private String name;

    @NotBlank(message = "Anul universitar este obligatoriu")
    private String anUniversitar;

    private String period;

    // locuri disponibile per sesiune
    private Integer spotsBuget;
    private Integer spotsTaxa;

    // zile in care studentul admis trebuie sa confirme
    private Integer confirmationDeadlineDays;

    private Long createdBy;
    private Date createdAt;
    private Long modifiedBy;
    private Date modifiedAt;

    public Session() {}

}
