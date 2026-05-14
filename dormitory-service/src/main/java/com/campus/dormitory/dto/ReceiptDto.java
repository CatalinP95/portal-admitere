package com.campus.dormitory.dto;

import lombok.Data;

import java.util.Date;

@Data
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
}
