package com.foodme.model;

import lombok.Data;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

@Data
@MappedSuperclass
public abstract class BaseAuditEntity extends ShortAuditEntity {
    @JoinColumn(name = "created_by")
    @ManyToOne
    private Account createdBy;

    @JoinColumn(name = "updated_by")
    @ManyToOne
    private Account updatedBy;
}
