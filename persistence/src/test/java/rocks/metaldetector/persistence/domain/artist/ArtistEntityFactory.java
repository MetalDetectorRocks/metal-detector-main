package rocks.metaldetector.persistence.domain.artist;

class ArtistEntityFactory {

  static ArtistEntity createArtistEntity(String externalId, String artistName, ArtistSource source) {
    return new ArtistEntity(externalId, artistName, null, source);
  }
}
