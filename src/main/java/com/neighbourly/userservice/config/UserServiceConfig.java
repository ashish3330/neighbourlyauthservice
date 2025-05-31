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
    private  final RefreshTokenHandler refreshTokenCommandHandler;
    private  final CreateServiceProviderProfileCommandHandler createServiceProviderProfileCommandHandler;
    private final SubmitDocumentCommandHandler submitDocumentCommandHandler;
    private final SubmitRatingCommandHandler submitRatingCommandHandler;
    private final VerifyDocumentCommandHandler verifyDocumentCommandHandler;

    public UserServiceConfig(HandlerRegistry handlerRegistry,
                             RegisterUserCommandHandler registerUserCommandHandler,
                             LoginUserCommandHandler loginUserCommandHandler,
                             ChangePasswordCommandHandler changePasswordCommandHandler,
                             GoogleSsoLoginCommandHandler googleSsoLoginCommandHandler,
                             SetLocationCommandHandler setLocationCommandHandler, RequestOtpCommandHandler requestOtpCommandHandler,
                             SetPasswordCommandHandler setPasswordCommandHandler, RefreshTokenHandler refreshTokenCommandHandler, CreateServiceProviderProfileCommandHandler createServiceProviderProfileCommandHandler, SubmitDocumentCommandHandler submitDocumentCommandHandler, SubmitRatingCommandHandler submitRatingCommandHandler, VerifyDocumentCommandHandler verifyDocumentCommandHandler) {
        this.handlerRegistry = handlerRegistry;
        this.registerUserCommandHandler = registerUserCommandHandler;
        this.loginUserCommandHandler = loginUserCommandHandler;
        this.changePasswordCommandHandler = changePasswordCommandHandler;
        this.googleSsoLoginCommandHandler = googleSsoLoginCommandHandler;
        this.setLocationCommandHandler = setLocationCommandHandler;
        this.requestOtpCommandHandler = requestOtpCommandHandler;
        this.setPasswordCommandHandler = setPasswordCommandHandler;
        this.refreshTokenCommandHandler = refreshTokenCommandHandler;
        this.createServiceProviderProfileCommandHandler = createServiceProviderProfileCommandHandler;
        this.submitDocumentCommandHandler = submitDocumentCommandHandler;
        this.submitRatingCommandHandler = submitRatingCommandHandler;
        this.verifyDocumentCommandHandler = verifyDocumentCommandHandler;
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
        handlerRegistry.registerHandler(CreateServiceProviderProfileCommand.class, createServiceProviderProfileCommandHandler);
        handlerRegistry.registerHandler(SubmitDocumentCommand.class, submitDocumentCommandHandler);
        handlerRegistry.registerHandler(SubmitRatingCommand.class, submitRatingCommandHandler);
        handlerRegistry.registerHandler(VerifyDocumentCommand.class, verifyDocumentCommandHandler);
    }



    @Bean(name = "userServiceSyncDispatcher")
    public SyncDispatcher userServiceSyncDispatcher(HandlerRegistry handlerRegistry) {
        return new SyncDispatcher(handlerRegistry);
    }


    @Bean(name = "userProfileSyncDispatcher")
    public SyncDispatcher userProfileSyncDispatcher(HandlerRegistry handlerRegistry) {
        return new SyncDispatcher(handlerRegistry);
    }


    @Bean(name = "setLocationSyncDispatcher")
    public SyncDispatcher setLocationSyncDispatcher(HandlerRegistry handlerRegistry) {
        return new SyncDispatcher(handlerRegistry);
    }

}