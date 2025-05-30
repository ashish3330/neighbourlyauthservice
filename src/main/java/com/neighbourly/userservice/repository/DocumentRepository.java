package com.neighbourly.userservice.repository;

import com.neighbourly.userservice.entity.DocumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentRepository extends JpaRepository<DocumentEntity, Long> {
    List<DocumentEntity> findByUserIdAndServiceId(Long userId, Long serviceId);
}