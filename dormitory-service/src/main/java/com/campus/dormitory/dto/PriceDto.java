package com.campus.dormitory.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class PriceDto {
    private Integer id;

    @NotBlank(message = "Numele tarifului este obligatoriu")
    private String name;

    @NotNull(message = "Pretul este obligatoriu")
    private Float price;

    @NotNull(message = "blockId este obligatoriu")
    private Integer blockId;

    private Integer enabled;

    public PriceDto() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Float getPrice() { return price; }
    public void setPrice(Float price) { this.price = price; }
    public Integer getBlockId() { return blockId; }
    public void setBlockId(Integer blockId) { this.blockId = blockId; }
    public Integer getEnabled() { return enabled; }
    public void setEnabled(Integer enabled) { this.enabled = enabled; }
}
