package io.hhplus.tdd;

import java.util.stream.Collectors;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import io.hhplus.tdd._core.exception.BadRequestException;

@RestControllerAdvice
class ApiControllerAdvice {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequestException(BadRequestException e) {
        return ResponseEntity.status(e.status.value()).body(
                new ErrorResponse(
                        String.valueOf(e.status.value()),
                        "잘못된 요청입니다."
                )
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                      .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                      .collect(Collectors.joining(","));
        return ResponseEntity.status(400).body(new ErrorResponse("400", msg));
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    ResponseEntity<ErrorResponse> handleParamValidation(HandlerMethodValidationException e) {
        String msg = e.getAllValidationResults().stream()
                      .flatMap(r -> r.getResolvableErrors().stream())
                      .map(MessageSourceResolvable::getDefaultMessage)
                      .collect(java.util.stream.Collectors.joining("; "));
        return ResponseEntity.status(400).body(new ErrorResponse("400", msg));
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        return ResponseEntity.status(500).body(new ErrorResponse("500", "에러가 발생했습니다."));
    }
}
