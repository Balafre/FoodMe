package com.foodme.service;

import com.foodme.model.Dish;
import com.foodme.model.MenuSection;
import com.foodme.repository.DishRepository;
import com.foodme.repository.MenuSectionRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DishService {

    @Autowired
    private DishRepository dishRepository;

    @Autowired
    private MenuSectionRepository menuSectionRepository;

    @Transactional
    public Dish update(Dish dish) {
        if (StringUtils.isBlank(dish.getName())) {
            return null;
        }

        if(dish.getMenuSection() == null) {
            dish.setMenuSection(menuSectionRepository.findOneByDishesId(dish.getId()));
        }

        dish = dishRepository.saveAndFlush(dish);
        return dish;
    }

    @Transactional
    public Dish save(Dish dish, Long menuSectionId) {
        MenuSection menuSection = menuSectionRepository.findOne(menuSectionId);
        if (menuSection == null) {
            return null;
        }
        dish.setMenuSection(menuSection);
        return update(dish);
    }

    public Dish findById(Long id) {
        return dishRepository.findOne(id);
    }

    public boolean isDishExist(Dish dish, Long menuSectionId) {
        return dishRepository.findOneByNameIgnoreCaseAndMenuSectionId(dish.getName(), menuSectionId) != null;
    }

    public boolean isDishExist(Long dishId) {
        return dishRepository.exists(dishId);
    }

    @Transactional
    public void deleteDishById(Long id) {
        dishRepository.deleteDishById(id);
    }
}
