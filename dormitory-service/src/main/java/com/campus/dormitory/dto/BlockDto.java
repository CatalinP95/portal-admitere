package com.campus.dormitory.dto;

import javax.validation.constraints.NotBlank;

public class BlockDto {
    private Integer id;

    @NotBlank(message = "Numele caminului este obligatoriu")
    private String name;

    private Integer enabled;

    public BlockDto() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getEnabled() { return enabled; }
    public void setEnabled(Integer enabled) { this.enabled = enabled; }
}
