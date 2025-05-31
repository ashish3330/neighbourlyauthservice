package com.neighbourly.userservice.handler;

import com.neighbourly.commonservice.dispatcher.CommandHandler;
import com.neighbourly.commonservice.errorhandling.Either;
import com.neighbourly.userservice.command.SubmitRatingCommand;
import com.neighbourly.userservice.dto.RatingDTO;
import com.neighbourly.userservice.dto.SubmitRatingRequestDTO;
import com.neighbourly.userservice.entity.RatingEntity;
import com.neighbourly.userservice.entity.ServiceEntity;
import com.neighbourly.userservice.entity.ServiceProviderProfileEntity;
import com.neighbourly.userservice.entity.User;
import com.neighbourly.userservice.exception.InvalidInputException;
import com.neighbourly.userservice.repository.RatingRepository;
import com.neighbourly.userservice.repository.ServiceProviderProfileRepository;
import com.neighbourly.userservice.repository.ServiceRepository;
import com.neighbourly.userservice.repository.UserRepository;
import com.neighbourly.userservice.util.CommonValidationUtil;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;

@Component
public class SubmitRatingCommandHandler implements CommandHandler<SubmitRatingCommand, RatingDTO> {

    private final ServiceProviderProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final ServiceRepository serviceRepository;
    private final RatingRepository ratingRepository;
    private final ModelMapper modelMapper;
    private final CommonValidationUtil validationUtil;

    public SubmitRatingCommandHandler(
            ServiceProviderProfileRepository profileRepository,
            UserRepository userRepository,
            ServiceRepository serviceRepository,
            RatingRepository ratingRepository,
            ModelMapper modelMapper,
            CommonValidationUtil validationUtil) {
        this.profileRepository = profileRepository;
        this.userRepository = userRepository;
        this.serviceRepository = serviceRepository;
        this.ratingRepository = ratingRepository;
        this.modelMapper = modelMapper;
        this.validationUtil = validationUtil;
    }

    @Override
    @Transactional
    public Either<String, RatingDTO> handle(SubmitRatingCommand command) {
        try {
            SubmitRatingRequestDTO dto = command.getRequestDTO();
            validationUtil.validateNotNull(dto.serviceProviderProfileId(), "Service provider profile ID");
            validationUtil.validateNotNull(dto.raterUserId(), "Rater user ID");
            validationUtil.validateNotNull(dto.serviceId(), "Service ID");
            validationUtil.validateNotNull(dto.rating(), "Rating");
            if (dto.rating().compareTo(BigDecimal.valueOf(1.0)) < 0 || dto.rating().compareTo(BigDecimal.valueOf(5.0)) > 0) {
                return Either.left("Rating must be between 1.0 and 5.0");
            }

            ServiceProviderProfileEntity profile = profileRepository.findById(dto.serviceProviderProfileId())
                    .orElseThrow(() -> new InvalidInputException("Service provider profile not found with id: " + dto.serviceProviderProfileId()));
            User rater = userRepository.findById(dto.raterUserId())
                    .orElseThrow(() -> new InvalidInputException("User not found with id: " + dto.raterUserId()));
            ServiceEntity service = serviceRepository.findById(dto.serviceId())
                    .orElseThrow(() -> new InvalidInputException("Service not found with id: " + dto.serviceId()));

            if (ratingRepository.findByServiceProviderProfileIdAndRaterUserIdAndServiceId(
                    dto.serviceProviderProfileId(), dto.raterUserId(), dto.serviceId()).isPresent()) {
                return Either.left("Rating already exists for this user and provider");
            }

            RatingEntity rating = new RatingEntity();
            rating.setServiceProviderProfile(profile);
            rating.setRaterUser(rater);
            rating.setService(service);
            rating.setRating(dto.rating());
            rating.setComment(dto.comment());
            RatingEntity savedRating = ratingRepository.save(rating);

            updateAverageRating(profile);

            return Either.right(mapToDTO(savedRating));
        } catch (InvalidInputException e) {
            return Either.left(e.getMessage());
        } catch (Exception e) {
            return Either.left("Failed to submit rating: " + e.getMessage());
        }
    }

    private void updateAverageRating(ServiceProviderProfileEntity profile) {
        List<RatingEntity> ratings = ratingRepository.findByServiceProviderProfileId(profile.getId());
        if (!ratings.isEmpty()) {
            BigDecimal avgRating = ratings.stream()
                    .map(RatingEntity::getRating)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .divide(BigDecimal.valueOf(ratings.size()), 2, RoundingMode.HALF_UP);
            profile.setAverageRating(avgRating);
            profile.setRatingCount(ratings.size());
        }
        profile.setUpdatedAt(Instant.now());
        profileRepository.save(profile);
    }

    private RatingDTO mapToDTO(RatingEntity entity) {
        return modelMapper.map(entity, RatingDTO.class);
    }
}