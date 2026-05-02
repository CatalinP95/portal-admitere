package com.campus.admissions.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Entity(name = "anotheruniversity")
@Data
public class AnotherUniversity implements Serializable {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Numele universitatii este obligatoriu")
    private String universityName;

    @NotBlank(message = "Numele colegiului este obligatoriu")
    private String collegeName;

    @NotBlank(message = "Profilul este obligatoriu")
    private String profileName;

    @NotBlank(message = "Adresa este obligatorie")
    private String address;

    @NotNull(message = "Tipul de educatie este obligatoriu")
    private Integer educationType;

    private Date graduationYear;

    @OneToOne(targetEntity = FormFunding.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "formfunding_id")
    private FormFunding formFunding;

    private Long userId;
    private Integer enabled;
    private Long createdBy;
    private Date createdAt;
    private Long modifiedBy;
    private Date modifiedAt;

    public AnotherUniversity() {}

}
