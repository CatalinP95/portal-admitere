package com.campus.dormitory.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity(name = "receipt")
public class Receipt implements Serializable {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Date date;
    private Date dataTranzactie;

    // UNPAID | PAID | LATE
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

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }
    public Date getDataTranzactie() { return dataTranzactie; }
    public void setDataTranzactie(Date dataTranzactie) { this.dataTranzactie = dataTranzactie; }
    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
    public Date getDueDate() { return dueDate; }
    public void setDueDate(Date dueDate) { this.dueDate = dueDate; }
    public Float getAmount() { return amount; }
    public void setAmount(Float amount) { this.amount = amount; }
    public Integer getMonth() { return month; }
    public void setMonth(Integer month) { this.month = month; }
    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }
    public RentalAgreement getRentalAgreement() { return rentalAgreement; }
    public void setRentalAgreement(RentalAgreement rentalAgreement) { this.rentalAgreement = rentalAgreement; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Integer getEnabled() { return enabled; }
    public void setEnabled(Integer enabled) { this.enabled = enabled; }
    public Long getCreatedBy() { return createdBy; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    public Long getModifiedBy() { return modifiedBy; }
    public void setModifiedBy(Long modifiedBy) { this.modifiedBy = modifiedBy; }
    public Date getModifiedAt() { return modifiedAt; }
    public void setModifiedAt(Date modifiedAt) { this.modifiedAt = modifiedAt; }
}
