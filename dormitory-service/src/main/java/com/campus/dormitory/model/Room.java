package com.campus.dormitory.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity(name = "room")
public class Room implements Serializable {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    public Floor getFloor() { return floor; }
    public void setFloor(Floor floor) { this.floor = floor; }
    public Integer getEnabled() { return enabled; }
    public void setEnabled(Integer enabled) { this.enabled = enabled; }
    public Long getCreatedBy() { return createdBy; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    public Long getModifiedBy() { return modifiedBy; }
    public void setModifiedBy(Long modifiedBy) { this.modifiedBy = modifiedBy; }
    public Date getModifiedAt() { return modifiedAt; }
    public void setModifiedAt(Date modifiedAt) { this.modifiedAt = modifiedAt; }
}
