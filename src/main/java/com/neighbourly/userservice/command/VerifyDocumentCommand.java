package com.neighbourly.userservice.command;

import com.neighbourly.commonservice.dispatcher.Command;
import com.neighbourly.userservice.dto.DocumentDTO;
import com.neighbourly.userservice.dto.VerifyDocumentRequestDTO;
import lombok.Getter;

@Getter
public class VerifyDocumentCommand extends Command<DocumentDTO> {
    private final VerifyDocumentRequestDTO requestDTO;

    public VerifyDocumentCommand(VerifyDocumentRequestDTO requestDTO) {
        this.requestDTO = requestDTO;
    }

}