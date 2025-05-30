package com.neighbourly.userservice.repository;

import com.neighbourly.userservice.entity.ServiceProviderProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ServiceProviderProfileRepository extends JpaRepository<ServiceProviderProfileEntity, Long> {
    Optional<ServiceProviderProfileEntity> findByUserIdAndServiceId(Long userId, Long serviceId);
    List<ServiceProviderProfileEntity> findByUserId(Long userId);
}
