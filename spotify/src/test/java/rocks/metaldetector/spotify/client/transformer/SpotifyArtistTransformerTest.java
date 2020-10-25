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

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static rocks.metaldetector.spotify.client.SpotifyDtoFactory.SpotfiyArtistFactory;

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
                      .imageUrl(givenArtist.getImages().get(0).getUrl())
                      .uri(givenArtist.getUri())
                      .genres(givenArtist.getGenres())
                      .popularity(givenArtist.getPopularity())
                      .follower(givenArtist.getFollowers().getTotal())
                      .build()
    );
  }

  @ParameterizedTest
  @MethodSource("imageProvider")
  @DisplayName("'imageUrl' is empty if there are no images")
  void should_transform_empty_images(List<SpotifyImage> images) {
    // given
    SpotifyArtist givenArtist = SpotfiyArtistFactory.withArtistName("Slayer");
    givenArtist.setImages(images);

    // when
    SpotifyArtistDto result = underTest.transform(givenArtist);

    // then
    assertThat(result.getImageUrl()).isEmpty();
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
    return Stream.of(
            Arguments.of((List<SpotifyImage>) null),
            Arguments.of(Collections.emptyList())
    );
  }
}
