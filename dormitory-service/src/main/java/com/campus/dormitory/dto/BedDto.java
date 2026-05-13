package com.campus.dormitory.dto;

import javax.validation.constraints.NotNull;

public class BedDto {
    private Integer id;
    private String name;

    @NotNull(message = "roomId este obligatoriu")
    private Integer roomId;
    private Integer enabled;

    public BedDto() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getRoomId() { return roomId; }
    public void setRoomId(Integer roomId) { this.roomId = roomId; }
    public Integer getEnabled() { return enabled; }
    public void setEnabled(Integer enabled) { this.enabled = enabled; }
}
