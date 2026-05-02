package com.campus.admissions.model;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

@Entity(name = "baccertificate")
@Data
public class BacCertificate implements Serializable {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idBac;

    @NotBlank(message = "{baccertificate.validation.series.empty}")
    @Size(min = 2, message = "{baccertificate.validation.series.length}")
    private String series;

    @NotBlank(message = "{baccertificate.validation.no.empty}")
    @Size(min = 4, message = "{baccertificate.validation.no.length}")
    private String no;

    @NotBlank(message = "{baccertificate.validation.siruesCode.empty}")
    @Size(min = 5, max = 9, message = "{baccertificate.validation.siruesCode.length}")
    private String siruesCode;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.TIMESTAMP)
    private Date validityStart;

    private Integer enabled;
    private Long createdBy;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    private Long modifiedBy;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date modifiedAt;

    public BacCertificate() {}

}
