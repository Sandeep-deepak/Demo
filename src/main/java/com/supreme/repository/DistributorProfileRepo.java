package com.supreme.repository;

import com.supreme.entity.DistributorProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DistributorProfileRepo extends JpaRepository<DistributorProfile, Long> {
    Optional<DistributorProfile> findByMobileNumber(String mobileNumber);

    Optional<DistributorProfile> deleteByMobileNumber(String mobileNumber);

    // Filter StaffProfiles using Active/inActive Status and return List of StaffProfiles
    public List<DistributorProfile> getDistributorProfileByActive(boolean active);

    public List<DistributorProfile> getDistributorProfileByDeleted(boolean deleted);

    public List<DistributorProfile> findByActiveAndDeleted(Boolean active, Boolean deleted);

}
