package com.foodme.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@MappedSuperclass
public abstract class ShortAuditEntity extends BaseEntity {

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedDate;

    /**
     * Sets createdDate before insert
     */
    @PrePersist
    public void setCreationDate() {
        this.createdDate = new Date();
    }

    /**
     * Sets updatedDate before update
     */
    @PreUpdate
    public void setChangeDate() {
        this.updatedDate = new Date();
    }

}
