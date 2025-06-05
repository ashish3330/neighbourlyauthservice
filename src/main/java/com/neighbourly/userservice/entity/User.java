package com.neighbourly.userservice.entity;

import com.vladmihalcea.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.Type;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@Getter
@Entity
@Table(name = "users")
public class User {

    // Getters and Setters
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String phoneNumber;

    @Column(nullable = false)
    private String password;

    @Column
    private String googleId;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id")
    private Address address;

    @Column
    private Double latitude;

    @Column
    private Double longitude;

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private Map<String, List<String>> roles;

    public void setId(Long id) { this.id = id; }

    public void setName(String name) { this.name = name; }

    public void setEmail(String email) { this.email = email; }

    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public void setPassword(String password) { this.password = password; }

    public void setGoogleId(String googleId) { this.googleId = googleId; }

    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public void setAddress(Address address) { this.address = address; }

    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public void setRoles(Map<String, List<String>> roles) { this.roles = roles; }
}
