package rocks.metaldetector.service.artist;

import rocks.metaldetector.persistence.domain.artist.ArtistEntity;

import static rocks.metaldetector.persistence.domain.artist.ArtistSource.SPOTIFY;

public class ArtistEntityFactory {

  public static ArtistEntity withExternalId(String externalId) {
    return ArtistEntity.builder()
            .externalId(externalId)
            .externalUrl("http://example.com/666")
            .artistName("")
            .genres("Black Metal, Post Black Metal")
            .source(SPOTIFY)
            .imageM("http://example.com/image.jpg")
            .build();
  }
}
