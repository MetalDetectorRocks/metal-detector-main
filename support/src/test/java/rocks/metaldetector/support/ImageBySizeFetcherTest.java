package rocks.metaldetector.support;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Stream;

import static rocks.metaldetector.support.ImageSize.L;
import static rocks.metaldetector.support.ImageSize.M;
import static rocks.metaldetector.support.ImageSize.S;
import static rocks.metaldetector.support.ImageSize.XS;

class ImageBySizeFetcherTest implements WithAssertions {

  private final ImageBySizeFetcherTestImpl underTest = new ImageBySizeFetcherTestImpl();

  @ParameterizedTest(name = "should return <{1}> as thumbnail")
  @MethodSource("thumbnailImageProvider")
  @DisplayName("Should return thumbnail")
  void should_return_thumbnail(Map<ImageSize, String> givenImages, String expected) {
    // given
    underTest.setImages(givenImages);

    // when
    var result = underTest.getThumbnailImage();

    // then
    assertThat(result).isEqualTo(expected);
  }

  private static Stream<Arguments> thumbnailImageProvider() {
    return Stream.of(
            Arguments.of(Map.of(XS, "xs", S, "s", M, "m"), "xs"),
            Arguments.of(Map.of(S, "s", M, "m"), "s"),
            Arguments.of(Map.of(M, "m"), ""),
            Arguments.of(Collections.emptyMap(), "")
    );
  }

  @ParameterizedTest(name = "should return <{1}> as small image")
  @MethodSource("smallImageProvider")
  @DisplayName("Should return small image")
  void should_return_small_image(Map<ImageSize, String> givenImages, String expected) {
    // given
    underTest.setImages(givenImages);

    // when
    var result = underTest.getSmallImage();

    // then
    assertThat(result).isEqualTo(expected);
  }

  private static Stream<Arguments> smallImageProvider() {
    return Stream.of(
            Arguments.of(Map.of(XS, "xs", S, "s", M, "m"), "s"),
            Arguments.of(Map.of(XS, "xs", M, "m", L, "l"), "m"),
            Arguments.of(Map.of(XS, "xs", L, "l"), "xs"),
            Arguments.of(Map.of(L, "l"), ""),
            Arguments.of(Collections.emptyMap(), "")
    );
  }

  @ParameterizedTest(name = "should return <{1}> as medium image")
  @MethodSource("mediumImageProvider")
  @DisplayName("Should return medium image")
  void should_return_medium_image(Map<ImageSize, String> givenImages, String expected) {
    // given
    underTest.setImages(givenImages);

    // when
    var result = underTest.getMediumImage();

    // then
    assertThat(result).isEqualTo(expected);
  }

  private static Stream<Arguments> mediumImageProvider() {
    return Stream.of(
            Arguments.of(Map.of(XS, "xs", S, "s", M, "m", L, "l"), "m"),
            Arguments.of(Map.of(XS, "xs", S, "s", L, "l"), "l"),
            Arguments.of(Map.of(XS, "xs", S, "s"), "s"),
            Arguments.of(Map.of(XS, "xs"), ""),
            Arguments.of(Collections.emptyMap(), "")
    );
  }

  @ParameterizedTest(name = "should return <{1}> as large image")
  @MethodSource("largeImageProvider")
  @DisplayName("Should return large image")
  void should_return_large_image(Map<ImageSize, String> givenImages, String expected) {
    // given
    underTest.setImages(givenImages);

    // when
    var result = underTest.getLargeImage();

    // then
    assertThat(result).isEqualTo(expected);
  }

  private static Stream<Arguments> largeImageProvider() {
    return Stream.of(
            Arguments.of(Map.of(XS, "xs", S, "s", M, "m", L, "l"), "l"),
            Arguments.of(Map.of(XS, "xs", S, "s", M, "m"), "m"),
            Arguments.of(Map.of(XS, "xs", S, "s"), ""),
            Arguments.of(Map.of(XS, "xs"), ""),
            Arguments.of(Collections.emptyMap(), "")
    );
  }
}

class ImageBySizeFetcherTestImpl implements ImageBySizeFetcher {

  private Map<ImageSize, String> images;

  @Override
  public Map<ImageSize, String> getImages() {
    return images;
  }

  void setImages(Map<ImageSize, String> images) {
    this.images = images;
  }
}
