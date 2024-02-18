package tech.andrefsramos.msAuth.exceptions.handler;

import java.nio.file.AccessDeniedException;
import java.util.Date;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import tech.andrefsramos.msAuth.exceptions.*;
import tech.andrefsramos.msAuth.exceptions.response.ExceptionResponse;

@ControllerAdvice
@RestController
public class CustomizedResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleValidationException(Exception ex, HttpServletRequest request) {
        return new ResponseEntity<>(new ExceptionResponse(
                new Date(),
                HttpStatus.INTERNAL_SERVER_ERROR,
                500,
                ex.getMessage(),
                request.getRequestURI()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<Object> handleValidationMethodArgument(MethodArgumentNotValidException ex, HttpServletRequest request) {
        return new ResponseEntity<>(new ExceptionResponse(
                new Date(),
                HttpStatus.BAD_REQUEST,
                400,
                ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage(),
                request.getRequestURI()),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(InvalidJwtAuthenticationException.class)
    public final ResponseEntity<ExceptionResponse> handleInvalidJwtAuthenticationExceptions(InvalidJwtAuthenticationException ex, HttpServletRequest request) {
        return new ResponseEntity<>(new ExceptionResponse(
                new Date(),
                HttpStatus.UNAUTHORIZED,
                401,
                ex.getMessage(),
                request.getRequestURI()), HttpStatus.UNAUTHORIZED
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ExceptionResponse> handleAccessDeniedException(AccessDeniedException ex, HttpServletRequest request) {
        return new ResponseEntity<>(new ExceptionResponse(
                new Date(),
                HttpStatus.FORBIDDEN,
                403,
                ex.getMessage(),
                request.getRequestURI()), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(AccessExcption.class)
    public ResponseEntity<Object> handleAccessDenied(AccessExcption ex, HttpServletRequest request) {
        return new ResponseEntity<>(new ExceptionResponse(
                new Date(),
                HttpStatus.FORBIDDEN,
                403,
                ex.getMessage(),
                request.getRequestURI()), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleValidationNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        return new ResponseEntity<>(new ExceptionResponse(
                new Date(),
                HttpStatus.NOT_FOUND,
                404,
                ex.getMessage(),
                request.getRequestURI()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ConstrainException.class)
    public final ResponseEntity<ExceptionResponse> handleConstrainException(ConstrainException ex, HttpServletRequest request) {
        return new ResponseEntity<>(new ExceptionResponse(
                new Date(),
                HttpStatus.CONFLICT,
                409,
                ex.getMessage(),
                request.getRequestURI()), HttpStatus.CONFLICT
        );
    }
}
