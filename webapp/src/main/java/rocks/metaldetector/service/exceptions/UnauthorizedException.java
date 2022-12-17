package rocks.metaldetector.service.exceptions;

public class UnauthorizedException extends RuntimeException {

  public UnauthorizedException() {
    super("Neither a valid access token nor a valid refresh token are present.");
  }
}
