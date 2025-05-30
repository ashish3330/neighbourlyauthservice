package com.neighbourly.userservice.config;

import com.neighbourly.commonservice.dispatcher.Dispatcher;
import com.neighbourly.commonservice.dispatcher.SyncDispatcher;
import com.neighbourly.commonservice.dispatcher.registry.HandlerRegistry;
import com.neighbourly.userservice.command.*;
import com.neighbourly.userservice.handler.*;
import jakarta.annotation.PostConstruct;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class UserServiceConfig {

    private final HandlerRegistry handlerRegistry;
    private final RegisterUserCommandHandler registerUserCommandHandler;
    private final LoginUserCommandHandler loginUserCommandHandler;
    private final ChangePasswordCommandHandler changePasswordCommandHandler;
    private final GoogleSsoLoginCommandHandler googleSsoLoginCommandHandler;
    private final SetLocationCommandHandler setLocationCommandHandler;
    private  final RequestOtpCommandHandler requestOtpCommandHandler;
    private  final SetPasswordCommandHandler setPasswordCommandHandler;
    private  final RefreshTokenCommandHandler refreshTokenCommandHandler;

    public UserServiceConfig(HandlerRegistry handlerRegistry,
                             RegisterUserCommandHandler registerUserCommandHandler,
                             LoginUserCommandHandler loginUserCommandHandler,
                             ChangePasswordCommandHandler changePasswordCommandHandler,
                             GoogleSsoLoginCommandHandler googleSsoLoginCommandHandler,
                             SetLocationCommandHandler setLocationCommandHandler, RequestOtpCommandHandler requestOtpCommandHandler,
                             SetPasswordCommandHandler setPasswordCommandHandler, RefreshTokenCommandHandler refreshTokenCommandHandler) {
        this.handlerRegistry = handlerRegistry;
        this.registerUserCommandHandler = registerUserCommandHandler;
        this.loginUserCommandHandler = loginUserCommandHandler;
        this.changePasswordCommandHandler = changePasswordCommandHandler;
        this.googleSsoLoginCommandHandler = googleSsoLoginCommandHandler;
        this.setLocationCommandHandler = setLocationCommandHandler;
        this.requestOtpCommandHandler = requestOtpCommandHandler;
        this.setPasswordCommandHandler = setPasswordCommandHandler;
        this.refreshTokenCommandHandler = refreshTokenCommandHandler;
    }

    @PostConstruct
    public void registerHandlers() {
        handlerRegistry.registerHandler(RegisterUserCommand.class, registerUserCommandHandler);
        handlerRegistry.registerHandler(LoginUserCommand.class, loginUserCommandHandler);
        handlerRegistry.registerHandler(ChangePasswordCommand.class, changePasswordCommandHandler);
        handlerRegistry.registerHandler(SetLocationCommand.class, setLocationCommandHandler);
        handlerRegistry.registerHandler(GoogleSsoLoginCommand.class, googleSsoLoginCommandHandler);
        handlerRegistry.registerHandler(RequestOtpCommand.class, requestOtpCommandHandler);
        handlerRegistry.registerHandler(SetPasswordCommand.class, setPasswordCommandHandler);
        handlerRegistry.registerHandler(RefreshTokenCommand.class, refreshTokenCommandHandler);
    }



    @Bean(name = "userServiceSyncDispatcher")
    public Dispatcher syncDispatcher(HandlerRegistry handlerRegistry) {
        return new SyncDispatcher(handlerRegistry);
    }

}