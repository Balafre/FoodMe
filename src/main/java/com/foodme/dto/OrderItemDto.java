package com.foodme.dto;

import com.foodme.model.Dish;
import lombok.Data;

import java.util.Set;

@Data
public class OrderItemDto {
    private Long id;
    private Set<DishDto> orderedDishes;
}
