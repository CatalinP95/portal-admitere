package com.campus.dormitory.dto;

import java.util.Date;

public class ReceiptDto {
    private Integer id;
    private Date date;
    private Date dataTranzactie;
    private String paymentStatus;
    private Date dueDate;
    private Float amount;
    private Integer month;
    private Integer year;
    private Long userId;
    private Integer rentalAgreementId;

    public ReceiptDto() {}

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
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Integer getRentalAgreementId() { return rentalAgreementId; }
    public void setRentalAgreementId(Integer rentalAgreementId) { this.rentalAgreementId = rentalAgreementId; }
}
