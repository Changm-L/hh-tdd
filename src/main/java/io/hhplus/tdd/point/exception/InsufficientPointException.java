package io.hhplus.tdd.point.exception;

import io.hhplus.tdd._core.exception.BadRequestException;

public class InsufficientPointException extends BadRequestException {
    public static final String MESSAGE = "포인트가 부족합니다.";

    public InsufficientPointException() {
        super(MESSAGE);
    }
}
