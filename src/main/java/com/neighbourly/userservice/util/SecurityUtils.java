package com.neighbourly.userservice.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SecurityUtils {

    private static final Logger logger = LoggerFactory.getLogger(SecurityUtils.class);

    /**
     * Retrieves the CustomPrincipal from the SecurityContextHolder.
     *
     * @return CustomPrincipal containing user details
     * @throws IllegalStateException if the user is not authenticated or principal is not a CustomPrincipal
     */
    public CustomPrincipal getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof CustomPrincipal principal)) {
            logger.warn("No authenticated user found in SecurityContext");
            throw new IllegalStateException("User not authenticated or invalid principal type");
        }

        logger.debug("Fetched user from SecurityContext - UserId: {}, Email: {}, Roles: {}",
                principal.userId(), principal.email(), principal.roles());
        return principal;
    }

    /**
     * Retrieves the current user's ID.
     *
     * @return Long userId
     * @throws IllegalStateException if user is not authenticated
     */
    public Long getCurrentUserId() {
        return getCurrentUser().userId();
    }

    /**
     * Retrieves the current user's email.
     *
     * @return String email
     * @throws IllegalStateException if user is not authenticated
     */
    public String getCurrentUserEmail() {
        return getCurrentUser().email();
    }

    /**
     * Retrieves the current user's roles.
     *
     * @return List<String> roles
     * @throws IllegalStateException if user is not authenticated
     */
    public List<String> getCurrentUserRoles() {
        return getCurrentUser().roles();
    }
}