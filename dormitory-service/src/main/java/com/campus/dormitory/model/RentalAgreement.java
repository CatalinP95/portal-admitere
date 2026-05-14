package com.campus.dormitory.model;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity(name = "rentalagreement")
@Data
public class RentalAgreement implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Date date;
    private String noReceipt;

    @OneToOne(targetEntity = BlockRequest.class)
    @JoinColumn(name = "blockrequest_id")
    private BlockRequest blockRequest;

    @OneToOne(targetEntity = Bed.class)
    @JoinColumn(name = "bed_id")
    private Bed bed;

    @OneToOne(targetEntity = Price.class)
    @JoinColumn(name = "price_id")
    private Price price;

    private Long userId;
    private Integer enabled;
    private Long createdBy;
    private Date createdAt;
    private Long modifiedBy;
    private Date modifiedAt;

    public RentalAgreement() {}
}
