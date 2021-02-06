package rocks.metaldetector.service.artist;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import rocks.metaldetector.testutil.DtoFactory;

import static rocks.metaldetector.persistence.domain.artist.ArtistSource.SPOTIFY;

class ArtistDtoTransformerTest implements WithAssertions {

  private final ArtistDtoTransformer underTest = new ArtistDtoTransformer();

  @Test
  @DisplayName("SpotifyArtistDto is transformed to ArtistDto")
  void test_spotify_dto_is_transformed() {
    // given
    var spotifyArtistDto = DtoFactory.SpotifyArtistDtoFactory.withArtistName("artist");

    // when
    var result = underTest.transformSpotifyArtistDto(spotifyArtistDto);

    // then
    assertThat(result.getExternalId()).isEqualTo(spotifyArtistDto.getId());
    assertThat(result.getArtistName()).isEqualTo(spotifyArtistDto.getName());
    assertThat(result.getFollower()).isEqualTo(spotifyArtistDto.getFollower());
    assertThat(result.getSource()).isEqualTo(SPOTIFY.getDisplayName());
//    assertThat(result.getThumb()).isEqualTo(spotifyArtistDto.getImageUrl()); ToDo DanielW: Adjust Test
    assertThat(result.getFollowedSince()).isNull();
  }
}