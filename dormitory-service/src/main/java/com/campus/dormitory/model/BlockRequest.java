package com.campus.dormitory.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity(name = "blockrequest")
public class BlockRequest implements Serializable {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Date date;

    // PENDING | APPROVED | REJECTED
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

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getContractId() { return contractId; }
    public void setContractId(Long contractId) { this.contractId = contractId; }
    public Block getBlock() { return block; }
    public void setBlock(Block block) { this.block = block; }
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
