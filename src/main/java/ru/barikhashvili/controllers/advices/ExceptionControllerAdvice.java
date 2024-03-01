package ru.barikhashvili.controllers.advices;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.barikhashvili.exceptions.InsufficientDataException;
import ru.barikhashvili.exceptions.ResourceNotFoundException;

import java.util.Map;

@RestControllerAdvice
@Slf4j
public class ExceptionControllerAdvice {
    @ExceptionHandler(value = ResourceNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleInvalidAddressExceptions(ResourceNotFoundException exception) {
        log.warn("The resource for the requested id was not found. {}", exception.getMessage());
        return new ResponseEntity<>(generateErrorDetails("Not found"), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintViolationException(ConstraintViolationException exception) {
        var message = exception.getMessage();
        if (message != null && message.contains("duplicate key")) {
            log.warn("The data sent to the request is not unique and is already contained in the database.{}", exception.getMessage());
            return ResponseEntity.unprocessableEntity()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(generateErrorDetails("Non-unique values found in fields"));
        } else {
            log.warn("The data sent in the request body is not valid because it violates the restrictions specified in the database. {}", exception.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(generateErrorDetails("Request contains incorrect values"));
        }
    }

    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleNullPointerException(HttpMessageNotReadableException exception) {
        if (exception.getMessage().contains("enum")) {
            log.warn("Enum аргумент в теле запроса принимает неверное значение или его числовое представление не существует. {}", exception.getMessage());
            return ResponseEntity.badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(generateErrorDetails("Request body contains incorrect type value"));
        } else {
            log.warn("The request body was not sent, data processing is impossible because the data is missing. {}", exception.getMessage());
            return ResponseEntity.badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(generateErrorDetails("Request body is missing"));
        }
    }

    @ExceptionHandler(value = InsufficientDataException.class)
    public ResponseEntity<Map<String, String>> handleInvalidAddressExceptions(InsufficientDataException exception) {
        log.warn("Some of the required data that should be present in the request is missing. {}", exception.getMessage());
        return ResponseEntity.badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .body(generateErrorDetails("Insufficient data"));
    }

    private Map<String, String> generateErrorDetails(String message) {
        return Map.of("error", message);
    }
}
