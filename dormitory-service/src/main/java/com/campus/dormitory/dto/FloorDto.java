package com.campus.dormitory.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class FloorDto {
    private Integer id;

    @NotBlank(message = "Numele etajului este obligatoriu")
    private String name;

    @NotNull(message = "blockId este obligatoriu")
    private Integer blockId;

    private Integer enabled;

    public FloorDto() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getBlockId() { return blockId; }
    public void setBlockId(Integer blockId) { this.blockId = blockId; }
    public Integer getEnabled() { return enabled; }
    public void setEnabled(Integer enabled) { this.enabled = enabled; }
}
