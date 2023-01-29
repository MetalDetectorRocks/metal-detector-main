package rocks.metaldetector.support;

public enum ErrorMessages {

  UNEXPECTED_SERVICE_BEHAVIOUR("An internal error occurred. Please try again later."),
  UNEXPECTED_EXTERNAL_SERVICE_BEHAVIOUR("An error occurred during calling an external service. Please try again later.");

  private final String errorMessage;

  ErrorMessages(String errorMessage) {
    this.errorMessage = errorMessage;
  }

  public String toDisplayString() {
    return errorMessage;
  }

}
