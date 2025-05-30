package com.neighbourly.userservice.controller;
import com.neighbourly.commonservice.errorhandling.Either;
import com.neighbourly.userservice.dto.*;
import com.neighbourly.userservice.service.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user-profiles")
public class UserProfileController {

    private final UserProfileService userProfileService;

    @Autowired
    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @PostMapping("/service-profiles")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> createServiceProviderProfile(@RequestBody CreateServiceProviderProfileRequestDTO request) {
        Either<String, ServiceProviderProfileDTO> result = userProfileService.createServiceProviderProfile(request);
        return result.isRight()
                ? ResponseEntity.ok(result.getRight())
                : ResponseEntity.badRequest().body(result.getLeft());
    }

    @GetMapping("/{userId}/services/{serviceId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getServiceProviderProfile(@PathVariable Long userId, @PathVariable Long serviceId) {
        Either<String, ServiceProviderProfileDTO> result = userProfileService.getServiceProviderProfile(userId, serviceId);
        return result.isRight()
                ? ResponseEntity.ok(result.getRight())
                : ResponseEntity.badRequest().body(result.getLeft());
    }

    @PostMapping("/documents")
    @PreAuthorize("hasAnyAuthority('ROLE_FOOD_DELIVERY_PROVIDER','ROLE_CAB_RENTING_PROVIDER','ROLE_BIKE_RENTAL_PROVIDER','ROLE_LOCAL_BUSINESS_DELIVERY_PROVIDER','ROLE_DELIVERY_SHARING_PROVIDER','ROLE_NEXTDOOR_PROVIDER')")
    public ResponseEntity<?> submitDocument(@RequestBody SubmitDocumentRequestDTO request) {
        Either<String, DocumentDTO> result = userProfileService.submitDocument(request);
        return result.isRight()
                ? ResponseEntity.ok(result.getRight())
                : ResponseEntity.badRequest().body(result.getLeft());
    }

    @PostMapping("/documents/verify")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> verifyDocument(@RequestBody VerifyDocumentRequestDTO request) {
        Either<String, DocumentDTO> result = userProfileService.verifyDocument(request);
        return result.isRight()
                ? ResponseEntity.ok(result.getRight())
                : ResponseEntity.badRequest().body(result.getLeft());
    }

    @PostMapping("/ratings")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> submitRating(@RequestBody SubmitRatingRequestDTO request) {
        Either<String, RatingDTO> result = userProfileService.submitRating(request);
        return result.isRight()
                ? ResponseEntity.ok(result.getRight())
                : ResponseEntity.badRequest().body(result.getLeft());
    }

    @GetMapping("/profiles/{profileId}/ratings")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getRatings(@PathVariable Long profileId) {
        Either<String, List<RatingDTO>> result = userProfileService.getRatingsForProvider(profileId);
        return result.isRight()
                ? ResponseEntity.ok(result.getRight())
                : ResponseEntity.badRequest().body(result.getLeft());
    }
}