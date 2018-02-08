package com.foodme.repository;

import com.foodme.model.GeneralOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GeneralOrderRepository extends JpaRepository<GeneralOrder, Long> {
}
