package rocks.metaldetector.butler.facade;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.metaldetector.butler.ButlerDtoFactory.ButlerReleaseRequestFactory;
import rocks.metaldetector.butler.ButlerDtoFactory.ButlerReleasesResponseFactory;
import rocks.metaldetector.butler.ButlerDtoFactory.ReleaseDtoFactory;
import rocks.metaldetector.butler.api.ButlerImportResponse;
import rocks.metaldetector.butler.api.ButlerReleasesRequest;
import rocks.metaldetector.butler.api.ButlerReleasesResponse;
import rocks.metaldetector.butler.client.ReleaseButlerRestClient;
import rocks.metaldetector.butler.client.transformer.ButlerImportResponseTransformer;
import rocks.metaldetector.butler.client.transformer.ButlerReleaseRequestTransformer;
import rocks.metaldetector.butler.client.transformer.ButlerReleaseResponseTransformer;
import rocks.metaldetector.butler.facade.dto.ImportResultDto;
import rocks.metaldetector.butler.facade.dto.ReleaseDto;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static rocks.metaldetector.butler.ButlerDtoFactory.ButlerImportResponseFactory;
import static rocks.metaldetector.butler.ButlerDtoFactory.ImportResultDtoFactory;

@ExtendWith(MockitoExtension.class)
class ReleaseServiceImplTest implements WithAssertions {

  @Mock
  private ReleaseButlerRestClient butlerClient;

  @Mock
  private ButlerReleaseRequestTransformer releaseRequestTransformer;

  @Mock
  private ButlerReleaseResponseTransformer releaseResponseTransformer;

  @Mock
  private ButlerImportResponseTransformer importResponseTransformer;

  @InjectMocks
  private ReleaseServiceImpl underTest;

  @AfterEach
  void tearDown() {
    reset(butlerClient, releaseRequestTransformer, releaseResponseTransformer, importResponseTransformer);
  }

  @Test
  @DisplayName("Querying should use request transformer to transform arguments")
  void query_should_transform_request_arguments() {
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
  @DisplayName("Querying should pass transformed arguments to butler client")
  void query_should_call_butler_client() {
    // given
    ButlerReleasesRequest request = ButlerReleaseRequestFactory.createDefault();
    when(releaseRequestTransformer.transform(any(), any(), any())).thenReturn(request);

    // when
    underTest.findReleases(null, null, null);

    // then
    verify(butlerClient, times(1)).queryReleases(eq(request));
  }

  @Test
  @DisplayName("Querying should transform and return response from butler client")
  void query_should_return_transformed_response() {
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
  @DisplayName("Import should call butler client")
  void import_should_call_butler_client() {
    // when
    underTest.importReleases();

    // then
    verify(butlerClient, times(1)).importReleases();
  }

  @Test
  @DisplayName("Import should transform and return response from butler client")
  void import_should_return_transformed_response() {
    // given
    ButlerImportResponse response = ButlerImportResponseFactory.createDefault();
    ImportResultDto expectedResult = ImportResultDtoFactory.createDefault();
    when(butlerClient.importReleases()).thenReturn(response);
    when(importResponseTransformer.transform(response)).thenReturn(expectedResult);

    // when
    ImportResultDto importResult = underTest.importReleases();

    // then
    verify(importResponseTransformer, times(1)).transform(response);
    assertThat(importResult).isEqualTo(expectedResult);
  }
}
