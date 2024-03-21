package com.supreme.repository;

import com.supreme.entity.AdminProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminProfileRepo extends JpaRepository<AdminProfile, Long> {
    Optional<AdminProfile> findByMobileNumber(String mobileNumber);
    Optional<AdminProfile> deleteByMobileNumber(String mobileNumber);

}
