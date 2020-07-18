package rocks.metaldetector.persistence.domain.artist;

import static rocks.metaldetector.persistence.domain.artist.ArtistSource.DISCOGS;

class ArtistEntityFactory {

  static ArtistEntity createArtistEntity(String externalId, String artistName) {
    return new ArtistEntity(externalId, artistName, null, DISCOGS);
  }
}
