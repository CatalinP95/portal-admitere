package com.campus.admissions.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Entity(name = "usefullinformation")
@Data
public class StudentInfo implements Serializable {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "Statutul civil este obligatoriu")
    private Integer civilStatus;
    private Integer socialStatus;

    @NotBlank(message = "Conditia medicala este obligatorie")
    private String medicalCondition;

    @NotBlank(message = "Numarul fisei medicale este obligatoriu")
    private String noMedicalrecord;

    private Long userId;
    private Integer enabled;
    private Long createdBy;
    private Date createdAt;
    private Long modifiedBy;
    private Date modifiedAt;

    public StudentInfo() {}

}
