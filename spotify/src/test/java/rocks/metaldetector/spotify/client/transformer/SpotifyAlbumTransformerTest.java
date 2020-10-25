package rocks.metaldetector.spotify.client.transformer;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.metaldetector.spotify.api.SpotifyImage;
import rocks.metaldetector.spotify.client.SpotifyDtoFactory.SpotfiyArtistFactory;
import rocks.metaldetector.spotify.client.SpotifyDtoFactory.SpotifyAlbumFactory;
import rocks.metaldetector.spotify.client.SpotifyDtoFactory.SpotifyArtistDtoFactory;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SpotifyAlbumTransformerTest implements WithAssertions {

  @Mock
  private SpotifyArtistTransformer artistTransformer;

  @InjectMocks
  private SpotifyAlbumTransformer underTest;

  @AfterEach
  void tearDown() {
    reset(artistTransformer);
  }

  @Test
  @DisplayName("artistTransformer is called for every artist in album")
  void test_artist_transformer_called() {
    // given
    var spotifyAlbum = SpotifyAlbumFactory.createDefault();
    spotifyAlbum.getArtists().add(SpotfiyArtistFactory.withArtistName("artist2"));

    // when
    underTest.transform(spotifyAlbum);

    // then
    verify(artistTransformer, times(1)).transform(spotifyAlbum.getArtists().get(0));
    verify(artistTransformer, times(1)).transform(spotifyAlbum.getArtists().get(1));
  }

  @Test
  @DisplayName("artistTransformer's result is set")
  void test_artist_transformers_result_set() {
    // given
    var spotifyAlbum = SpotifyAlbumFactory.createDefault();
    var spotifyArtistDto = SpotifyArtistDtoFactory.withArtistName("artist");
    doReturn(spotifyArtistDto).when(artistTransformer).transform(any());

    // when
    var result = underTest.transform(spotifyAlbum);

    // then
    assertThat(result.getArtists()).isEqualTo(List.of(spotifyArtistDto));
  }

  @Test
  @DisplayName("spotifyAlbum is transformed")
  void test_spotify_album_transformed() {
    // given
    var spotifyAlbum = SpotifyAlbumFactory.createDefault();

    // when
    var result = underTest.transform(spotifyAlbum);

    // then
    assertThat(result.getGenres()).isEqualTo(spotifyAlbum.getGenres());
    assertThat(result.getId()).isEqualTo(spotifyAlbum.getId());
    assertThat(result.getName()).isEqualTo(spotifyAlbum.getName());
    assertThat(result.getPopularity()).isEqualTo(spotifyAlbum.getPopularity());
    assertThat(result.getUri()).isEqualTo(spotifyAlbum.getUri());
  }

  @ParameterizedTest
  @MethodSource("spotifyImageProvider")
  @DisplayName("if no images are present an empty String is transformed")
  void test_null_image_transformed(List<SpotifyImage> images) {
    // given
    var spotifyAlbum = SpotifyAlbumFactory.createDefault();
    spotifyAlbum.setImages(images);

    // when
    var result = underTest.transform(spotifyAlbum);

    // then
    assertThat(result.getImageUrl()).isNotNull().isEmpty();
  }

  private static Stream<Arguments> spotifyImageProvider() {
    return Stream.of(
        Arguments.of((Object) null),
        Arguments.of(Collections.emptyList())
    );
  }
}