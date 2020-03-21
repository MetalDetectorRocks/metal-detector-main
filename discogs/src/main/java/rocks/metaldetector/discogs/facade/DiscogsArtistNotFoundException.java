package rocks.metaldetector.discogs.facade;

import rocks.metaldetector.support.ResourceNotFoundException;

public class DiscogsArtistNotFoundException extends ResourceNotFoundException {

  public DiscogsArtistNotFoundException() {
    super(DiscogsErrorMessages.ARTIST_NOT_FOUND.toDisplayString());
  }
}
