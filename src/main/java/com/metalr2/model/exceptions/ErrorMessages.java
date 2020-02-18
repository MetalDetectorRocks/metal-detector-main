package com.metalr2.model.exceptions;

public enum ErrorMessages {

  USER_WITH_EMAIL_ALREADY_EXISTS("This email address has already been used, please choose another one."),
  USER_WITH_USERNAME_ALREADY_EXISTS("This username has already been used, please choose another one."),
  ADMINISTRATOR_CANNOT_DISABLE_HIMSELF("An administrator cannot deactivate himself."),
  ADMINISTRATOR_DISCARD_ROLE("An administrator cannot discard his role."),
  RESOURCE_DOES_NOT_EXIST("Resource does not exist."),
  USER_WITH_ID_NOT_FOUND("User with provided id not found."),
  USER_NOT_FOUND("User with provided email or username not found."),
  TOKEN_NOT_FOUND("Token not found."),
  TOKEN_EXPIRED("Token is expired."),
  VALIDATION_ERROR("There were errors validating your request. Please refer to the documentation for a valid request.");

  private final String errorMessage;
	
  ErrorMessages(String errorMessage) {
    this.errorMessage = errorMessage;
  }

  public String toDisplayString() {
    return errorMessage;
  }

}
