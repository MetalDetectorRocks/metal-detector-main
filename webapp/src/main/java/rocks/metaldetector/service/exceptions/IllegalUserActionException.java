package rocks.metaldetector.service.exceptions;

import static rocks.metaldetector.service.user.UserErrorMessages.ADMINISTRATOR_CANNOT_DISABLE_HIMSELF;
import static rocks.metaldetector.service.user.UserErrorMessages.ADMINISTRATOR_DISCARD_ROLE;
import static rocks.metaldetector.service.user.UserErrorMessages.OAUTH_USER_CANNOT_CHANGE_EMAIL;
import static rocks.metaldetector.service.user.UserErrorMessages.OAUTH_USER_CANNOT_CHANGE_PASSWORD;

public class IllegalUserActionException extends RuntimeException {

  private IllegalUserActionException(String message) {
    super(message);
  }

  public static IllegalUserActionException createAdminCannotDisableHimselfException() {
    return new IllegalUserActionException(ADMINISTRATOR_CANNOT_DISABLE_HIMSELF.toDisplayString());
  }

  public static IllegalUserActionException createAdminCannotDiscardHisRoleException() {
    return new IllegalUserActionException(ADMINISTRATOR_DISCARD_ROLE.toDisplayString());
  }

  public static IllegalUserActionException createOAuthUserCannotChangeEMailException() {
    return new IllegalUserActionException(OAUTH_USER_CANNOT_CHANGE_EMAIL.toDisplayString());
  }

  public static IllegalUserActionException createOAuthUserCannotChangePasswordException() {
    return new IllegalUserActionException(OAUTH_USER_CANNOT_CHANGE_PASSWORD.toDisplayString());
  }
}
