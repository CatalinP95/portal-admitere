package com.campus.dormitory.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity(name = "rentalagreement")
public class RentalAgreement implements Serializable {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Date date;
    private String noReceipt;

    @OneToOne(targetEntity = BlockRequest.class)
    @JoinColumn(name = "blockrequest_id")
    private BlockRequest blockRequest;

    @OneToOne(targetEntity = Bed.class)
    @JoinColumn(name = "bed_id")
    private Bed bed;

    @OneToOne(targetEntity = Price.class)
    @JoinColumn(name = "price_id")
    private Price price;

    private Long userId;
    private Integer enabled;
    private Long createdBy;
    private Date createdAt;
    private Long modifiedBy;
    private Date modifiedAt;

    public RentalAgreement() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }
    public String getNoReceipt() { return noReceipt; }
    public void setNoReceipt(String noReceipt) { this.noReceipt = noReceipt; }
    public BlockRequest getBlockRequest() { return blockRequest; }
    public void setBlockRequest(BlockRequest blockRequest) { this.blockRequest = blockRequest; }
    public Bed getBed() { return bed; }
    public void setBed(Bed bed) { this.bed = bed; }
    public Price getPrice() { return price; }
    public void setPrice(Price price) { this.price = price; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
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
