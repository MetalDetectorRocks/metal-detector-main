package rocks.metaldetector.service.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.core.OAuth2AuthorizationException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.RestClientException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;
import rocks.metaldetector.config.constants.ViewNames;
import rocks.metaldetector.support.exceptions.ExternalServiceException;
import rocks.metaldetector.support.exceptions.ResourceNotFoundException;
import rocks.metaldetector.web.api.response.ErrorResponse;

import javax.validation.ValidationException;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.METHOD_NOT_ALLOWED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.http.HttpStatus.UNSUPPORTED_MEDIA_TYPE;

@ControllerAdvice
@Slf4j
public class RestExceptionsHandler {

  @ExceptionHandler({IllegalArgumentException.class, MissingServletRequestParameterException.class})
  Object handleIllegalOrMissingArgumentErrors(Exception exception, WebRequest webRequest) {
    log.error(webRequest.getContextPath() + ": " + exception.getMessage());
    String requestUri = ((ServletWebRequest) webRequest).getRequest().getRequestURI();
    if (requestUri.startsWith("/rest/")) {
      return new ResponseEntity<>(createErrorResponse(exception), new HttpHeaders(), BAD_REQUEST);
    }
    else {
      return new ModelAndView(ViewNames.Error.ERROR_400);
    }
  }

  @ExceptionHandler({HttpMessageNotReadableException.class})
  ResponseEntity<ErrorResponse> handleMissingRequestBody(Exception exception, WebRequest webRequest) {
    var message = "Request body is missing";
    log.warn(webRequest.getContextPath() + ": " + message);
    return new ResponseEntity<>(createErrorResponse(exception), new HttpHeaders(), BAD_REQUEST);
  }

  @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
  ResponseEntity<ErrorResponse> handleHttpMethodNotSupported(Exception exception, WebRequest webRequest) {
    log.warn(webRequest.getContextPath() + ": " + exception.getMessage());
    return new ResponseEntity<>(createErrorResponse(exception), new HttpHeaders(), METHOD_NOT_ALLOWED);
  }

  @ExceptionHandler({HttpMediaTypeNotSupportedException.class})
  ResponseEntity<ErrorResponse>  handleMediaTypeNotSupported(Exception exception, WebRequest webRequest) {
    log.warn(webRequest.getContextPath() + ": " + exception.getMessage());
    return new ResponseEntity<>(createErrorResponse(exception), new HttpHeaders(), UNSUPPORTED_MEDIA_TYPE);
  }

  @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
  public ResponseEntity<ErrorResponse> handleValidationErrors(Exception exception, WebRequest webRequest) {
    log.warn(webRequest.getContextPath() + ": " + exception.getMessage());
    BindingResult bindingResult = null;
    if (exception instanceof MethodArgumentNotValidException) {
      bindingResult = ((MethodArgumentNotValidException) exception).getBindingResult();
    }
    else if (exception instanceof BindException) {
      bindingResult = ((BindException) exception).getBindingResult();
    }

    return bindingResult != null
            ? new ResponseEntity<>(createErrorResponse(bindingResult), new HttpHeaders(), BAD_REQUEST)
            : handleAllOtherExceptions(exception, webRequest);
  }

  @ExceptionHandler({UserAlreadyExistsException.class, IllegalUserActionException.class})
  public ResponseEntity<ErrorResponse> handleUserException(RuntimeException exception, WebRequest webRequest) {
    log.warn(webRequest.getContextPath() + ": " + exception.getMessage());
    return new ResponseEntity<>(createErrorResponse(exception), new HttpHeaders(), CONFLICT);
  }

  @ExceptionHandler({ResourceNotFoundException.class})
  public ResponseEntity<ErrorResponse> handleNotFoundException(RuntimeException exception, WebRequest webRequest) {
    log.warn(webRequest.getContextPath() + ": " + exception.getMessage());
    return new ResponseEntity<>(createErrorResponse(exception), new HttpHeaders(), NOT_FOUND);
  }

  @ExceptionHandler({AccessDeniedException.class, OAuth2AuthorizationException.class})
  public ResponseEntity<ErrorResponse> handleAccessDeniedException(RuntimeException exception, WebRequest webRequest) {
    log.warn(webRequest.getContextPath() + ": " + exception.getMessage());
    return new ResponseEntity<>(createErrorResponse(exception), new HttpHeaders(), FORBIDDEN);
  }

  @ExceptionHandler({ValidationException.class})
  ResponseEntity<ErrorResponse> handleValidationException(Exception exception, WebRequest webRequest) {
    log.warn(webRequest.getContextPath() + ": " + exception.getMessage());
    return new ResponseEntity<>(createErrorResponse(exception), new HttpHeaders(), UNPROCESSABLE_ENTITY);
  }

  @ExceptionHandler({ExternalServiceException.class, RestClientException.class})
  public ResponseEntity<ErrorResponse> handleRestExceptions(RuntimeException exception, WebRequest webRequest) {
    log.error(webRequest.getContextPath() + ": " + exception.getMessage());
    return new ResponseEntity<>(createErrorResponse(exception), new HttpHeaders(), HttpStatus.SERVICE_UNAVAILABLE);
  }

  @ExceptionHandler({Exception.class})
  public ResponseEntity<ErrorResponse> handleAllOtherExceptions(Exception exception, WebRequest webRequest) {
    log.error(webRequest.getContextPath() + ": " + exception.getMessage(), exception);
    return new ResponseEntity<>(createErrorResponse(exception), new HttpHeaders(), INTERNAL_SERVER_ERROR);
  }

  private ErrorResponse createErrorResponse(Throwable exception) {
    return new ErrorResponse(exception.getMessage());
  }

  private ErrorResponse createErrorResponse(BindingResult bindingResult) {
    List<String> fieldErrors = bindingResult.getAllErrors()
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
