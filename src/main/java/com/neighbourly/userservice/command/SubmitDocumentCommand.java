package com.neighbourly.userservice.command;

import com.neighbourly.commonservice.dispatcher.Command;
import com.neighbourly.userservice.dto.DocumentDTO;
import com.neighbourly.userservice.dto.SubmitDocumentRequestDTO;
import lombok.Getter;

@Getter
public class SubmitDocumentCommand extends Command<DocumentDTO> {
    private final SubmitDocumentRequestDTO requestDTO;

    public SubmitDocumentCommand(SubmitDocumentRequestDTO requestDTO) {
        this.requestDTO = requestDTO;
    }

}