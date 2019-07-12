package com.metalr2.model.exceptions;

public enum ErrorMessages {

  VALIDATION_ERRORS("There were errors validating your request. Please refer to the documentation for a valid request."),
  User_ALREADY_EXISTS("This email has already been registered, please choose another one."),
  RESOURCE_DOES_NOT_EXIST("Resource does not exist."),
  USER_WITH_ID_NOT_FOUND("User with provided id not found."),
  USER_WITH_EMAIL_NOT_FOUND("User with provided email not found. Please check your email address."),
  ADDRESS_WITH_ID_NOT_FOUND("Address with provided id not found."),
  TOKEN_NOT_FOUND("Token not found."), // what to do?
  EMAIL_VERIFICATION_TOKEN_EXPIRED("Email verification token is expired."),
  PASSWORD_RESET_TOKEN_EXPIRED("Password reset token is expired. Please reset the password again.");

  private final String errorMessage;
	
  ErrorMessages(String errorMessage) {
    this.errorMessage = errorMessage;
  }

  public String toDisplayString() {
    return errorMessage;
  }

}
