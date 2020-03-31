package rocks.metaldetector.service.exceptions;

import rocks.metaldetector.service.user.UserErrorMessages;

public class IllegalUserActionException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  private IllegalUserActionException(String message) {
    super(message);
  }

  public static IllegalUserActionException createAdminCannotDisableHimselfException() {
    return new IllegalUserActionException(UserErrorMessages.ADMINISTRATOR_CANNOT_DISABLE_HIMSELF.toDisplayString());
  }

  public static IllegalUserActionException createAdminCannotDiscardHisRoleException() {
    return new IllegalUserActionException(UserErrorMessages.ADMINISTRATOR_DISCARD_ROLE.toDisplayString());
  }
}
