package com.campus.admissions.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Entity(name = "profile")
@Data
public class Faculty implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Numele specializarii este obligatoriu")
    private String name;

    @NotNull(message = "Numarul de ani este obligatoriu")
    private Integer noYears;

    private Integer no_students;
    private Integer no_students_buget;
    private Float low_average;

    @ManyToOne(targetEntity = University.class)
    @JoinColumn(name = "university_id")
    private University university;

    private Integer enabled;
    private Long createdBy;
    private Date createdAt;
    private Long modifiedBy;
    private Date modifiedAt;

    public Faculty() {}

}
