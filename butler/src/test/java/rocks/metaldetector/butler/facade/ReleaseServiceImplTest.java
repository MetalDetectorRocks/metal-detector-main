package rocks.metaldetector.butler.facade;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.metaldetector.butler.ButlerDtoFactory.ButlerReleaseRequestFactory;
import rocks.metaldetector.butler.ButlerDtoFactory.ButlerReleasesResponseFactory;
import rocks.metaldetector.butler.ButlerDtoFactory.ImportJobResultDtoFactory;
import rocks.metaldetector.butler.ButlerDtoFactory.ReleaseDtoFactory;
import rocks.metaldetector.butler.api.ButlerImportJob;
import rocks.metaldetector.butler.api.ButlerReleasesRequest;
import rocks.metaldetector.butler.api.ButlerReleasesResponse;
import rocks.metaldetector.butler.client.ReleaseButlerRestClient;
import rocks.metaldetector.butler.client.transformer.ButlerImportJobTransformer;
import rocks.metaldetector.butler.client.transformer.ButlerReleaseRequestTransformer;
import rocks.metaldetector.butler.client.transformer.ButlerReleaseResponseTransformer;
import rocks.metaldetector.butler.facade.dto.ImportJobResultDto;
import rocks.metaldetector.butler.facade.dto.ReleaseDto;
import rocks.metaldetector.support.Page;
import rocks.metaldetector.support.PageRequest;
import rocks.metaldetector.support.Pagination;
import rocks.metaldetector.support.TimeRange;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static rocks.metaldetector.butler.ButlerDtoFactory.ButlerImportJobFactory;

@ExtendWith(MockitoExtension.class)
class ReleaseServiceImplTest implements WithAssertions {

  @Mock
  private ReleaseButlerRestClient butlerClient;

  @Mock
  private ButlerReleaseRequestTransformer releaseRequestTransformer;

  @Mock
  private ButlerReleaseResponseTransformer releaseResponseTransformer;

  @Mock
  private ButlerImportJobTransformer importJobResponseTransformer;

  @InjectMocks
  private ReleaseServiceImpl underTest;

  @AfterEach
  void tearDown() {
    reset(butlerClient, releaseRequestTransformer, releaseResponseTransformer, importJobResponseTransformer);
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
      verify(releaseRequestTransformer).transform(artists, timeRange, null);
    }

    @Test
    @DisplayName("Should pass transformed arguments to butler client")
    void Should_call_butler_client() {
      // given
      ButlerReleasesRequest request = ButlerReleaseRequestFactory.createDefault();
      when(releaseRequestTransformer.transform(any(), any(), any())).thenReturn(request);

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
      PageRequest pageRequest = new PageRequest(10, 1);

      // when
      underTest.findReleases(artists, timeRange, pageRequest);

      // then
      verify(releaseRequestTransformer).transform(artists, timeRange, pageRequest);
    }

    @Test
    @DisplayName("Should pass transformed arguments to butler client")
    void Should_call_butler_client() {
      // given
      ButlerReleasesRequest request = ButlerReleaseRequestFactory.createDefault();
      when(releaseRequestTransformer.transform(any(), any(), any())).thenReturn(request);

      // when
      underTest.findReleases(null, null, null);

      // then
      verify(butlerClient).queryReleases(eq(request));
    }

    @Test
    @DisplayName("Should transform and return response from butler client")
    void should_return_transformed_response() {
      // given
      ButlerReleasesResponse response = ButlerReleasesResponseFactory.createDefault();
      Page<ReleaseDto> expectedResult = new Page<>(List.of(ReleaseDtoFactory.createDefault()), new Pagination());
      when(butlerClient.queryReleases(any())).thenReturn(response);
      when(releaseResponseTransformer.transformToPage(response)).thenReturn(expectedResult);

      // when
      Page<ReleaseDto> releases = underTest.findReleases(null, null, null);

      // then
      verify(releaseResponseTransformer).transformToPage(response);
      assertThat(releases).isEqualTo(expectedResult);
    }
  }

  @Test
  @DisplayName("Creating an import job should call butler client")
  void create_import_job_should_call_butler_client() {
    // when
    underTest.createImportJob();

    // then
    verify(butlerClient).createImportJob();
  }

  @Test
  @DisplayName("Querying import job results should use butler client")
  void query_import_job_results_should_use_butler_client() {
    // given
    doReturn(Collections.emptyList()).when(butlerClient).queryImportJobResults();

    // when
    underTest.queryImportJobResults();

    // then
    verify(butlerClient).queryImportJobResults();
  }

  @Test
  @DisplayName("Querying import job results should use import job transformer to transform each response")
  void query_import_job_results_should_use_transformer() {
    // given
    var butlerImportJobs = List.of(
        ButlerImportJobFactory.createDefault(),
        ButlerImportJobFactory.createDefault()
    );
    doReturn(butlerImportJobs).when(butlerClient).queryImportJobResults();

    // when
    underTest.queryImportJobResults();

    // then
    verify(importJobResponseTransformer, times(butlerImportJobs.size())).transform(any(ButlerImportJob.class));
  }

  @Test
  @DisplayName("Querying import job results should return transformed responses")
  void query_import_job_results_should_return_transformed_responses() {
    // given
    var butlerImportJobs = List.of(
        ButlerImportJobFactory.createDefault(),
        ButlerImportJobFactory.createDefault()
    );
    doReturn(butlerImportJobs).when(butlerClient).queryImportJobResults();
    doReturn(ImportJobResultDtoFactory.createDefault()).when(importJobResponseTransformer).transform(any());

    // when
    List<ImportJobResultDto> response = underTest.queryImportJobResults();

    // then
    assertThat(response).isEqualTo(List.of(
        ImportJobResultDtoFactory.createDefault(),
        ImportJobResultDtoFactory.createDefault()
    ));
  }

  @Test
  @DisplayName("Creating an cover download job should call butler client")
  void create_cover_download_job_should_call_butler_client() {
    // when
    underTest.createRetryCoverDownloadJob();

    // then
    verify(butlerClient).createRetryCoverDownloadJob();
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
