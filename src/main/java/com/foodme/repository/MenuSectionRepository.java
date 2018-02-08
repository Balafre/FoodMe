package com.foodme.repository;

import com.foodme.model.MenuSection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Lev on 5/30/2016.
 */
@Repository
public interface MenuSectionRepository extends JpaRepository<MenuSection, Long> {
    MenuSection findOneByNameIgnoreCaseAndMenuId(String name, Long menuId);

    MenuSection findOneByDishesId(Long dishId);
}
