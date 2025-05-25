package com.neighbourly.userservice.config;

import com.neighbourly.commonservice.dispatcher.registry.HandlerRegistry;
import com.neighbourly.userservice.command.*;
import com.neighbourly.userservice.handler.*;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Configuration
public class UserServiceConfig {

    private final HandlerRegistry handlerRegistry;
    private final RegisterUserCommandHandler registerUserCommandHandler;
    private final LoginUserCommandHandler loginUserCommandHandler;
    private final ChangePasswordCommandHandler changePasswordCommandHandler;
    private final GoogleSsoLoginCommandHandler googleSsoLoginCommandHandler;

    public UserServiceConfig(HandlerRegistry handlerRegistry,
                             RegisterUserCommandHandler registerUserCommandHandler,
                             LoginUserCommandHandler loginUserCommandHandler,
                             ChangePasswordCommandHandler changePasswordCommandHandler,
                             GoogleSsoLoginCommandHandler googleSsoLoginCommandHandler) {
        this.handlerRegistry = handlerRegistry;
        this.registerUserCommandHandler = registerUserCommandHandler;
        this.loginUserCommandHandler = loginUserCommandHandler;
        this.changePasswordCommandHandler = changePasswordCommandHandler;
        this.googleSsoLoginCommandHandler = googleSsoLoginCommandHandler;
    }

    @PostConstruct
    public void registerHandlers() {
        handlerRegistry.registerHandler(RegisterUserCommand.class, registerUserCommandHandler);
        handlerRegistry.registerHandler(LoginUserCommand.class, loginUserCommandHandler);
        handlerRegistry.registerHandler(ChangePasswordCommand.class, changePasswordCommandHandler);
        handlerRegistry.registerHandler(GoogleSsoLoginCommand.class, googleSsoLoginCommandHandler);
    }

    @Component
    public static class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

        @Override
        public void commence(HttpServletRequest request, HttpServletResponse response,
                             AuthenticationException authException) throws IOException {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
        }
    }
}