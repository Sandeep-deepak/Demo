package com.supreme.repository;

import com.supreme.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepo extends JpaRepository<Order, Long> {
    Optional<Order> findByOrderImgName(String orderImgName);
}
