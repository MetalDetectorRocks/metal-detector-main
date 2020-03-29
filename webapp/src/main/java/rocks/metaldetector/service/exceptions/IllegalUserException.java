package rocks.metaldetector.service.exceptions;

import rocks.metaldetector.service.user.UserErrorMessages;

public class IllegalUserException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  private IllegalUserException(String message) {
    super(message);
  }

  public static IllegalUserException createAdminCannotDisableHimselfException() {
    return new IllegalUserException(UserErrorMessages.ADMINISTRATOR_CANNOT_DISABLE_HIMSELF.toDisplayString());
  }

  public static IllegalUserException createAdminCannotDiscardHisRoleException() {
    return new IllegalUserException(UserErrorMessages.ADMINISTRATOR_DISCARD_ROLE.toDisplayString());
  }
}
