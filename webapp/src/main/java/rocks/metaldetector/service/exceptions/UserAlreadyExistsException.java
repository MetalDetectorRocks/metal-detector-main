package rocks.metaldetector.service.exceptions;

import lombok.Getter;
import rocks.metaldetector.service.user.UserErrorMessages;

public class UserAlreadyExistsException extends RuntimeException {

  @Getter
  private final Reason reason;

  private UserAlreadyExistsException(String message, Reason reason) {
    super(message);
    this.reason = reason;
  }

  public static UserAlreadyExistsException createUserWithUsernameAlreadyExistsException() {
    return new UserAlreadyExistsException(UserErrorMessages.USER_WITH_USERNAME_ALREADY_EXISTS.toDisplayString(), Reason.USERNAME_ALREADY_EXISTS);
  }

  public static UserAlreadyExistsException createUserWithEmailAlreadyExistsException() {
    return new UserAlreadyExistsException(UserErrorMessages.USER_WITH_EMAIL_ALREADY_EXISTS.toDisplayString(), Reason.EMAIL_ALREADY_EXISTS);
  }

  public enum Reason {
    USERNAME_ALREADY_EXISTS,
    EMAIL_ALREADY_EXISTS
  }

}
