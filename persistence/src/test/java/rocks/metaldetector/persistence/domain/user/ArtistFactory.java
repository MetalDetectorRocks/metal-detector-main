package rocks.metaldetector.persistence.domain.user;

import rocks.metaldetector.persistence.domain.artist.ArtistEntity;

import static rocks.metaldetector.persistence.domain.artist.ArtistSource.DISCOGS;

public class ArtistFactory {

  public static ArtistEntity withExternalId(String externalId) {
    return new ArtistEntity(externalId, externalId, "image url", DISCOGS);
  }
}
