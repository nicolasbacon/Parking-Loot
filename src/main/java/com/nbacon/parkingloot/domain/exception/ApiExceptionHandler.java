package com.nbacon.parkingloot.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(VehicleTypeNotFoundException.class)
    public ResponseEntity<String> handleVehicleTypeNotFound(VehicleTypeNotFoundException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(ParkingNotFoundException.class)
    public ResponseEntity<String> handleParkingNotFound(ParkingNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(NoAvailableSpotException.class)
    public ResponseEntity<String> handleNoAvailableSpot(NoAvailableSpotException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, Object> errors = new HashMap<>();

        errors.put("status", HttpStatus.BAD_REQUEST.value());
        errors.put("error", "Validation failed");

        List<Map<String, String>> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> {
                    assert fieldError.getDefaultMessage() != null;
                    return Map.of(
                            "field", fieldError.getField(),
                            "message", fieldError.getDefaultMessage()
                    );
                })
                .toList();

        errors.put("fieldErrors", fieldErrors);

        return ResponseEntity.badRequest().body(errors);
    }

}

