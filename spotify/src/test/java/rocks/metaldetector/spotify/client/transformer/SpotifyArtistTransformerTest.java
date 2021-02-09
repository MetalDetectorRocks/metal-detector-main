package rocks.metaldetector.spotify.client.transformer;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.metaldetector.spotify.api.SpotifyArtist;
import rocks.metaldetector.spotify.api.SpotifyImage;
import rocks.metaldetector.spotify.facade.dto.SpotifyArtistDto;
import rocks.metaldetector.support.ImageSize;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static rocks.metaldetector.spotify.client.SpotifyDtoFactory.SpotfiyArtistFactory;
import static rocks.metaldetector.spotify.client.transformer.SpotifyArtistTransformer.SPOTIFY_URL_KEY_NAME;
import static rocks.metaldetector.support.ImageSize.L;
import static rocks.metaldetector.support.ImageSize.XS;

@ExtendWith(MockitoExtension.class)
class SpotifyArtistTransformerTest implements WithAssertions {

  private final SpotifyArtistTransformer underTest = new SpotifyArtistTransformer();

  @Test
  @DisplayName("Should transform SpotifyArtist to SpotifyArtistDto")
  void should_transform() {
    // given
    SpotifyArtist givenArtist = SpotfiyArtistFactory.withArtistName("Slayer");

    // when
    SpotifyArtistDto result = underTest.transform(givenArtist);

    // then
    assertThat(result).isEqualTo(
              SpotifyArtistDto.builder()
                      .id(givenArtist.getId())
                      .name(givenArtist.getName())
                      .uri(givenArtist.getUri())
                      .url(givenArtist.getExternalUrls().get(SPOTIFY_URL_KEY_NAME))
                      .genres(givenArtist.getGenres())
                      .popularity(givenArtist.getPopularity())
                      .follower(givenArtist.getFollowers().getTotal())
                      .build()
    );
  }

  @ParameterizedTest(name = "should transform {0} to {1}")
  @MethodSource("imageProvider")
  @DisplayName("should transform images")
  void should_transform_images(List<SpotifyImage> images, Map<ImageSize, String> expectedImages) {
    // given
    SpotifyArtist givenArtist = SpotfiyArtistFactory.withArtistName("Slayer");
    givenArtist.setImages(images);

    // when
    SpotifyArtistDto result = underTest.transform(givenArtist);

    // then
    assertThat(result.getImages()).isEqualTo(expectedImages);
  }

  @Test
  @DisplayName("should fully capitalize the genres")
  void should_fully_capitalize_the_genres() {
    // given
    SpotifyArtist givenArtist = SpotfiyArtistFactory.withArtistName("Slayer");
    givenArtist.setGenres(List.of("black metal", "atmospheric black metal"));

    // when
    SpotifyArtistDto result = underTest.transform(givenArtist);

    // then
    assertThat(result.getGenres()).containsExactly("Black Metal", "Atmospheric Black Metal");
  }

  @Test
  @DisplayName("should return empty list if genres are nul")
  void should_return_empty_list_if_genres_are_nul() {
    // given
    SpotifyArtist givenArtist = SpotfiyArtistFactory.withArtistName("Slayer");
    givenArtist.setGenres(null);

    // when
    SpotifyArtistDto result = underTest.transform(givenArtist);

    // then
    assertThat(result.getGenres()).isEmpty();
  }

  @Test
  @DisplayName("followers are 0 if no follower object is provided")
  void test_transform_null_follower() {
    // given
    SpotifyArtist givenArtist = SpotfiyArtistFactory.withArtistName("Slayer");
    givenArtist.setFollowers(null);

    // when
    var result = underTest.transform(givenArtist);

    // then
    assertThat(result.getFollower()).isEqualTo(0);
  }

  private static Stream<Arguments> imageProvider() {
    SpotifyImage spotifyImage1 = new SpotifyImage("http://example.com/image-l.jpg", 600, 600);
    SpotifyImage spotifyImage2 = new SpotifyImage("http://example.com/image-xs-1.jpg", 100, 100);
    SpotifyImage spotifyImage3 = new SpotifyImage("http://example.com/image-xs-2.jpg", 64, 64);
    return Stream.of(
            Arguments.of(null, Collections.emptyMap()),
            Arguments.of(Collections.emptyList(), Collections.emptyMap()),
            Arguments.of(List.of(spotifyImage1), Map.of(L, spotifyImage1.getUrl())),
            Arguments.of(List.of(spotifyImage1, spotifyImage2), Map.of(L, spotifyImage1.getUrl(), XS, spotifyImage2.getUrl())),
            Arguments.of(List.of(spotifyImage1, spotifyImage2, spotifyImage3), Map.of(L, spotifyImage1.getUrl(), XS, spotifyImage2.getUrl()))
    );
  }
}
