package com.campus.dormitory.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class BedDto {
    private Integer id;
    private String name;

    @NotNull(message = "roomId este obligatoriu")
    private Integer roomId;

    private Integer enabled;
}
