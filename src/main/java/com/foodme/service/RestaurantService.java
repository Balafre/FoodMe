package com.foodme.service;

import com.foodme.model.Account;
import com.foodme.model.GeoLocation;
import com.foodme.model.Menu;
import com.foodme.model.Restaurant;
import com.foodme.repository.AccountRepository;
import com.foodme.repository.MenuRepository;
import com.foodme.repository.RestaurantRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.foodme.util.StreamUtils.streamOf;

@Service
public class RestaurantService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RestaurantService.class);
    private static final double DEFAULT_SEARCH_RANGE = 100; //default range in meters to search the restaurants within

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private AccountRepository accountRepository;

    public boolean isRestaurantExist(Restaurant restaurant) {
        return restaurantRepository.findOneByNameAndEmailAllIgnoreCase(
                restaurant.getName(), restaurant.getEmail()) != null;
    }

    public boolean isRestaurantExist(Long id) {
        return restaurantRepository.exists(id);
    }

    @Transactional
    public Restaurant save(Restaurant restaurant, Long accountId) {
        Account account = accountRepository.findOne(accountId);
        if (account == null) {
            LOGGER.info("Account# {} does not exist", accountId);
        }
        restaurant.setCreatedBy(account);
        restaurant = restaurantRepository.saveAndFlush(restaurant);

        return restaurant;
    }

    @Transactional
    public Restaurant save(Restaurant restaurant) {
        restaurant = restaurantRepository.saveAndFlush(restaurant);
        return restaurant;
    }

    public Restaurant findById(Long id) {
        return restaurantRepository.findOne(id);
    }

    @Transactional
    public void deleteRestaurantById(Long id) {
        restaurantRepository.delete(id);
    }

    public List<Menu> findAllMenusByRestaurant(Long restaurantId) {
        List<Menu> menus = menuRepository.findByRestaurantIdOrderByIdAsc(restaurantId);
        streamOf(menus).forEach(menu -> {
            Collections.sort(menu.getMenuSections());
            streamOf(menu.getMenuSections()).forEach(menuSection -> {
                Collections.sort(menuSection.getDishes());
            });
        });

        return menus;
    }

    public List<Restaurant> findAllRestaurantsByAccount(Long id) {
        List<Restaurant> restaurants = restaurantRepository.findByCreatedById(id);

        return restaurants;
    }

    public List<Restaurant> findRestaurantsWithinRange(String country,
                                                       String city,
                                                       double latitude,
                                                       double longitude,
                                                       double range) {

        List<Restaurant> restaurants = restaurantRepository.findByCountryAndCity(country, city);
        if(restaurants == null) {
            return null;
        }

        GeoLocation currLocation = new GeoLocation(latitude, longitude);

        List<Restaurant> results = new ArrayList<>();
        restaurants.forEach(restaurant -> {
            Double distance = currLocation.distanceFrom(restaurant.getGeoLocation()) * 1000;
            if(distance <= range) {
                results.add(restaurant);
            }
        });

        return results;
    }
}
