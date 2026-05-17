package com.campus.dormitory.model;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity(name = "receipt")
@Data
public class Receipt implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Date date;
    private Date dataTranzactie;

    private String paymentStatus;

    private Date dueDate;
    private Float amount;
    private Integer month;
    private Integer year;

    @ManyToOne(targetEntity = RentalAgreement.class)
    @JoinColumn(name = "rentalagreement_id")
    private RentalAgreement rentalAgreement;

    private Long userId;
    private Integer enabled;
    private Long createdBy;
    private Date createdAt;
    private Long modifiedBy;
    private Date modifiedAt;

    public Receipt() {}
}
