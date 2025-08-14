package io.hhplus.tdd.point.dto.request;

import jakarta.validation.constraints.Positive;

public record UseRequest(
        @Positive long amount
) {
}
