package com.campus.admissions.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Entity(name = "formfunding")
@Data
public class FormFunding implements Serializable {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "Anul 1 este obligatoriu")
    private Integer year1;
    private Integer year2;
    private Integer year3;
    private Integer year4;
    private Integer year5;
    private Integer year6;

    private Long userId;
    private Integer enabled;
    private Long createdBy;
    private Date createdAt;
    private Long modifiedBy;
    private Date modifiedAt;

    public FormFunding() {}

}
