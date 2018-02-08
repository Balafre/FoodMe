package com.foodme.dto;

import com.foodme.enumeration.OrderStatus;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class GeneralOrderDto {
    private Long id;
    private List<OrderItemDto> orderItems;
    private String pickUpAddress;
    private Date pickUpDate;
    private OrderStatus status;
}
