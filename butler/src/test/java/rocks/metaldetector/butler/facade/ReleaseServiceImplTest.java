package rocks.metaldetector.butler.facade;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.metaldetector.butler.ButlerDtoFactory.ButlerImportJobResponseFactory;
import rocks.metaldetector.butler.ButlerDtoFactory.ButlerReleaseRequestFactory;
import rocks.metaldetector.butler.ButlerDtoFactory.ButlerReleasesResponseFactory;
import rocks.metaldetector.butler.ButlerDtoFactory.ImportJobResultDtoFactory;
import rocks.metaldetector.butler.ButlerDtoFactory.ReleaseDtoFactory;
import rocks.metaldetector.butler.api.ButlerImportJobResponse;
import rocks.metaldetector.butler.api.ButlerReleasesRequest;
import rocks.metaldetector.butler.api.ButlerReleasesResponse;
import rocks.metaldetector.butler.client.ReleaseButlerRestClient;
import rocks.metaldetector.butler.client.transformer.ButlerImportJobResponseTransformer;
import rocks.metaldetector.butler.client.transformer.ButlerReleaseRequestTransformer;
import rocks.metaldetector.butler.client.transformer.ButlerReleaseResponseTransformer;
import rocks.metaldetector.butler.facade.dto.ImportJobResultDto;
import rocks.metaldetector.butler.facade.dto.ReleaseDto;

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

@ExtendWith(MockitoExtension.class)
class ReleaseServiceImplTest implements WithAssertions {

  @Mock
  private ReleaseButlerRestClient butlerClient;

  @Mock
  private ButlerReleaseRequestTransformer releaseRequestTransformer;

  @Mock
  private ButlerReleaseResponseTransformer releaseResponseTransformer;

  @Mock
  private ButlerImportJobResponseTransformer importJobResponseTransformer;

  @InjectMocks
  private ReleaseServiceImpl underTest;

  @AfterEach
  void tearDown() {
    reset(butlerClient, releaseRequestTransformer, releaseResponseTransformer, importJobResponseTransformer);
  }

  @Test
  @DisplayName("Querying releases should use request transformer to transform arguments")
  void query_releases_should_transform_request_arguments() {
    // given
    Iterable<String> artists = List.of("A", "B", "C");
    LocalDate from = LocalDate.of(2020, 1, 1);
    LocalDate to = LocalDate.of(2020, 12, 31);

    // when
    underTest.findReleases(artists, from, to);

    // then
    verify(releaseRequestTransformer, times(1)).transform(artists, from, to);
  }

  @Test
  @DisplayName("Querying releases should pass transformed arguments to butler client")
  void query_releases_should_call_butler_client() {
    // given
    ButlerReleasesRequest request = ButlerReleaseRequestFactory.createDefault();
    when(releaseRequestTransformer.transform(any(), any(), any())).thenReturn(request);

    // when
    underTest.findReleases(null, null, null);

    // then
    verify(butlerClient, times(1)).queryReleases(eq(request));
  }

  @Test
  @DisplayName("Querying releases should transform and return response from butler client")
  void query_releases_should_return_transformed_response() {
    // given
    ButlerReleasesResponse response = ButlerReleasesResponseFactory.createDefault();
    List<ReleaseDto> expectedResult = List.of(ReleaseDtoFactory.createDefault());
    when(butlerClient.queryReleases(any())).thenReturn(response);
    when(releaseResponseTransformer.transform(response)).thenReturn(expectedResult);

    // when
    List<ReleaseDto> releases = underTest.findReleases(null, null, null);

    // then
    verify(releaseResponseTransformer, times(1)).transform(response);
    assertThat(releases).isEqualTo(expectedResult);
  }

  @Test
  @DisplayName("Creating an import job should call butler client")
  void create_import_job_should_call_butler_client() {
    // when
    underTest.createImportJob();

    // then
    verify(butlerClient, times(1)).createImportJob();
  }

  @Test
  @DisplayName("Querying import job results should use butler client")
  void query_import_job_results_should_use_butler_client() {
    // given
    doReturn(Collections.emptyList()).when(butlerClient).queryImportJobResults();

    // when
    underTest.queryImportJobResults();

    // then
    verify(butlerClient, times(1)).queryImportJobResults();
  }

  @Test
  @DisplayName("Querying import job results should use import job transformer to transform each response")
  void query_import_job_results_should_use_transformer() {
    // given
    var butlerImportJobResponses = List.of(
            ButlerImportJobResponseFactory.createDefault(),
            ButlerImportJobResponseFactory.createDefault()
    );
    doReturn(butlerImportJobResponses).when(butlerClient).queryImportJobResults();

    // when
    underTest.queryImportJobResults();

    // then
    verify(importJobResponseTransformer, times(butlerImportJobResponses.size())).transform(any(ButlerImportJobResponse.class));
  }

  @Test
  @DisplayName("Querying import job results should return transformed responses")
  void query_import_job_results_should_return_transformed_responses() {
    // given
    var butlerImportJobResponses = List.of(
            ButlerImportJobResponseFactory.createDefault(),
            ButlerImportJobResponseFactory.createDefault()
    );
    doReturn(butlerImportJobResponses).when(butlerClient).queryImportJobResults();
    doReturn(ImportJobResultDtoFactory.createDefault()).when(importJobResponseTransformer).transform(any());

    // when
    List<ImportJobResultDto> response = underTest.queryImportJobResults();

    // then
    assertThat(response).isEqualTo(List.of(
            ImportJobResultDtoFactory.createDefault(),
            ImportJobResultDtoFactory.createDefault()
    ));
  }
}
