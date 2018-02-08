package com.foodme.model;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Entity
@Table(name = "menu_section")
public class MenuSection extends BaseAuditEntity implements Comparable<MenuSection>{
    @Length(max = 50)
    private String name;

    private String description;

    @Column(name = "menu_section_picture_url")
    private String menuSectionPictureUrl;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER,
            mappedBy = "menuSection", orphanRemoval = true)
    private List<Dish> dishes = new ArrayList<>();

    @ManyToOne(cascade = CascadeType.PERSIST)
    private Menu menu;

    public MenuSection(){}

    public MenuSection(String name, String description) {
        this.name = name;
        this.description = description;
    }

    @Override
    public int compareTo(MenuSection ms) {
        if(this.id == ms.id) {
            return 0;
        }
        return this.id > ms.id ? 1 : -1;
    }
}
