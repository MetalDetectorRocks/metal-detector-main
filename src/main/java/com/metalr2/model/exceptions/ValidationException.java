package com.metalr2.model.exceptions;

import org.springframework.validation.FieldError;

import java.util.ArrayList;
import java.util.List;

public class ValidationException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  private List<FieldError> fieldErrors = new ArrayList<>();

  private ValidationException(String message) {
    super(message);
  }

  public ValidationException(String message, List<FieldError> fieldErrors) {
    this(message);
    this.fieldErrors = fieldErrors;
  }

  List<FieldError> getFieldErrors() {
    return fieldErrors;
  }
}
