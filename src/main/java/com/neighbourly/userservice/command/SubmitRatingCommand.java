package com.neighbourly.userservice.command;

import com.neighbourly.commonservice.dispatcher.Command;
import com.neighbourly.userservice.dto.RatingDTO;
import com.neighbourly.userservice.dto.SubmitRatingRequestDTO;
import lombok.Getter;

@Getter
public class SubmitRatingCommand extends Command<RatingDTO> {
    private final SubmitRatingRequestDTO requestDTO;

    public SubmitRatingCommand(SubmitRatingRequestDTO requestDTO) {
        this.requestDTO = requestDTO;
    }

}
