package rocks.metaldetector.service.releases;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import rocks.metaldetector.web.DtoFactory;
import rocks.metaldetector.web.dto.request.ButlerReleasesRequest;
import rocks.metaldetector.web.dto.response.ButlerReleasesResponse;
import rocks.metaldetector.web.dto.releases.ReleaseDto;

import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static rocks.metaldetector.web.DtoFactory.ButlerReleasesResponseFactory;

@ExtendWith(MockitoExtension.class)
class ReleasesServiceTest implements WithAssertions {

  private static  final String ALL_RELEASES_URL = "url";

  @Mock
  private RestTemplate restTemplate;

  private ReleasesServiceImpl releasesService;

  @Captor
  private ArgumentCaptor<HttpEntity<ButlerReleasesRequest>> argumentCaptor;

  @BeforeEach
  void setUp() {
    releasesService = new ReleasesServiceImpl(restTemplate, ALL_RELEASES_URL);
  }

  @AfterEach
  void tearDown() {
    reset(restTemplate);
  }

  @Test
  @DisplayName("getReleases() should return valid result")
  void get_releases_valid_result() {
    // given
    LocalDate releaseDate = LocalDate.of(2020, 1, 1);
    ButlerReleasesResponse responseMock = ButlerReleasesResponseFactory.withOneResult("A1", releaseDate);
    ButlerReleasesRequest request = new ButlerReleasesRequest();
    when(restTemplate.postForEntity(eq(ALL_RELEASES_URL), any(HttpEntity.class), eq(ButlerReleasesResponse.class)))
        .thenReturn(ResponseEntity.ok(responseMock));

    // when
    List<ReleaseDto> releases = releasesService.getReleases(request);

    // then
    assertThat(releases).hasSize(1);
    assertThat(releases.get(0)).isEqualTo(DtoFactory.ReleaseDtoFactory.withOneResult("A1", releaseDate));

    verify(restTemplate, times(1)).postForEntity(eq(ALL_RELEASES_URL), any(HttpEntity.class), eq(ButlerReleasesResponse.class));
  }

  @ParameterizedTest(name = "[{index}] => Result <{0}> | HttpStatus <{1}>")
  @MethodSource("responseProvider")
  @DisplayName("getReleases() should return empty result on when butler sends no usable response")
  void get_releases_empty_response(ButlerReleasesResponse responseMock, HttpStatus httpStatus) {
    // given
    ButlerReleasesRequest request = new ButlerReleasesRequest();
    when(restTemplate.postForEntity(eq(ALL_RELEASES_URL), any(HttpEntity.class), eq(ButlerReleasesResponse.class)))
        .thenReturn(ResponseEntity.status(httpStatus).body(responseMock));

    // when
    List<ReleaseDto> releases = releasesService.getReleases(request);

    // then
    assertThat(releases).isEmpty();

    verify(restTemplate, times(1)).postForEntity(eq(ALL_RELEASES_URL), any(HttpEntity.class), eq(ButlerReleasesResponse.class));
  }

  private static Stream<Arguments> responseProvider() {
    ButlerReleasesResponse result = ButlerReleasesResponseFactory.withOneResult("A1", LocalDate.now());
    ButlerReleasesResponse emptyResult = ButlerReleasesResponseFactory.withEmptyResult();
    return Stream.of(
        Arguments.of(null, HttpStatus.OK),
        Arguments.of(result, HttpStatus.BAD_REQUEST),
        Arguments.of(emptyResult, HttpStatus.OK)
    );
  }

  @Test
  @DisplayName("Test http entity request")
  void test_http_entity() {
    // given
    ButlerReleasesResponse responseMock = ButlerReleasesResponseFactory.withOneResult("A1", LocalDate.now());
    ButlerReleasesRequest request = new ButlerReleasesRequest();
    when(restTemplate.postForEntity(eq(ALL_RELEASES_URL), any(HttpEntity.class), eq(ButlerReleasesResponse.class)))
        .thenReturn(ResponseEntity.ok(responseMock));

    // when
    releasesService.getReleases(request);

    // then
    verify(restTemplate, times(1)).postForEntity(eq(ALL_RELEASES_URL), argumentCaptor.capture(), eq(ButlerReleasesResponse.class));

    HttpEntity<ButlerReleasesRequest> httpEntity = argumentCaptor.getValue();
    HttpHeaders headers = httpEntity.getHeaders();

    assertThat(httpEntity.getBody()).isEqualTo(request);
    assertThat(headers.getAccept()).isEqualTo(Collections.singletonList(MediaType.APPLICATION_JSON));
    assertThat(headers.getContentType()).isEqualByComparingTo(MediaType.APPLICATION_JSON);
    assertThat(headers.getAcceptCharset()).isEqualTo(Collections.singletonList(Charset.defaultCharset()));
  }
}