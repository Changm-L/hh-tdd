package io.hhplus.tdd.point.dto.request;

import jakarta.validation.constraints.Positive;

public record ChargeRequest(
        @Positive long amount
) {
}
