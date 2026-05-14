package com.campus.dormitory.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;

@Entity(name = "floor")
@Data
public class Floor implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Numele etajului este obligatoriu")
    private String name;

    @ManyToOne(targetEntity = Block.class)
    @JoinColumn(name = "block_id")
    private Block block;

    private Integer enabled;
    private Long createdBy;
    private Date createdAt;
    private Long modifiedBy;
    private Date modifiedAt;

    public Floor() {}
}
