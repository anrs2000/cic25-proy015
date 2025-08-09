package es.cic.curso25.proy015.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(maxVehiculosException.class)
    public ResponseEntity<String> maxVehiculosHandler(maxVehiculosException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Error " + e.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> NotFoundHandler(NotFoundException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body("Error " + e.getMessage());
    }
}
