package rocks.metaldetector.model.exceptions;

public class ResourceNotFoundException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public ResourceNotFoundException(String detailedMessage) {
    super(ErrorMessages.RESOURCE_DOES_NOT_EXIST.toDisplayString() + " " + detailedMessage);
  }

}
