package rocks.metaldetector.web.transformer;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;
import rocks.metaldetector.support.DetectorSort;

import java.util.List;

import static rocks.metaldetector.support.DetectorSort.Direction.ASC;
import static rocks.metaldetector.support.DetectorSort.Direction.DESC;

class DetectorSortTransformerTest implements WithAssertions {

  private final DetectorSortTransformer underTest = new DetectorSortTransformer();

  @Test
  @DisplayName("Sort is transformed to DetectorSort")
  void test_transform() {
    // given
    var sort = Sort.by(Sort.Order.asc("artist"), Sort.Order.desc("releaseDate"));
    var expectedSorting = new DetectorSort(List.of(new DetectorSort.Order(ASC, "artist"), new DetectorSort.Order(DESC, "releaseDate")));

    // when
    var result = underTest.transform(sort);

    // then
    assertThat(result).isEqualTo(expectedSorting);
  }
}