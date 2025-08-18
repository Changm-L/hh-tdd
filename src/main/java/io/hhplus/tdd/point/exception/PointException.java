package io.hhplus.tdd.point.exception;

public abstract class PointException extends IllegalArgumentException {
    public PointException(String message) {
        super(message);
    }

    public abstract int getHttpStatus();
}
