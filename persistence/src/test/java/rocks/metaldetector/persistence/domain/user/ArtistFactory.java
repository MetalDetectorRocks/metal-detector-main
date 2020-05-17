package rocks.metaldetector.persistence.domain.user;

import rocks.metaldetector.persistence.domain.artist.ArtistEntity;

public class ArtistFactory {

  public static ArtistEntity withDiscogsId(long discogsId) {
    return new ArtistEntity(discogsId, Long.toString(discogsId), "image url");
  }
}
