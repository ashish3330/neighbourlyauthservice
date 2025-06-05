package com.neighbourly.userservice.util;

import com.neighbourly.commonservice.errorhandling.Either;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class ControllerUtil {
    public  <L, R> ResponseEntity<?> toResponseEntity(Either<L, R> result) {
        return result.isRight()
                ? ResponseEntity.ok(result.getRight())
                : ResponseEntity.badRequest().body(result.getLeft());
    }
}
