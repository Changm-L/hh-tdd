package io.hhplus.tdd.point.exception;

public class InsufficientPointException extends PointException {
    public static final String MESSAGE = "포인트가 부족합니다.";

    public InsufficientPointException() {
        super(MESSAGE);
    }

    @Override
    public int getHttpStatus() {
        return 400;
    }
}
