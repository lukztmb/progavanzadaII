package infrastructure.controller;

import infrastructure.exception.BusinessRuleViolationsException;
import infrastructure.exception.DuplicateResourceException;
import infrastructure.exception.ResourceNotFoundException;
import jakarta.validation.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionsHandler {
    // README: Validation errors -> 400 Bad Request
    // Se activa por el @Valid en el DTO del controlador
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.toList());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST) // 400
                .body(Map.of("error", "Errores de validación", "details", errors));
    }

    // Para la excepción de validación de TaskComment.create()
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Map<String, String>> handleJakartaValidation(ValidationException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST) // 400
                .body(Map.of("error", ex.getMessage()));
    }

    // README: Duplicate resource (e.g., project name) -> 409 Conflict
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<Map<String, String>> handleDuplicateResource(DuplicateResourceException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT) // 409
                .body(Map.of("error", ex.getMessage()));
    }

    // README: Business rule violation (...) -> 409 Conflict
    @ExceptionHandler(BusinessRuleViolationsException.class)
    public ResponseEntity<Map<String, String>> handleBusinessRuleViolation(BusinessRuleViolationsException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT) // 409
                .body(Map.of("error", ex.getMessage()));
    }

    // README: Not found -> 404 Not Found
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleResourceNotFound(ResourceNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND) // 404
                .body(Map.of("error", ex.getMessage()));
    }
}
