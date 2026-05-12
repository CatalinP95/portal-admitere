package com.campus.admissions.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import java.io.Serializable;
import java.util.Date;

@Entity(name = "averagecompetition")
@Data
@AllArgsConstructor
@Builder
public class AverageCompetition implements Serializable {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idAverage;

    @NotNull(message = "Media BAC este obligatorie")
    @Range(min = 0, max = 10, message = "Media trebuie sa fie intre 0 si 10")
    private Float averageBac;

    @NotNull(message = "Nota diferenta este obligatorie")
    @Range(min = 0, max = 10, message = "Nota trebuie sa fie intre 0 si 10")
    private Float markDif1;

    @NotNull(message = "Nota diferenta este obligatorie")
    @Range(min = 0, max = 10, message = "Nota trebuie sa fie intre 0 si 10")
    private Float markDif2;

    @NotNull(message = "Nota diferenta este obligatorie")
    @Range(min = 0, max = 10, message = "Nota trebuie sa fie intre 0 si 10")
    private Float markDif3;

    private Long userId;
    private Integer enabled;
    private Long createdBy;
    private Date createdAt;
    private Long modifiedBy;
    private Date modifiedAt;

    public AverageCompetition() {}

}
