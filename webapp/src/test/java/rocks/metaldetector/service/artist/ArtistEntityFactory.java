package rocks.metaldetector.service.artist;

import rocks.metaldetector.persistence.domain.artist.ArtistEntity;

import static rocks.metaldetector.persistence.domain.artist.ArtistSource.DISCOGS;

public class ArtistEntityFactory {

  public static ArtistEntity withExternalId(String externalId) {
    return new ArtistEntity(externalId, externalId, "image url", DISCOGS);
  }
}
