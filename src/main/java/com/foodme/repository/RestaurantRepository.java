package com.foodme.repository;

import com.foodme.model.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Lev on 5/30/2016.
 */
@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    Restaurant findOneByNameAndEmailAllIgnoreCase(String name, String email);
    List<Restaurant> findByCreatedById(Long id);
    List<Restaurant> findByCountryAndCity(String country, String city);
    Restaurant findOneByMenusId(Long id);
}
