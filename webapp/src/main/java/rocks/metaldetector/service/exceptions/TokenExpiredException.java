package rocks.metaldetector.service.exceptions;

import static rocks.metaldetector.service.user.UserErrorMessages.TOKEN_EXPIRED;

public class TokenExpiredException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public TokenExpiredException() {
    super(TOKEN_EXPIRED.toDisplayString());
  }
}
