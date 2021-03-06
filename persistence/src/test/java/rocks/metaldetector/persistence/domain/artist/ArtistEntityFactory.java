package rocks.metaldetector.persistence.domain.artist;

class ArtistEntityFactory {

  static ArtistEntity createArtistEntity(String externalId, String artistName, ArtistSource source) {
    return ArtistEntity.builder()
            .externalId(externalId)
            .externalUrl("http://example.com/artist")
            .artistName(artistName)
            .genres("Back Metal, Post Black Metal")
            .source(source)
            .spotifyPopularity(66)
            .spotifyFollower(666)
            .imageXs("http://example.com/image-xs.jpg")
            .imageS("http://example.com/image-s.jpg")
            .imageM("http://example.com/image-m.jpg")
            .imageL("http://example.com/image-l.jpg")
            .build();
  }
}
