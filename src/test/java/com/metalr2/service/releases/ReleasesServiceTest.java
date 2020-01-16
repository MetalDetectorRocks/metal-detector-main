package com.metalr2.service.releases;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.metalr2.config.misc.ReleaseButlerConfig;
import com.metalr2.web.DtoFactory;
import com.metalr2.web.dto.releases.ReleasesRequest;
import com.metalr2.web.dto.releases.ReleasesResponse;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Stream;

import static com.metalr2.service.releases.ReleasesServiceImpl.ALL_RELEASES_URL_FRAGMENT;
import static com.metalr2.web.DtoFactory.ReleasesResponseFactory;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReleasesServiceTest implements WithAssertions {

  private static final String BASE_URL = "base-url";
  private static  final String ALL_RELEASES_URL = BASE_URL + ALL_RELEASES_URL_FRAGMENT;

  @Mock
  private RestTemplate restTemplate;

  @Mock
  private ObjectMapper mapper;

  @Mock
  private ReleaseButlerConfig releaseButlerConfig;

  @InjectMocks
  private ReleasesServiceImpl releasesService;

  @Captor
  private ArgumentCaptor<HttpEntity<String>> argumentCaptor;

  @BeforeEach
  void setUp() {
  }

  @AfterEach
  void tearDown() {
    reset(restTemplate);
  }

  @Test
  @DisplayName("getReleases() should return valid result")
  void get_releases_valid_result() throws JsonProcessingException {
    // given
    LocalDate releaseDate = LocalDate.of(2020, 1, 1);
    ReleasesResponse responseMock = DtoFactory.ReleasesResponseFactory.withOneResult("A1", releaseDate);
    ReleasesRequest request = new ReleasesRequest();
    when(releaseButlerConfig.getRestBaseUrl()).thenReturn(BASE_URL);
    when(restTemplate.postForEntity(eq(ALL_RELEASES_URL), any(HttpEntity.class), eq(ReleasesResponse.class)))
        .thenReturn(ResponseEntity.ok(responseMock));
    when(mapper.writeValueAsString(request)).thenReturn("body");

    // when
    Optional<ReleasesResponse> responseOptional = releasesService.getReleases(request);

    // then
    assertThat(responseOptional).isPresent();

    ReleasesResponse response = responseOptional.get();
    assertThat(response.getReleases()).hasSize(1);
    assertThat(response.getReleases().iterator().next()).isEqualTo(DtoFactory.ReleaseDtoFactory.withOneResult("A1", releaseDate));

    verify(mapper, times(1)).writeValueAsString(request);
    verify(restTemplate, times(1)).postForEntity(eq(ALL_RELEASES_URL), any(HttpEntity.class), eq(ReleasesResponse.class));
    String restBaseUrl = verify(releaseButlerConfig, times(1)).getRestBaseUrl();
  }

  @ParameterizedTest(name = "[{index}] => Result <{0}> | HttpStatus <{1}>")
  @MethodSource("responseProvider")
  @DisplayName("getReleases() should return empty result on when butler sends no usable response")
  void get_releases_empty_response(ReleasesResponse responseMock, HttpStatus httpStatus) throws JsonProcessingException {
    // given
    ReleasesRequest request = new ReleasesRequest();
    when(releaseButlerConfig.getRestBaseUrl()).thenReturn(BASE_URL);
    when(restTemplate.postForEntity(eq(ALL_RELEASES_URL), any(HttpEntity.class), eq(ReleasesResponse.class)))
        .thenReturn(ResponseEntity.status(httpStatus).body(responseMock));
    when(mapper.writeValueAsString(request)).thenReturn("body");

    // when
    Optional<ReleasesResponse> responseOptional = releasesService.getReleases(request);

    // then
    assertThat(responseOptional).isEmpty();

    verify(mapper, times(1)).writeValueAsString(request);
    verify(restTemplate, times(1)).postForEntity(eq(ALL_RELEASES_URL), any(HttpEntity.class), eq(ReleasesResponse.class));
    String restBaseUrl = verify(releaseButlerConfig, times(1)).getRestBaseUrl();
  }

  private static Stream<Arguments> responseProvider() {
    ReleasesResponse result = ReleasesResponseFactory.withOneResult("A1", LocalDate.now());
    ReleasesResponse emptyResult = ReleasesResponseFactory.withEmptyResult();
    return Stream.of(
        Arguments.of(null, HttpStatus.OK),
        Arguments.of(result, HttpStatus.BAD_REQUEST),
        Arguments.of(emptyResult, HttpStatus.OK)
    );
  }

  @Test
  @DisplayName("getReleases() should return empty result on parsing error")
  void get_releases_parsing_error() throws JsonProcessingException {
    // given
    ReleasesRequest request = new ReleasesRequest();
    when(mapper.writeValueAsString(request)).thenThrow(new JsonProcessingException("Error"){});

    // when
    Optional<ReleasesResponse> responseOptional = releasesService.getReleases(request);

    // then
    assertThat(responseOptional).isEmpty();
    verify(mapper, times(1)).writeValueAsString(request);
  }

  @Test
  @DisplayName("Test http entity request")
  void test_http_entity() throws JsonProcessingException {
    // given
    ReleasesResponse responseMock = DtoFactory.ReleasesResponseFactory.withOneResult("A1", LocalDate.now());
    ReleasesRequest request = new ReleasesRequest();
    when(releaseButlerConfig.getRestBaseUrl()).thenReturn(BASE_URL);
    when(restTemplate.postForEntity(eq(ALL_RELEASES_URL), any(HttpEntity.class), eq(ReleasesResponse.class)))
        .thenReturn(ResponseEntity.ok(responseMock));
    when(mapper.writeValueAsString(request)).thenReturn("body");

    // when
    releasesService.getReleases(request);

    // then
    verify(restTemplate, times(1)).postForEntity(eq(ALL_RELEASES_URL), argumentCaptor.capture(), eq(ReleasesResponse.class));

    HttpEntity<String> httpEntity = argumentCaptor.getValue();
    HttpHeaders headers = httpEntity.getHeaders();

    assertThat(httpEntity.getBody()).isEqualTo("body");
    assertThat(headers.getAccept()).isEqualTo(Collections.singletonList(MediaType.APPLICATION_JSON));
    assertThat(headers.getContentType()).isEqualByComparingTo(MediaType.APPLICATION_JSON);
    assertThat(headers.getAcceptCharset()).isEqualTo(Collections.singletonList(Charset.defaultCharset()));
  }
}