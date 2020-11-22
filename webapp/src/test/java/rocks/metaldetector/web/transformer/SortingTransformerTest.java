package rocks.metaldetector.web.transformer;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;
import rocks.metaldetector.support.Sorting;

import java.util.List;

import static rocks.metaldetector.support.Sorting.Direction.ASC;
import static rocks.metaldetector.support.Sorting.Direction.DESC;

class SortingTransformerTest implements WithAssertions {

  private final SortingTransformer underTest = new SortingTransformer();

  @Test
  @DisplayName("Sort is transformed to Sorting")
  void test_transform() {
    // given
    var sort = Sort.by(Sort.Order.asc("artist"), Sort.Order.desc("releaseDate"));
    var expectedSorting = new Sorting(List.of(new Sorting.Order(ASC, "artist"), new Sorting.Order(DESC, "releaseDate")));

    // when
    var result = underTest.transform(sort);

    // then
    assertThat(result).isEqualTo(expectedSorting);
  }
}