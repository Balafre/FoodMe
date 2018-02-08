package com.foodme.dto;

import com.foodme.model.Menu;
import lombok.Data;

import java.util.List;

@Data
public class MenuSectionDto {
    private Long id;
    private String name;
    private String description;
    private String menuSectionPictureUrl;
    private List<DishDto> dishes;
}
