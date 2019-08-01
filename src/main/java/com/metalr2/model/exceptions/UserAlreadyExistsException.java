package com.metalr2.model.exceptions;

import lombok.Getter;

public class UserAlreadyExistsException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  @Getter
  private final Reason reason;

  private UserAlreadyExistsException(String message, Reason reason) {
    super(message);
    this.reason = reason;
  }

  public static UserAlreadyExistsException createUserWithUsernameAlreadyExistsException() {
    return new UserAlreadyExistsException(ErrorMessages.USER_WITH_USERNAME_ALREADY_EXISTS.toDisplayString(), Reason.USERNAME_ALREADY_EXISTS);
  }

  public static UserAlreadyExistsException createUserWithEmailAlreadyExistsException() {
    return new UserAlreadyExistsException(ErrorMessages.USER_WITH_EMAIL_ALREADY_EXISTS.toDisplayString(), Reason.EMAIL_ALREADY_EXISTS);
  }

  public enum Reason {
    USERNAME_ALREADY_EXISTS,
    EMAIL_ALREADY_EXISTS
  }

}
