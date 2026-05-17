package com.campus.dormitory.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class PriceDto {
    private Integer id;

    @NotBlank(message = "Numele tarifului este obligatoriu")
    private String name;

    @NotNull(message = "Pretul este obligatoriu")
    private Float price;

    @NotNull(message = "blockId este obligatoriu")
    private Integer blockId;

    private Integer enabled;
}
