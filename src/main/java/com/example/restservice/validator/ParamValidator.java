package com.example.restservice.validator;

import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class ParamValidator {
    public void paramValidator (String at, String unit) throws BadRequestException {
        StringBuilder msg = new StringBuilder();
        int i = 0;
        if (at == null) {
            msg.append("Query parameter 'at' is required\n");
            i += 1;
        }
        if (unit == null) {
            msg.append("Query parameter 'unit' is required\n");
            i += 1;
        }
        if (!msg.isEmpty()) {
            if (i == 2) {
            throw new BadRequestException("Either mandatory query parameter `at` or `unit` is not provided.");
        }
            else {
                throw new BadRequestException(msg.toString());
            }
        }
    }
    public static void validateDateRange(Instant from, Instant to) throws BadRequestException {
        if (from == null || to == null) {
            throw new BadRequestException("Either mandatory query parameter `from` or `to` is not provided.");
        }
        if (from.isAfter(to)) {
            throw new BadRequestException("Parameter `from` must be before `to`.");
        }
    }
    public static void validateIdParam(Integer id) throws BadRequestException {
        if (id == null){
            throw new BadRequestException("Parameter `id` is required.");
        }
        if (id <= 0) {
            throw new BadRequestException("Parameter `id` must be greater than 0.");
        }
    }
}
