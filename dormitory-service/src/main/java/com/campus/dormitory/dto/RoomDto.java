package com.campus.dormitory.dto;

import javax.validation.constraints.NotNull;

public class RoomDto {
    private Integer id;
    private String name;
    private Integer type;

    @NotNull(message = "numberSeats este obligatoriu")
    private Integer numberSeats;
    private Integer numberSeatsoccupied;

    @NotNull(message = "floorId este obligatoriu")
    private Integer floorId;
    private Integer enabled;

    public RoomDto() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getType() { return type; }
    public void setType(Integer type) { this.type = type; }
    public Integer getNumberSeats() { return numberSeats; }
    public void setNumberSeats(Integer numberSeats) { this.numberSeats = numberSeats; }
    public Integer getNumberSeatsoccupied() { return numberSeatsoccupied; }
    public void setNumberSeatsoccupied(Integer numberSeatsoccupied) { this.numberSeatsoccupied = numberSeatsoccupied; }
    public Integer getFloorId() { return floorId; }
    public void setFloorId(Integer floorId) { this.floorId = floorId; }
    public Integer getEnabled() { return enabled; }
    public void setEnabled(Integer enabled) { this.enabled = enabled; }
}
