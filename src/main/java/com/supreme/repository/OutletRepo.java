package com.supreme.repository;

import com.supreme.entity.Outlet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OutletRepo extends JpaRepository<Outlet, Long> {

    Optional<Outlet> findByOutletName(String outletName);

    Optional<Outlet> deleteByOutletName(String outletName);

    Boolean existsByOutletName(String outletName);

    Boolean existsByMobileNumber(String mobileNumber);

}
