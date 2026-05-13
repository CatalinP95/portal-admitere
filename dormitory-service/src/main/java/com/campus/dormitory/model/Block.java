package com.campus.dormitory.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;

@Entity(name = "block")
public class Block implements Serializable {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Numele caminului este obligatoriu")
    private String name;

    private Integer enabled;
    private Long createdBy;
    private Date createdAt;
    private Long modifiedBy;
    private Date modifiedAt;

    public Block() {}

    public Integer getId() { return id; }

    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

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
