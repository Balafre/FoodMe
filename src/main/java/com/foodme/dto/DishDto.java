package com.foodme.dto;

import com.foodme.model.Option;
import com.foodme.enumeration.OptionType;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
public class DishDto {
    private Long id;
    private String name;
    private String description;
    private String dishPictureUrl;
    private BigDecimal price;
    private Map<OptionType, Option> options;
}
