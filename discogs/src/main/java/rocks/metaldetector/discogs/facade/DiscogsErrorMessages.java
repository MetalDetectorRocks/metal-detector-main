package rocks.metaldetector.discogs.facade;

public enum DiscogsErrorMessages {

  ARTIST_NOT_FOUND("An artist with provided id or name could not be found on Discogs.");

  private final String errorMessage;

  DiscogsErrorMessages(String errorMessage) {
    this.errorMessage = errorMessage;
  }

  public String toDisplayString() {
    return errorMessage;
  }
}
