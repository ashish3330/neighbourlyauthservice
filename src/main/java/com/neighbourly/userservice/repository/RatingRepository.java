package com.neighbourly.userservice.repository;

import com.neighbourly.userservice.entity.RatingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RatingRepository extends JpaRepository<RatingEntity, Long> {
    List<RatingEntity> findByServiceProviderProfileId(Long serviceProviderProfileId);
    Optional<RatingEntity> findByServiceProviderProfileIdAndRaterUserIdAndServiceId(
            Long serviceProviderProfileId, Long raterUserId, Long serviceId);
}