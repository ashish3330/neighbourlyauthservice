package com.neighbourly.userservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "documents")
@Data
public class DocumentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "service_id")
    private ServiceEntity service;
    private String documentType;
    private String filePath;
    private String status;
    private Instant uploadedAt;
    private Instant verifiedAt;

}