package com.foodme.repository;

import com.foodme.model.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Lev on 5/30/2016.
 */
@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {
    Menu findOneByNameIgnoreCaseAndRestaurantId(String name, Long restaurantId);

    List<Menu> findByRestaurantIdOrderByIdAsc(Long restaurantId);

    Menu findOneByMenuSectionsId(Long menuSectionId);
}
