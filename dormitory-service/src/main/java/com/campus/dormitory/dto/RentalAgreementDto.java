package com.campus.dormitory.dto;

import lombok.Data;

import java.util.Date;

@Data
public class RentalAgreementDto {
    private Integer id;
    private Date date;
    private String noReceipt;
    private Integer blockRequestId;
    private Integer bedId;
    private Integer priceId;
    private Long userId;
}
