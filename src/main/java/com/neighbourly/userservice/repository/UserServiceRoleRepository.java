package com.neighbourly.userservice.repository;

import com.neighbourly.userservice.entity.UserServiceRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;



@Repository


public interface UserServiceRoleRepository extends JpaRepository<UserServiceRoleEntity, Long> {
    List<UserServiceRoleEntity> findByUserId(Long userId);
    Optional<UserServiceRoleEntity> findByUserIdAndServiceIdAndRole(Long userId, Long serviceId, String role);
}