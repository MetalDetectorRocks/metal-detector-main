package rocks.metaldetector.support;

public class ExternalServiceException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public ExternalServiceException() {
    super(ErrorMessages.UNEXPECTED_EXTERNAL_SERVICE_BEHAVIOUR.toDisplayString());
  }

  public ExternalServiceException(String detailMessage) {
    super(ErrorMessages.UNEXPECTED_EXTERNAL_SERVICE_BEHAVIOUR.toDisplayString() + ": " + detailMessage);
  }
}
