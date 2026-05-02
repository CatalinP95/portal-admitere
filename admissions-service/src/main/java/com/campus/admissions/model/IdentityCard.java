package com.campus.admissions.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.util.Date;

@Entity(name = "identitycard")
@Data
public class IdentityCard implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "{identitycard.validation.firstName.empty}")
    @Size(min = 5, message = "{identitycard.validation.firstName.length}")
    private String firstName;

    @NotBlank(message = "{identitycard.validation.lastName.empty}")
    @Size(min = 5, message = "{identitycard.validation.lastName.length}")
    private String lastName;

    @NotBlank(message = "{identitycard.validation.cnp.empty}")
    @Size(min = 13, max = 13, message = "{identitycard.validation.cnp.length}")
    private String cnp;

    @NotBlank(message = "{identitycard.validation.series.empty}")
    @Size(min = 2, max = 2, message = "{identitycard.validation.series.length}")
    private String series;

    @NotBlank(message = "{identitycard.validation.no.empty}")
    @Size(min = 6, max = 6, message = "{identitycard.validation.no.length}")
    private String no;

    @NotBlank(message = "{identitycard.validation.nationality.empty}")
    @Size(min = 3, message = "{identitycard.validation.nationality.length}")
    private String nationality;

    @NotBlank(message = "{identitycard.validation.issuedBy.empty}")
    @Size(min = 5, message = "{identitycard.validation.issuedBy.length}")
    private String issuedBy;

    @NotBlank(message = "{identitycard.validation.sex.empty}")
    private String sex;

    @NotBlank(message = "{identitycard.validation.address.empty}")
    @Size(min = 15, message = "{identitycard.validation.address.length}")
    private String address;

    @NotBlank(message = "{identitycard.validation.placeBirth.empty}")
    @Size(min = 5, message = "{identitycard.validation.placeBirth.length}")
    private String placeBirth;

    @NotBlank(message = "{identitycard.validation.ethnicity.empty}")
    @Size(min = 3, message = "{identitycard.validation.ethnicity.length}")
    private String ethnicity;

    private Double distance;

    @NotNull(message = "{identitycard.validation.validityStart.empty}")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.TIMESTAMP)
    private Date validityStart;

    @NotNull(message = "{identitycard.validation.validityEnd.empty}")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.TIMESTAMP)
    private Date validityEnd;

    @Lob
    @JsonIgnore
    private byte[] image;

    private Integer enabled;
    private Long createdBy;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    private Long modifiedBy;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date modifiedAt;

    public IdentityCard() {
    }

}
