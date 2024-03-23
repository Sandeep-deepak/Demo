package com.supreme.repository;

import com.supreme.entity.DistributorProductQuantity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DistributorProductQtyRepo extends JpaRepository<DistributorProductQuantity, Long> {

    @Query("SELECT dpq.currentQty FROM DistributorProductQuantity dpq WHERE dpq.distributorProfile.id = :distributorId AND dpq.product.id = :productId")
    int findCurrentQtyByDistributorIdAndProductId(Long distributorId, Long productId);

//    Integer findCurrentQtyByDistributorProfileIdAndProductProductId(Long distributorId, Long productId);

    List<DistributorProductQuantity> findByDistributorProfileId(Long distributorId);

//    @Query("SELECT dp.product.id AS productId, dp.currentQty AS currentQty FROM DistributorProductQuantity dp WHERE dp.distributorProfile.id = :distributorId")
//    List<Object[]> findProductIdAndCurrentQtyByDistributorId(Long distributorId);
//    List<Object[]> findProductIdAndCurrentQtyByDistributorProfileId(Long distributorId);
}
