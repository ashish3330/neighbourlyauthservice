package com.neighbourly.userservice.command;

import com.neighbourly.commonservice.dispatcher.Command;
import com.neighbourly.userservice.dto.RequestOtpDTO;

public class RequestOtpCommand extends Command<String> {
    private final RequestOtpDTO requestOtpDTO;

    public RequestOtpCommand(RequestOtpDTO requestOtpDTO) {
        this.requestOtpDTO = requestOtpDTO;
    }

    public RequestOtpDTO getRequestOtpDTO() {
        return requestOtpDTO;
    }
}