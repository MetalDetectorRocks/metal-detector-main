package com.metalr2.model.exceptions;

public class UserAlreadyExistsException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public UserAlreadyExistsException() {
    super(ErrorMessages.User_ALREADY_EXISTS.toDisplayString());
  }

}
