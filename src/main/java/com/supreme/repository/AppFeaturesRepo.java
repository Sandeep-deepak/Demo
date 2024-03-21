package com.supreme.repository;

import com.supreme.entity.AppFeatures;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppFeaturesRepo extends JpaRepository<AppFeatures, Long> {
}
