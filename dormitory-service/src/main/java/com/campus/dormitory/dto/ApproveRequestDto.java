package com.campus.dormitory.dto;

import javax.validation.constraints.NotNull;

public class ApproveRequestDto {
    @NotNull(message = "bedId este obligatoriu")
    private Integer bedId;

    @NotNull(message = "priceId este obligatoriu")
    private Integer priceId;

    @NotNull(message = "adminId este obligatoriu")
    private Long adminId;

    public ApproveRequestDto() {}

    public Integer getBedId() { return bedId; }
    public void setBedId(Integer bedId) { this.bedId = bedId; }
    public Integer getPriceId() { return priceId; }
    public void setPriceId(Integer priceId) { this.priceId = priceId; }
    public Long getAdminId() { return adminId; }
    public void setAdminId(Long adminId) { this.adminId = adminId; }
}
