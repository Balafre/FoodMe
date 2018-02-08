package com.foodme.model;

import com.foodme.enumeration.OrderStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "general_order")
@Data
@EqualsAndHashCode(callSuper = true)
public class GeneralOrder extends BaseAuditEntity {
    @OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    @JoinColumn(name = "general_order_id")
    private Set<OrderItem> orderItems = new HashSet<>();

    @Column(name = "pick_up_address")
    private String pickUpAddress;

    @Column(name = "pick_up_at", nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    // null means ASAP
    private Date pickUpDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;
}
