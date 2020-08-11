package rocks.metaldetector.discogs.client.transformer;

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
import rocks.metaldetector.discogs.api.DiscogsArtist;
import rocks.metaldetector.discogs.api.DiscogsImage;
import rocks.metaldetector.discogs.client.DiscogsDtoFactory.DiscogsArtistFactory;
import rocks.metaldetector.discogs.client.DiscogsDtoFactory.DiscogsImageFactory;
import rocks.metaldetector.discogs.facade.dto.DiscogsArtistDto;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DiscogsArtistTransformerTest implements WithAssertions {

  @Mock
  private DiscogsArtistNameTransformer artistNameTransformer;

  @InjectMocks
  private DiscogsArtistTransformer underTest;

  @AfterEach
  void tearDown() {
    reset(artistNameTransformer);
  }

  @Test
  @DisplayName("Should transform DiscogsArtist to DiscogsArtistDto")
  void should_transform_id_and_name() {
    // given
    DiscogsArtist discogsArtist = DiscogsArtistFactory.createDefault();

    // when
    DiscogsArtistDto result = underTest.transform(discogsArtist);

    // then
    assertThat(result).isEqualTo(
            DiscogsArtistDto.builder()
                            .id(String.valueOf(discogsArtist.getId()))
                            .imageUrl("")
                            .build()
    );
  }

  @Test
  @DisplayName("Should use DiscogsArtistNameTransformer to transform artist name")
  void should_transform_artist_name() {
    // given
    var expectedName = "Slayer";
    DiscogsArtist discogsArtist = DiscogsArtistFactory.createDefault();
    doReturn(expectedName).when(artistNameTransformer).transformArtistName(anyString());

    // when
    DiscogsArtistDto result = underTest.transform(discogsArtist);

    // then
    verify(artistNameTransformer, times(1)).transformArtistName(discogsArtist.getName());
    assertThat(result.getName()).isEqualTo(expectedName);
  }

  @Test
  @DisplayName("Should transform always the first image as image url")
  void should_transform_image_url() {
    // given
    DiscogsArtist discogsArtist = DiscogsArtistFactory.createDefault();
    List<DiscogsImage> images = List.of(
            DiscogsImageFactory.createDefault("img1"),
            DiscogsImageFactory.createDefault("img2")
    );
    discogsArtist.setImages(images);

    // when
    DiscogsArtistDto result = underTest.transform(discogsArtist);

    // then
    assertThat(result.getImageUrl()).isEqualTo(images.get(0).getResourceUrl());
  }

  @ParameterizedTest(name = "Should not fail if images is {0}")
  @MethodSource("discogsImageProvider")
  @DisplayName("Image url is empty if artist has no images")
  void should_not_fail_on_empty_image(List<DiscogsImage> images) {
    // given
    DiscogsArtist discogsArtist = DiscogsArtistFactory.createDefault();
    discogsArtist.setImages(images);

    // when
    DiscogsArtistDto result = underTest.transform(discogsArtist);

    // then
    assertThat(result.getImageUrl()).isEmpty();
  }

  private static Stream<Arguments> discogsImageProvider() {
    return Stream.of(
            Arguments.of(Collections.emptyList()),
            Arguments.of((List<DiscogsImage>) null)
    );
  }
}
