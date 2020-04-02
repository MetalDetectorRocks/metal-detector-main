package rocks.metaldetector.butler.client;

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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import rocks.metaldetector.butler.ButlerDtoFactory.ButlerReleasesResponseFactory;
import rocks.metaldetector.butler.api.ButlerReleasesRequest;
import rocks.metaldetector.butler.api.ButlerReleasesResponse;
import rocks.metaldetector.support.exceptions.ExternalServiceException;

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ReleaseButlerRestClientImplTest implements WithAssertions {

  private static  final String RELEASES_URL = "http://releases.com";

  @Mock
  private RestTemplate restTemplate;

  private ReleaseButlerRestClientImpl underTest;

  @Captor
  private ArgumentCaptor<HttpEntity<ButlerReleasesRequest>> argumentCaptor;

  @BeforeEach
  void setUp() {
    underTest = new ReleaseButlerRestClientImpl(restTemplate, RELEASES_URL);
  }

  @AfterEach
  void tearDown() {
    reset(restTemplate);
  }

  @Test
  @DisplayName("A POST call is made on the injected URL")
  void test_post_on_url() {
    // given
    ButlerReleasesRequest request = new ButlerReleasesRequest();
    ButlerReleasesResponse responseMock = ButlerReleasesResponseFactory.createDefault();
    doReturn(ResponseEntity.ok(responseMock)).when(restTemplate).postForEntity(anyString(), any(), any());

    // when
    underTest.queryReleases(request);

    // then
    verify(restTemplate, times(1)).postForEntity(eq(RELEASES_URL), any(), any());
  }

  @Test
  @DisplayName("The provided ButlerReleasesRequest is packed into a HttpEntity and sent as POST body")
  void test_request_as_http_entity() {
    // given
    ButlerReleasesRequest request = new ButlerReleasesRequest();
    ButlerReleasesResponse responseMock = ButlerReleasesResponseFactory.createDefault();
    doReturn(ResponseEntity.ok(responseMock)).when(restTemplate).postForEntity(anyString(), any(), any());

    // when
    underTest.queryReleases(request);

    // then
    verify(restTemplate, times(1)).postForEntity(anyString(), argumentCaptor.capture(), any());

    HttpEntity<ButlerReleasesRequest> httpEntity = argumentCaptor.getValue();
    assertThat(httpEntity.getBody()).isEqualTo(request);
    assertThat(httpEntity.getHeaders().getAccept()).isEqualTo(Collections.singletonList(MediaType.APPLICATION_JSON));
    assertThat(httpEntity.getHeaders().getContentType()).isEqualByComparingTo(MediaType.APPLICATION_JSON);
    assertThat(httpEntity.getHeaders().getAcceptCharset()).isEqualTo(Collections.singletonList(Charset.defaultCharset()));
  }

  @Test
  @DisplayName("The body of the result is returned")
  void get_releases_valid_result() {
    // given
    ButlerReleasesRequest request = new ButlerReleasesRequest();
    ButlerReleasesResponse responseMock = ButlerReleasesResponseFactory.createDefault();
    doReturn(ResponseEntity.ok(responseMock)).when(restTemplate).postForEntity(anyString(), any(), any());

    // when
    ButlerReleasesResponse response = underTest.queryReleases(request);

    // then
    assertThat(response).isEqualTo(responseMock);
  }

  @Test
  @DisplayName("If the response is null, a ExternalServiceException is thrown")
  void test_exception_if_response_is_null() {
    // given
    ButlerReleasesRequest request = new ButlerReleasesRequest();
    doReturn(ResponseEntity.ok(null)).when(restTemplate).postForEntity(anyString(), any(), any());

    // when
    Throwable throwable = catchThrowable(() -> underTest.queryReleases(request));

    // then
    assertThat(throwable).isInstanceOf(ExternalServiceException.class);
  }

  @ParameterizedTest(name = "If the status is {0}, a ExternalServiceException is thrown")
  @MethodSource("httpStatusCodeProvider")
  @DisplayName("If the status code is not OK, a ExternalServiceException is thrown")
  void test_exception_if_status_is_not_ok(HttpStatus httpStatus) {
    // given
    ButlerReleasesRequest request = new ButlerReleasesRequest();
    ButlerReleasesResponse responseMock = ButlerReleasesResponseFactory.createDefault();
    doReturn(ResponseEntity.status(httpStatus).body(responseMock)).when(restTemplate).postForEntity(anyString(), any(), any());

    // when
    Throwable throwable = catchThrowable(() -> underTest.queryReleases(request));

    // then
    assertThat(throwable).isInstanceOf(ExternalServiceException.class);
  }

  private static Stream<Arguments> httpStatusCodeProvider() {
    return Stream.of(HttpStatus.values()).filter(status -> !status.is2xxSuccessful()).map(Arguments::of);
  }
}
