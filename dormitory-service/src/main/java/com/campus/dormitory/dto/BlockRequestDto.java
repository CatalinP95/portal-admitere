package com.campus.dormitory.dto;

import lombok.Data;

import java.util.Date;

@Data
public class BlockRequestDto {
    private Integer id;
    private Date date;
    private String status;
    private String rejectionReason;
    private Long userId;
    private Long contractId;
    private Integer blockId;
    private String blockName;
}
