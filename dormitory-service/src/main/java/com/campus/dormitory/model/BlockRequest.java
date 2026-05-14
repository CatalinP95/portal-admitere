package com.campus.dormitory.model;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity(name = "blockrequest")
@Data
public class BlockRequest implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Date date;

    private String status;

    private String rejectionReason;

    private Long userId;
    private Long contractId;

    @ManyToOne(targetEntity = Block.class)
    @JoinColumn(name = "block_id")
    private Block block;

    private Integer enabled;
    private Long createdBy;
    private Date createdAt;
    private Long modifiedBy;
    private Date modifiedAt;

    public BlockRequest() {}
}
