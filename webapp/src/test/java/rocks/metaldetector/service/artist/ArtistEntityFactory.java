package rocks.metaldetector.service.artist;

import rocks.metaldetector.persistence.domain.artist.ArtistEntity;

public class ArtistEntityFactory {

  public static ArtistEntity withDiscogsId(long discogsId) {
    return new ArtistEntity(discogsId, Long.toString(discogsId), "image url");
  }
}
