package com.supreme.repository;

import com.supreme.entity.ExecutiveProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExecutiveProfileRepo extends JpaRepository<ExecutiveProfile, Long> {
    Optional<ExecutiveProfile> findByMobileNumber(String mobileNumber);

    Optional<ExecutiveProfile> deleteByMobileNumber(String mobileNumber);

    Boolean existsByMobileNumber(String mobileNumber);

    public List<ExecutiveProfile> getExecutiveProfileByActive(boolean active);

    public List<ExecutiveProfile> getExecutiveProfileByDeleted(boolean deleted);

    public List<ExecutiveProfile> findByActiveAndDeleted(Boolean active, Boolean deleted);

}
