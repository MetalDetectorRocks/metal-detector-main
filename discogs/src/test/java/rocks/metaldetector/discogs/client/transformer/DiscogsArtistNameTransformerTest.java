package rocks.metaldetector.discogs.client.transformer;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

class DiscogsArtistNameTransformerTest implements WithAssertions {

  private final DiscogsArtistNameTransformer underTest = new DiscogsArtistNameTransformer();

  @ParameterizedTest(name = "If the artist name is <{0}>, it should be transformed to <{1}>")
  @MethodSource("artristNameProvider")
  @DisplayName("Should cut suffix '($number)' from artist name")
  void should_transform_artist_name(String givenName, String expectedName) {
    // when
    String result = underTest.transformArtistName(givenName);

    // then
    assertThat(result).isEqualTo(expectedName);
  }

  private static Stream<Arguments> artristNameProvider() {
    return Stream.of(
      Arguments.of("Karg", "Karg"),
      Arguments.of("Karg (0)", "Karg"),
      Arguments.of("Karg (1)", "Karg"),
      Arguments.of("Karg (10)", "Karg"),
      Arguments.of("Karg(0)", "Karg"),
      Arguments.of("Karg(1)", "Karg"),
      Arguments.of("Karg(10)", "Karg"),
      Arguments.of("Karg (a)", "Karg (a)"),
      Arguments.of("Karg (1) Karg", "Karg (1) Karg"),
      Arguments.of("Karg Karg Karg (1)", "Karg Karg Karg")
    );
  }
}