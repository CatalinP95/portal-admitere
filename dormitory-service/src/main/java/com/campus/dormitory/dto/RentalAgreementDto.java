package com.campus.dormitory.dto;

import java.util.Date;

public class RentalAgreementDto {
    private Integer id;
    private Date date;
    private String noReceipt;
    private Integer blockRequestId;
    private Integer bedId;
    private Integer priceId;
    private Long userId;

    public RentalAgreementDto() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }
    public String getNoReceipt() { return noReceipt; }
    public void setNoReceipt(String noReceipt) { this.noReceipt = noReceipt; }
    public Integer getBlockRequestId() { return blockRequestId; }
    public void setBlockRequestId(Integer blockRequestId) { this.blockRequestId = blockRequestId; }
    public Integer getBedId() { return bedId; }
    public void setBedId(Integer bedId) { this.bedId = bedId; }
    public Integer getPriceId() { return priceId; }
    public void setPriceId(Integer priceId) { this.priceId = priceId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
}
