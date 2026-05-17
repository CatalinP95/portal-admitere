package com.campus.dormitory.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
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
}
