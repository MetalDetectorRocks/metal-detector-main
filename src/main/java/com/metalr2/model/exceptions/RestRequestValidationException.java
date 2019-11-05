package com.metalr2.model.exceptions;

import org.springframework.validation.FieldError;

import java.util.ArrayList;
import java.util.List;

public class RestRequestValidationException extends RuntimeException {

  private List<FieldError> fieldErrors = new ArrayList<>();

  private RestRequestValidationException(String message) {
    super(message);
  }

  public RestRequestValidationException(String message, List<FieldError> fieldErrors) {
    this(message);
    this.fieldErrors = fieldErrors;
  }

  public List<FieldError> getFieldErrors() {
    return fieldErrors;
  }
}
