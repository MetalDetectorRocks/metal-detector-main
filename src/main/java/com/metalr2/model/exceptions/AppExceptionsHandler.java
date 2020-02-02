package com.metalr2.model.exceptions;

import com.metalr2.web.dto.response.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.List;
import java.util.stream.Collectors;

/**
 * This class handles all Exception that can occur while using the REST API of this application.
 */
@ControllerAdvice
public class AppExceptionsHandler {

  private static final Logger LOG = LoggerFactory.getLogger(AppExceptionsHandler.class);

  @ExceptionHandler(value = MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleBadRequests(MethodArgumentNotValidException exception, WebRequest webRequest) {
    LOG.warn(webRequest.getContextPath() + ": " + exception.getMessage());
    return new ResponseEntity<>(createErrorResponse(exception), new HttpHeaders(), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(value = UserAlreadyExistsException.class)
  public ResponseEntity<ErrorResponse> handleAlreadyExistsException(UserAlreadyExistsException exception, WebRequest webRequest) {
    LOG.warn(webRequest.getContextPath() + ": " + exception.getMessage());
    return new ResponseEntity<>(createErrorResponse(exception), new HttpHeaders(), HttpStatus.CONFLICT);
  }

  @ExceptionHandler(value = {ResourceNotFoundException.class})
  public ResponseEntity<ErrorResponse> handleNotFoundException(RuntimeException exception, WebRequest webRequest) {
    LOG.warn(webRequest.getContextPath() + ": " + exception.getMessage());
    return new ResponseEntity<>(createErrorResponse(exception), new HttpHeaders(), HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(value = {Exception.class})
  public ResponseEntity<ErrorResponse> handleAllOtherExceptions(Exception exception, WebRequest webRequest) {
    LOG.error(webRequest.getContextPath() + ": " + exception.getMessage(), exception);
    return new ResponseEntity<>(createErrorResponse(exception), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
  }

  private ErrorResponse createErrorResponse(Throwable exception) {
    return new ErrorResponse(exception.getMessage());
  }

  private ErrorResponse createErrorResponse(MethodArgumentNotValidException exception) {
    List<String> fieldErrors = exception.getBindingResult().getAllErrors()
        .stream()
        .map(this::transformFieldError)
        .sorted()
        .collect(Collectors.toList());

    return new ErrorResponse(fieldErrors);
  }

  private String transformFieldError(ObjectError error) {
    if (error instanceof FieldError) {
      return "Error regarding field '" +
             ((FieldError) error).getField() +
             "': " +
             error.getDefaultMessage() +
             ".";
    }
    else {
      return error.getDefaultMessage();
    }

  }
	
}
