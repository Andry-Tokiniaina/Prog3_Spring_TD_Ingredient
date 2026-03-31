package com.example.restservice.validator;

import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RequestBodyValidator {
    public void requestBodyValidator(List<Integer> ingIds) throws BadRequestException {
        if (ingIds.isEmpty() || ingIds == null) {
            throw new BadRequestException("The request body (list of IDs) is mandatory");
        }
    }
}
