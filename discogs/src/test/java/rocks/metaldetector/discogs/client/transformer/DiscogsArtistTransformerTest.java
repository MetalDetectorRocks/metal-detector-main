package rocks.metaldetector.discogs.client.transformer;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import rocks.metaldetector.discogs.api.DiscogsArtist;
import rocks.metaldetector.discogs.api.DiscogsImage;
import rocks.metaldetector.discogs.client.DiscogsDtoFactory.DiscogsArtistFactory;
import rocks.metaldetector.discogs.client.DiscogsDtoFactory.DiscogsImageFactory;
import rocks.metaldetector.discogs.facade.dto.DiscogsArtistDto;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

class DiscogsArtistTransformerTest implements WithAssertions {

  private DiscogsArtistTransformer underTest = new DiscogsArtistTransformer();

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
                            .name(discogsArtist.getName())
                            .imageUrl("")
                            .build()
    );
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
  @DisplayName("Image url is null if artist has no images")
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
            Arguments.of((List) null)
    );
  }
}
