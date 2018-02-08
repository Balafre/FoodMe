package com.foodme.model;

import com.foodme.enumeration.OptionType;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Data
@Entity
public class Dish extends BaseAuditEntity implements Comparable<Dish>{
    @Length(max = 50)
    private String name;

    private String description;

    @Column(name = "dish_picture_url")
    private String dishPictureUrl;

    @OneToMany(mappedBy="dish")
    @MapKeyEnumerated(EnumType.STRING)
    private Map<OptionType, Option> options = new HashMap<>();

    @ManyToOne(cascade = CascadeType.PERSIST)
    private MenuSection menuSection;

    private BigDecimal price;

    public Dish(){}

    public Dish(String name, String description) {
        this.name = name;
        this.description = description;
    }

    @Override
    public int compareTo(Dish dish) {
        if(this.id == dish.id) {
            return 0;
        }
        return this.id > dish.id ? 1 : -1;
    }
}
