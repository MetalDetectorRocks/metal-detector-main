package rocks.metaldetector.persistence.domain.artist;

public enum ArtistSource {

  DISCOGS("Discogs"),
  SPOTIFY("Spotify");

  private final String value;

  ArtistSource(String value) {
    this.value = value;
  }

  public String getDisplayName() {
    return value;
  }

  public static ArtistSource getArtistSourceFromString(String source) {
    if (source.equalsIgnoreCase(DISCOGS.getDisplayName())) {
      return DISCOGS;
    }
    else if (source.equalsIgnoreCase(SPOTIFY.getDisplayName())) {
      return SPOTIFY;
    }
    throw new IllegalArgumentException("Source '" + source + "' not found!");
  }
}
