package com.neighbourly.userservice.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.google.api.client.json.jackson2.JacksonFactory;

import java.util.Collections;

@Service
public class GoogleSsoService {

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    public GoogleUserInfo verifyIdToken(String idTokenString) throws Exception {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), JacksonFactory.getDefaultInstance())
                .setAudience(Collections.singletonList(clientId))
                .build();

        GoogleIdToken idToken = verifier.verify(idTokenString);
        if (idToken == null) {
            throw new IllegalArgumentException("Invalid ID token");
        }

        GoogleIdToken.Payload payload = idToken.getPayload();
        return new GoogleUserInfo(
                payload.getSubject(),
                (String) payload.get("email"),
                (String) payload.get("name")
        );
    }

    public static class GoogleUserInfo {
        private final String googleId;
        private final String email;
        private final String name;

        public GoogleUserInfo(String googleId, String email, String name) {
            this.googleId = googleId;
            this.email = email;
            this.name = name;
        }

        public String getGoogleId() { return googleId; }
        public String getEmail() { return email; }
        public String getName() { return name; }
    }
}