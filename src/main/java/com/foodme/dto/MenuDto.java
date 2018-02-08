package com.foodme.dto;

import lombok.Data;

import java.util.List;

@Data
public class MenuDto {
    private Long id;
    private String name;
    private String description;
    private List<MenuSectionDto> menuSections;
}
