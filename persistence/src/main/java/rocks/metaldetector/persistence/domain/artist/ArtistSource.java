package rocks.metaldetector.persistence.domain.artist;

public enum ArtistSource {

  DISCOGS("Discogs"),
  SPOTIFY("Spotify");

  private final String value;

  ArtistSource(String value) {
    this.value = value;
  }

  public String toDisplayString() {
    return value;
  }
}
