package rocks.metaldetector.butler.facade;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.metaldetector.butler.ButlerDtoFactory.ButlerReleaseRequestFactory;
import rocks.metaldetector.butler.ButlerDtoFactory.ButlerReleasesResponseFactory;
import rocks.metaldetector.butler.ButlerDtoFactory.ReleaseDtoFactory;
import rocks.metaldetector.butler.api.ButlerReleasesRequest;
import rocks.metaldetector.butler.api.ButlerReleasesResponse;
import rocks.metaldetector.butler.client.ReleaseButlerRestClient;
import rocks.metaldetector.butler.client.transformer.ButlerReleaseRequestTransformer;
import rocks.metaldetector.butler.client.transformer.ButlerReleaseResponseTransformer;
import rocks.metaldetector.butler.client.transformer.ButlerSortTransformer;
import rocks.metaldetector.butler.facade.dto.ReleaseDto;
import rocks.metaldetector.support.DetectorSort;
import rocks.metaldetector.support.Page;
import rocks.metaldetector.support.PageRequest;
import rocks.metaldetector.support.Pagination;
import rocks.metaldetector.support.TimeRange;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static rocks.metaldetector.support.DetectorSort.Direction.ASC;

@ExtendWith(MockitoExtension.class)
class ReleaseServiceImplTest implements WithAssertions {

  @Mock
  private ReleaseButlerRestClient butlerClient;

  @Mock
  private ButlerReleaseRequestTransformer releaseRequestTransformer;

  @Mock
  private ButlerSortTransformer sortTransformer;

  @Mock
  private ButlerReleaseResponseTransformer releaseResponseTransformer;

  private ReleaseServiceImpl underTest;

  @BeforeEach
  void setup() {
    underTest = new ReleaseServiceImpl(butlerClient, releaseRequestTransformer, sortTransformer, releaseResponseTransformer);
  }

  @AfterEach
  void tearDown() {
    reset(butlerClient, releaseRequestTransformer, sortTransformer, releaseResponseTransformer);
  }

  @DisplayName("Test of findAllReleases()")
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  @Nested
  class FindAllReleasesTest {

    @Test
    @DisplayName("Should use request transformer to transform arguments")
    void should_transform_request_arguments() {
      // given
      Iterable<String> artists = List.of("A", "B", "C");
      TimeRange timeRange = new TimeRange(LocalDate.of(2020, 1, 1), LocalDate.of(2020, 12, 31));

      // when
      underTest.findAllReleases(artists, timeRange);

      // then
      verify(releaseRequestTransformer).transform(artists, timeRange, null, null);
    }

    @Test
    @DisplayName("Should pass transformed arguments to butler client")
    void Should_call_butler_client() {
      // given
      ButlerReleasesRequest request = ButlerReleaseRequestFactory.createDefault();
      when(releaseRequestTransformer.transform(any(), any(), any(), any())).thenReturn(request);

      // when
      underTest.findAllReleases(null, null);

      // then
      verify(butlerClient).queryAllReleases(eq(request));
    }

    @Test
    @DisplayName("Should transform and return response from butler client")
    void should_return_transformed_response() {
      // given
      ButlerReleasesResponse response = ButlerReleasesResponseFactory.createDefault();
      List<ReleaseDto> expectedResult = List.of(ReleaseDtoFactory.createDefault());
      when(butlerClient.queryAllReleases(any())).thenReturn(response);
      when(releaseResponseTransformer.transformToList(response)).thenReturn(expectedResult);

      // when
      List<ReleaseDto> releases = underTest.findAllReleases(null, null);

      // then
      verify(releaseResponseTransformer).transformToList(response);
      assertThat(releases).isEqualTo(expectedResult);
    }
  }

  @DisplayName("Test of findReleases() with PageRequest")
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  @Nested
  class FindReleasesTest {

    @Test
    @DisplayName("Should use request transformer to transform arguments")
    void should_transform_request_arguments() {
      // given
      Iterable<String> artists = List.of("A", "B", "C");
      TimeRange timeRange = new TimeRange(LocalDate.of(2020, 1, 1), LocalDate.of(2020, 12, 31));
      PageRequest pageRequest = new PageRequest(1, 10, null);
      String query = "query";

      // when
      underTest.findReleases(artists, timeRange, query, pageRequest);

      // then
      verify(releaseRequestTransformer).transform(artists, timeRange, query, pageRequest);
    }

    @Test
    @DisplayName("Should use sort transformer to transform sort parameter")
    void should_transform_sort_parameter() {
      // given
      PageRequest pageRequest = new PageRequest(1, 10, new DetectorSort("foo", "asc"));

      // when
      underTest.findReleases(null, null, null, pageRequest);

      // then
      verify(sortTransformer).transform(pageRequest.getSort());
    }

    @Test
    @DisplayName("Should pass transformed request to butler client")
    void should_call_butler_client_with_request() {
      // given
      ButlerReleasesRequest request = ButlerReleaseRequestFactory.createDefault();
      when(releaseRequestTransformer.transform(any(), any(), any(), any())).thenReturn(request);

      // when
      underTest.findReleases(null, null, null, new PageRequest());

      // then
      verify(butlerClient).queryReleases(eq(request), any());
    }

    @Test
    @DisplayName("Should pass transformed sort parameter to butler client")
    void should_call_butler_client_with_sort() {
      // given
      var transformedSortParam = "sort";
      PageRequest pageRequest = new PageRequest(0, 0, new DetectorSort("artist", ASC));
      when(sortTransformer.transform(any())).thenReturn(transformedSortParam);

      // when
      underTest.findReleases(null, null, null, pageRequest);

      // then
      verify(butlerClient).queryReleases(any(), eq(transformedSortParam));
    }

    @Test
    @DisplayName("Should transform and return response from butler client")
    void should_return_transformed_response() {
      // given
      ButlerReleasesResponse response = ButlerReleasesResponseFactory.createDefault();
      Page<ReleaseDto> expectedResult = new Page<>(List.of(ReleaseDtoFactory.createDefault()), new Pagination());
      when(butlerClient.queryReleases(any(), any())).thenReturn(response);
      when(releaseResponseTransformer.transformToPage(response)).thenReturn(expectedResult);

      // when
      Page<ReleaseDto> releases = underTest.findReleases(null, null, null, new PageRequest());

      // then
      verify(releaseResponseTransformer).transformToPage(response);
      assertThat(releases).isEqualTo(expectedResult);
    }
  }

  @Test
  @DisplayName("Updating a release should call butler client")
  void update_release_should_call_butler_client() {
    // given
    var releaseId = 1L;
    var state = "state";

    // when
    underTest.updateReleaseState(releaseId, state);

    // then
    verify(butlerClient).updateReleaseState(releaseId, state);
  }
}
