package com.foodme.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class Option extends BaseOption{
    @ManyToOne
    private Dish dish;
}
