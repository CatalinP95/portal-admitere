package com.campus.admissions.model;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

@Entity(name = "highschool")
@Data
public class Highschool implements Serializable {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "{highschool.validation.name.empty}")
    @Size(min = 5, message = "{highschool.validation.name.length}")
    private String name;

    @NotBlank(message = "{highschool.validation.address.empty}")
    @Size(min = 15, message = "{highschool.validation.address.length}")
    private String address;

    @NotBlank(message = "{highschool.validation.profile.empty}")
    private String profile;

    @NotNull(message = "{highschool.validation.duration.empty}")
    private Integer duration;

    private Integer enabled;
    private Long createdBy;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    private Long modifiedBy;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date modifiedAt;

    public Highschool() {}

}
