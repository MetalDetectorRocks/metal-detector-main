package rocks.metaldetector.persistence.domain.artist;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

class ArtistEntityTest implements WithAssertions {

  @ParameterizedTest(name = "Should return <{0}> as <{1}>")
  @MethodSource("genreProvider")
  @DisplayName("Should return genres as List")
  void should_return_genres_as_list(String givenGenres, List<String> expectedGenres) {
    // given
    ArtistEntity artistEntity = ArtistEntity.builder()
            .artistName("Harakiri for the Sky")
            .genres(givenGenres)
            .build();

    // when
    var genresAsList = artistEntity.getGenresAsList();

    // then
    assertThat(genresAsList).isEqualTo(expectedGenres);
  }

  private static Stream<Arguments> genreProvider() {
    return Stream.of(
            Arguments.of(null, Collections.emptyList()),
            Arguments.of("", Collections.emptyList()),
            Arguments.of(" ", Collections.emptyList()),
            Arguments.of("Black Metal", List.of("Black Metal")),
            Arguments.of("Black Metal,Post Black Metal", List.of("Black Metal", "Post Black Metal")),
            Arguments.of("Black Metal, Post Black Metal", List.of("Black Metal", "Post Black Metal"))
    );
  }
}
