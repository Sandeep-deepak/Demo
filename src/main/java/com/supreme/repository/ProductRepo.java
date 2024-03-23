package com.supreme.repository;

import com.supreme.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepo extends JpaRepository<Product, Long> {

    Optional<Product> findByProductName(String productName);

    Optional<Product> deleteByProductName(String productName);

    Boolean existsByProductName(String productName);

}
