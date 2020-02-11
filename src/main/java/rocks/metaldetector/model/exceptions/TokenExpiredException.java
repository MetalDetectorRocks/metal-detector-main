package rocks.metaldetector.model.exceptions;

public class TokenExpiredException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public TokenExpiredException() {
    super(ErrorMessages.TOKEN_EXPIRED.toDisplayString());
  }
}
