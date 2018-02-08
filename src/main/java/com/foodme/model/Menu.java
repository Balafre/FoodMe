package com.foodme.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Entity
public class Menu extends BaseAuditEntity {
    @Column(nullable = false)
    @Length(max = 50)
    private String name;

    private String description;

    @ManyToOne(cascade = CascadeType.PERSIST)
    private Restaurant restaurant;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER,
            mappedBy = "menu",
            orphanRemoval = true)
    private List<MenuSection> menuSections = new ArrayList<>();

    public Menu(){}

    public Menu(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public void deleteSection(MenuSection section) {
       for(MenuSection mSec : this.getMenuSections()) {
           if(mSec.equals(section)) {
               this.getMenuSections().remove(section);
               break;
           }
       }
    }
}
