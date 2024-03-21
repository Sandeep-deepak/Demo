package com.supreme.repository;

import com.supreme.entity.ProductsNewQty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductsNewQtyRepo extends JpaRepository<ProductsNewQty, Long> {
}
