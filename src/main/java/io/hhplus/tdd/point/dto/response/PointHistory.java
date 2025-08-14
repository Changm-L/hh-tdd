package io.hhplus.tdd.point.dto.response;

import io.hhplus.tdd.point.constant.TransactionType;

public record PointHistory(
        long id,
        long userId,
        long amount,
        TransactionType type,
        long updateMillis
) {
}
