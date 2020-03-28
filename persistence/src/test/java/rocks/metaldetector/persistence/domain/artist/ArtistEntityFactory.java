package rocks.metaldetector.persistence.domain.artist;

class ArtistEntityFactory {

  static ArtistEntity createArtistEntity(long discogsId, String artistName, String thumb) {
    return new ArtistEntity(discogsId, artistName, thumb);
  }
}
