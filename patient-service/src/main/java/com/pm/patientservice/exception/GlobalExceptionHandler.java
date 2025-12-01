package com.pm.patientservice.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,String>> handleValidationException
            (MethodArgumentNotValidException ex){

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(
                error ->  errors.put(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(EmailAlreadyExists.class)
    public ResponseEntity<Map<String,String>> handleEmailAlreadyExistsException(EmailAlreadyExists ex) {
        log.warn("Email already exists");
        Map<String, String> error = new HashMap<>();
        error.put("message", "Email Alredy Exists");
        return ResponseEntity.badRequest().body(error);

    }

    @ExceptionHandler(PatientNotFountException.class)
    public ResponseEntity<Map<String,String>> handlePatientNotFoundException(PatientNotFountException ex) {
        log.warn("Patient not found");
        Map<String, String> error = new HashMap<>();
        error.put("message", "Patient Not Found");
        return ResponseEntity.badRequest().body(error);
    }
}
