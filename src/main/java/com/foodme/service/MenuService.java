package com.foodme.service;

import com.foodme.model.Menu;
import com.foodme.model.Restaurant;
import com.foodme.repository.MenuRepository;
import com.foodme.repository.RestaurantRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MenuService {

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Transactional
    public Menu update(Menu menu) {
        if (StringUtils.isBlank(menu.getName())) {
            return null;
        }

        if (menu.getRestaurant() == null) {
            menu.setRestaurant(restaurantRepository.findOneByMenusId(menu.getId()));
        }

        menu = menuRepository.saveAndFlush(menu);
        return menu;
    }

    @Transactional
    public Menu save(Menu menu, Long restaurantId) {
        Restaurant restaurant = restaurantRepository.findOne(restaurantId);
        if (restaurant == null) {
            return null;
        }
        menu.setRestaurant(restaurant);
        return menuRepository.saveAndFlush(menu);
    }

    public Menu findById(Long id) {
        return menuRepository.findOne(id);
    }

    public boolean isMenuExist(Menu menu, Long restaurantId) {
        return menuRepository.findOneByNameIgnoreCaseAndRestaurantId(menu.getName(), restaurantId) != null;
    }

    public boolean isMenuExist(Long menuId) {
        return menuRepository.exists(menuId);
    }


    @Transactional
    public void deleteMenuById(Long id) {
        Menu menu = menuRepository.findOne(id);
        Restaurant restaurant = menu.getRestaurant();
        restaurant.deleteMenu(menu);

        restaurantRepository.save(restaurant);
    }
}
