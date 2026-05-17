package com.campus.dormitory.model;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity(name = "room")
@Data
public class Room implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
    private Integer type;
    private Integer numberSeats;
    private Integer numberSeatsoccupied;

    @ManyToOne(targetEntity = Floor.class)
    @JoinColumn(name = "floor_id")
    private Floor floor;

    private Integer enabled;
    private Long createdBy;
    private Date createdAt;
    private Long modifiedBy;
    private Date modifiedAt;

    public Room() {}
}
