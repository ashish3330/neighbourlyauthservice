package com.neighbourly.userservice.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ratings")
@Data
public class RatingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "service_provider_profile_id")
    private ServiceProviderProfileEntity serviceProviderProfile;
    @ManyToOne
    @JoinColumn(name = "rater_user_id")
    private User raterUser;
    @ManyToOne
    @JoinColumn(name = "service_id")
    private ServiceEntity service;
    private BigDecimal rating;
    private String comment;
    private LocalDateTime createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ServiceProviderProfileEntity getServiceProviderProfile() {
        return serviceProviderProfile;
    }

    public void setServiceProviderProfile(ServiceProviderProfileEntity serviceProviderProfile) {
        this.serviceProviderProfile = serviceProviderProfile;
    }

    public User getRaterUser() {
        return raterUser;
    }

    public void setRaterUser(User raterUser) {
        this.raterUser = raterUser;
    }

    public ServiceEntity getService() {
        return service;
    }

    public void setService(ServiceEntity service) {
        this.service = service;
    }

    public BigDecimal getRating() {
        return rating;
    }

    public void setRating(BigDecimal rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

}
