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
import rocks.metaldetector.butler.api.ButlerReleasesRequest;
import rocks.metaldetector.butler.api.ButlerReleasesResponse;
import rocks.metaldetector.butler.client.ReleaseButlerRestClient;
import rocks.metaldetector.butler.client.transformer.ButlerReleaseRequestTransformer;
import rocks.metaldetector.butler.client.transformer.ButlerReleaseResponseTransformer;
import rocks.metaldetector.butler.facade.dto.ReleaseDto;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReleaseServiceImplTest implements WithAssertions {

  @Mock
  private ReleaseButlerRestClient butlerClient;

  @Mock
  private ButlerReleaseRequestTransformer requestTransformer;

  @Mock
  private ButlerReleaseResponseTransformer responseTransformer;

  @InjectMocks
  private ReleaseServiceImpl underTest;

  @AfterEach
  void tearDown() {
    reset(butlerClient, requestTransformer, responseTransformer);
  }

  @Test
  @DisplayName("Should use request transformer to transform arguments")
  void should_transform_arguments() {
    // given
    Iterable<String> artists = List.of("A", "B", "C");
    LocalDate from = LocalDate.of(2020, 1, 1);
    LocalDate to = LocalDate.of(2020, 12, 31);

    // when
    underTest.findReleases(artists, from, to);

    // then
    verify(requestTransformer, times(1)).transform(artists, from, to);
  }

  @Test
  @DisplayName("Should pass transformed arguments to butler client")
  void should_call_butler_client() {
    // given
    ButlerReleasesRequest request = ButlerReleaseRequestFactory.createDefault();
    when(requestTransformer.transform(any(), any(), any())).thenReturn(request);

    // when
    underTest.findReleases(null, null, null);

    // then
    verify(butlerClient, times(1)).queryReleases(eq(request));
  }

  @Test
  @DisplayName("Should transform and return response from butler client")
  void should_return_transformed_response() {
    // given
    ButlerReleasesResponse response = ButlerReleasesResponseFactory.createDefault();
    List<ReleaseDto> expectedResult = List.of(ReleaseDtoFactory.createDefault());
    when(butlerClient.queryReleases(any())).thenReturn(response);
    when(responseTransformer.transform(response)).thenReturn(expectedResult);

    // when
    List<ReleaseDto> releases = underTest.findReleases(null, null, null);

    // then
    verify(responseTransformer, times(1)).transform(response);
    assertThat(releases).isEqualTo(expectedResult);
  }
}
