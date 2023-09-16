package rocks.metaldetector.butler.client.transformer;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import rocks.metaldetector.butler.api.ButlerImportInfo;
import rocks.metaldetector.butler.api.ButlerReleaseInfo;
import rocks.metaldetector.butler.api.ButlerStatisticsResponse;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

class ButlerStatisticsTransformerTest implements WithAssertions {

  private final ButlerStatisticsTransformer underTest = new ButlerStatisticsTransformer();

  @DisplayName("Test for ReleaseInfo")
  @Nested
  class ReleaseInfoTest {

    private static final ButlerReleaseInfo RELEASE_INFO = ButlerReleaseInfo.builder()
        .releasesPerMonth(Map.of(YearMonth.now(), 66))
        .upcomingReleases(6)
        .totalReleases(666)
        .releasesThisMonth(67)
        .duplicates(1)
        .build();
    private static final ButlerStatisticsResponse BUTLER_RESPONSE = ButlerStatisticsResponse.builder()
        .releaseInfo(RELEASE_INFO)
        .build();

    @Test
    @DisplayName("Should transform releases per month")
    void should_transform_releases_per_month() {
      // when
      var result = underTest.transform(BUTLER_RESPONSE);

      // then
      assertThat(result.getReleaseInfo()).isNotNull();
      assertThat(result.getReleaseInfo().getReleasesPerMonth()).isEqualTo(RELEASE_INFO.getReleasesPerMonth());
    }

    @Test
    @DisplayName("Should transform upcoming releases")
    void should_transform_upcoming_releases() {
      // when
      var result = underTest.transform(BUTLER_RESPONSE);

      // then
      assertThat(result.getReleaseInfo()).isNotNull();
      assertThat(result.getReleaseInfo().getUpcomingReleases()).isEqualTo(RELEASE_INFO.getUpcomingReleases());
    }

    @Test
    @DisplayName("Should transform releases this month")
    void should_transform_releases_this_month() {
      // when
      var result = underTest.transform(BUTLER_RESPONSE);

      // then
      assertThat(result.getReleaseInfo()).isNotNull();
      assertThat(result.getReleaseInfo().getReleasesThisMonth()).isEqualTo(RELEASE_INFO.getReleasesThisMonth());
    }

    @Test
    @DisplayName("Should transform total releases")
    void should_transform_total_releases() {
      // when
      var result = underTest.transform(BUTLER_RESPONSE);

      // then
      assertThat(result.getReleaseInfo()).isNotNull();
      assertThat(result.getReleaseInfo().getTotalReleases()).isEqualTo(RELEASE_INFO.getTotalReleases());
    }

    @Test
    @DisplayName("Should transform duplicates")
    void should_transform_duplicates() {
      // when
      var result = underTest.transform(BUTLER_RESPONSE);

      // then
      assertThat(result.getReleaseInfo()).isNotNull();
      assertThat(result.getReleaseInfo().getDuplicates()).isEqualTo(RELEASE_INFO.getDuplicates());
    }
  }

  @DisplayName("Test of ImportInfo")
  @Nested
  class ImportInfoTest {

    private static final ButlerImportInfo IMPORT_INFO = ButlerImportInfo.builder()
        .source("someSource")
        .successRate(100)
        .lastImport(LocalDateTime.now())
        .lastImport(LocalDateTime.now())
        .build();
    private static final ButlerStatisticsResponse BUTLER_RESPONSE = ButlerStatisticsResponse.builder()
        .importInfo(List.of(IMPORT_INFO))
        .build();

    @Test
    @DisplayName("Should transform source")
    void should_transform_source() {
      // when
      var result = underTest.transform(BUTLER_RESPONSE);

      // then
      assertThat(result.getImportInfo()).isNotNull().isNotEmpty();
      assertThat(result.getImportInfo().get(0).getSource()).isEqualTo(IMPORT_INFO.getSource());
    }

    @Test
    @DisplayName("Should transform success rate")
    void should_transform_success_rate() {
      // when
      var result = underTest.transform(BUTLER_RESPONSE);

      // then
      assertThat(result.getImportInfo()).isNotNull().isNotEmpty();
      assertThat(result.getImportInfo().get(0).getSuccessRate()).isEqualTo(IMPORT_INFO.getSuccessRate());
    }

    @Test
    @DisplayName("Should transform last import")
    void should_transform_last_import() {
      // when
      var result = underTest.transform(BUTLER_RESPONSE);

      // then
      assertThat(result.getImportInfo()).isNotNull().isNotEmpty();
      assertThat(result.getImportInfo().get(0).getLastImport()).isEqualTo(IMPORT_INFO.getLastImport());
    }

    @Test
    @DisplayName("Should transform last successful import")
    void should_transform_last_successful_import() {
      // when
      var result = underTest.transform(BUTLER_RESPONSE);

      // then
      assertThat(result.getImportInfo()).isNotNull().isNotEmpty();
      assertThat(result.getImportInfo().get(0).getLastSuccessfulImport()).isEqualTo(IMPORT_INFO.getLastSuccessfulImport());
    }

    @Test
    @DisplayName("Should transform all imports")
    void should_transform_all() {
      // given
      BUTLER_RESPONSE.setImportInfo(List.of(IMPORT_INFO, IMPORT_INFO, IMPORT_INFO));

      // when
      var result = underTest.transform(BUTLER_RESPONSE);

      // then
      assertThat(result.getImportInfo()).isNotNull().hasSize(BUTLER_RESPONSE.getImportInfo().size());
    }
  }
}
