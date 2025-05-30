package com.neighbourly.userservice.service;

import com.neighbourly.commonservice.dispatcher.Dispatcher;
import com.neighbourly.commonservice.dispatcher.SyncDispatcher;
import com.neighbourly.commonservice.errorhandling.Either;
import com.neighbourly.userservice.command.SetAddressCommand;
import com.neighbourly.userservice.command.SetLocationCommand;
import com.neighbourly.userservice.dto.SetAddressRequestDTO;
import com.neighbourly.userservice.dto.SetLocationRequestDTO;
import com.neighbourly.userservice.dto.UserDTO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class UserLocationService {

    private final SyncDispatcher syncDispatcher;

    public UserLocationService(@Qualifier("setLocationSyncDispatcher") SyncDispatcher syncDispatcher) {
        this.syncDispatcher = syncDispatcher;
    }

    public Either<String, UserDTO> setLocation(SetLocationRequestDTO setLocationRequestDTO) {
        return syncDispatcher.dispatch(new SetLocationCommand(setLocationRequestDTO));
    }

    public Either<String, UserDTO> setAddress(SetAddressRequestDTO setAddressRequestDTO) {
        return syncDispatcher.dispatch(new SetAddressCommand(setAddressRequestDTO));
    }
}