package rocks.metaldetector.service.artist;

import rocks.metaldetector.persistence.domain.artist.ArtistEntity;

class ArtistFactory {

  static ArtistEntity withDiscogsId(long discogsId) {
    return new ArtistEntity(discogsId, Long.toString(discogsId), "image url");
  }
}
