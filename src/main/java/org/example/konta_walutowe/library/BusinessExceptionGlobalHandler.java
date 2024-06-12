package org.example.konta_walutowe.library;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class BusinessExceptionGlobalHandler {

    @ExceptionHandler(value = { BusinessException.class })
    public ResponseEntity<BusinessErrorResponse> handleException(BusinessException ex) {
        BusinessErrorResponse businessErrorResponse = new BusinessErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), System.currentTimeMillis());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(businessErrorResponse);
    }
}
