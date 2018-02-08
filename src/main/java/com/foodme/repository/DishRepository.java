package com.foodme.repository;

import com.foodme.model.Dish;
import com.foodme.model.MenuSection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface DishRepository extends JpaRepository<Dish, Long> {
    MenuSection findOneByNameIgnoreCaseAndMenuSectionId(String name, Long menuSectionId);

    @Modifying
    @Transactional
    @Query("delete from Dish d where d.id = ?1")
    void deleteDishById(Long id);
}
