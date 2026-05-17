package com.campus.dormitory.model;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity(name = "allocationsession")
@Data
public class AllocationSession implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
    private Date startDate;
    private Date endDate;

    // OPEN | RUNNING | CLOSED
    private String status;

    private Integer enabled;
    private Long createdBy;
    private Date createdAt;
    private Long modifiedBy;
    private Date modifiedAt;

    public AllocationSession() {}
}
