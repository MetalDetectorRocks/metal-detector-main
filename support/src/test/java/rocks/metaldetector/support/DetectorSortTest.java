package rocks.metaldetector.support;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static rocks.metaldetector.support.DetectorSort.Direction.ASC;

class DetectorSortTest implements WithAssertions {

  @Test
  @DisplayName("Constructor with direction and list of properties works correctly")
  void test_constructor() {
    // given
    var givenDirection = ASC;
    var givenProperties = List.of("artist", "albumTitle", "releaseDate");
    var expectedOrders = List.of(new DetectorSort.Order(givenDirection, givenProperties.get(0)),
                                 new DetectorSort.Order(givenDirection, givenProperties.get(1)),
                                 new DetectorSort.Order(givenDirection, givenProperties.get(2)));

    // when
    var result = new DetectorSort(givenDirection, givenProperties);

    // then
    assertThat(result.getOrders()).containsExactly(expectedOrders.toArray(DetectorSort.Order[]::new));
  }

  @Test
  @DisplayName("ToString returns formatted objects to use as url parameter")
  void test_to_string() {
    // given
    var givenDetectorSort = new DetectorSort(ASC, List.of("artist", "albumTitle", "releaseDate"));
    var expectedSortString = "sort=artist,ASC&sort=albumTitle,ASC&sort=releaseDate,ASC";

    // when
    var sortAsString = givenDetectorSort.toString();

    // then
    assertThat(sortAsString).isEqualTo(expectedSortString);
  }
}
