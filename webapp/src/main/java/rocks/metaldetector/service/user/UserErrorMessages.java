package rocks.metaldetector.service.user;

public enum UserErrorMessages {

  USER_WITH_EMAIL_ALREADY_EXISTS("This email address has already been used, please choose another one."),
  USER_WITH_USERNAME_ALREADY_EXISTS("This username has already been used, please choose another one."),
  ADMINISTRATOR_CANNOT_DISABLE_HIMSELF("An administrator cannot deactivate himself."),
  ADMINISTRATOR_DISCARD_ROLE("An administrator cannot discard his role."),
  USER_WITH_ID_NOT_FOUND("User with provided id not found."),
  USER_NOT_FOUND("User with provided email or username not found."),
  TOKEN_EXPIRED("Token is expired."),
  OAUTH_USER_CANNOT_CHANGE_EMAIL("OAUth users cannot change their email address."),
  OAUTH_USER_CANNOT_CHANGE_PASSWORD("OAUth users cannot change their password.");

  private final String errorMessage;
	
  UserErrorMessages(String errorMessage) {
    this.errorMessage = errorMessage;
  }

  public String toDisplayString() {
    return errorMessage;
  }

}
