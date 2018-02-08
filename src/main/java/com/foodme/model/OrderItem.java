package com.foodme.model;

import com.foodme.enumeration.OrderStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "order_item")
@Setter
@Getter
@NoArgsConstructor
public class OrderItem extends BaseAuditEntity {
    @ManyToOne
    @JoinColumn(name = "restaurant_operator_id")
    private Account restaurantOperator;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    @ManyToOne(cascade = CascadeType.PERSIST)
    private GeneralOrder order;

    @ManyToMany(
            fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "order_item_dishes",
            joinColumns = @JoinColumn(name = "order_item_id", nullable = false, updatable = false),
            inverseJoinColumns = { @JoinColumn(name = "dish_id", nullable = false, updatable = false) })
    private Set<Dish> orderedDishes = new HashSet<>();

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Builder
    public OrderItem(Restaurant restaurant, GeneralOrder order, Set<Dish> dishes) {
        this.restaurant = restaurant;
        this.order = order;
        orderedDishes = dishes;
    }
}
