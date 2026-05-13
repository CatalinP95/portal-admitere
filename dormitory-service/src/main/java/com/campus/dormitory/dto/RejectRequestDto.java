package com.campus.dormitory.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class RejectRequestDto {
    @NotBlank(message = "Motivul respingerii este obligatoriu")
    private String reason;

    @NotNull(message = "adminId este obligatoriu")
    private Long adminId;

    public RejectRequestDto() {}

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public Long getAdminId() { return adminId; }
    public void setAdminId(Long adminId) { this.adminId = adminId; }
}
