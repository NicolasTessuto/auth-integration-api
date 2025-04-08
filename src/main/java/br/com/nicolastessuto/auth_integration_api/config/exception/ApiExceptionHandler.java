package br.com.nicolastessuto.auth_integration_api.config.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class ApiExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiException> handleUnexpectedExceptions(Exception ex) {
        log.error("Internal server error:", ex);
        return ResponseEntity.badRequest().body(new ApiException("Oops! Something went wrong...", LocalDateTime.now()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiException> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(new ApiException(ex.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiException> handleNoResourceFoundException(NoResourceFoundException ex) {
        return ResponseEntity.badRequest().body(new ApiException(ex.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiException> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        return ResponseEntity.badRequest().body(new ApiException(ex.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<ApiException> handleDuplicateKeyException(DuplicateKeyException ex) {
        return ResponseEntity.badRequest().body(new ApiException(ex.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(HubspotIntegrationException.class)
    public ResponseEntity<ApiException> handleHubspotIntegrationException(HubspotIntegrationException ex) {
        return ResponseEntity.status(ex.getStatusCode()).body(new ApiException(ex.getLocalizedMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiException> handleMissingParams(MissingServletRequestParameterException ex) {
        String name = ex.getParameterName();
        String message = "The required parameter '" + name + "' is missing.";
        return ResponseEntity.badRequest().body(new ApiException(message, LocalDateTime.now()));
    }

}
