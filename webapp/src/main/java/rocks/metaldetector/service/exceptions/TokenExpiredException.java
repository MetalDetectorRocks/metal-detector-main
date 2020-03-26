package rocks.metaldetector.service.exceptions;

import rocks.metaldetector.service.user.UserErrorMessages;

public class TokenExpiredException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public TokenExpiredException() {
    super(UserErrorMessages.TOKEN_EXPIRED.toDisplayString());
  }
}
