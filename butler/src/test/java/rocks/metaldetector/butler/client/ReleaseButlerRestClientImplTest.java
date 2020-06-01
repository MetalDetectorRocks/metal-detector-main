package rocks.metaldetector.butler.client;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
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
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import rocks.metaldetector.butler.ButlerDtoFactory.ButlerImportJobResponseFactory;
import rocks.metaldetector.butler.ButlerDtoFactory.ButlerReleasesResponseFactory;
import rocks.metaldetector.butler.api.ButlerImportJobResponse;
import rocks.metaldetector.butler.api.ButlerReleasesRequest;
import rocks.metaldetector.butler.api.ButlerReleasesResponse;
import rocks.metaldetector.butler.config.ButlerConfig;
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

  @Mock
  private RestTemplate restTemplate;

  @Mock
  private ButlerConfig butlerConfig;

  @InjectMocks
  private ReleaseButlerRestClientImpl underTest;

  @Captor
  private ArgumentCaptor<HttpEntity<ButlerReleasesRequest>> argumentCaptorReleases;

  @Captor
  private ArgumentCaptor<HttpEntity<ButlerReleasesRequest>> argumentCaptorImport;

  @AfterEach
  void tearDown() {
    reset(restTemplate, butlerConfig);
  }

  @Test
  @DisplayName("A POST call is made on the injected releases URL")
  void test_post_on_releases_url() {
    // given
    var butlerUrl = "releases-url";
    doReturn(butlerUrl).when(butlerConfig).getUnpaginatedReleasesUrl();
    ButlerReleasesRequest request = new ButlerReleasesRequest();
    ButlerReleasesResponse responseMock = ButlerReleasesResponseFactory.createDefault();
    doReturn(ResponseEntity.ok(responseMock)).when(restTemplate).postForEntity(anyString(), any(), eq(ButlerReleasesResponse.class));

    // when
    underTest.queryReleases(request);

    // then
    verify(restTemplate, times(1)).postForEntity(eq(butlerUrl), any(), eq(ButlerReleasesResponse.class));
  }

  @Test
  @DisplayName("The provided ButlerReleasesRequest is packed into a HttpEntity and sent as POST body")
  void test_releases_http_entity() {
    // given
    doReturn("releases-url").when(butlerConfig).getUnpaginatedReleasesUrl();
    ButlerReleasesRequest request = new ButlerReleasesRequest();
    ButlerReleasesResponse responseMock = ButlerReleasesResponseFactory.createDefault();
    doReturn(ResponseEntity.ok(responseMock)).when(restTemplate).postForEntity(anyString(), any(), any());

    // when
    underTest.queryReleases(request);

    // then
    verify(restTemplate, times(1)).postForEntity(anyString(), argumentCaptorReleases.capture(), any());

    HttpEntity<ButlerReleasesRequest> httpEntity = argumentCaptorReleases.getValue();
    assertThat(httpEntity.getBody()).isEqualTo(request);
    assertThat(httpEntity.getHeaders().getAccept()).isEqualTo(Collections.singletonList(MediaType.APPLICATION_JSON));
    assertThat(httpEntity.getHeaders().getContentType()).isEqualByComparingTo(MediaType.APPLICATION_JSON);
    assertThat(httpEntity.getHeaders().getAcceptCharset()).isEqualTo(Collections.singletonList(Charset.defaultCharset()));
  }

  @Test
  @DisplayName("The body of the query result is returned")
  void get_releases_valid_result() {
    // given
    doReturn("releases-url").when(butlerConfig).getUnpaginatedReleasesUrl();
    ButlerReleasesRequest request = new ButlerReleasesRequest();
    ButlerReleasesResponse responseMock = ButlerReleasesResponseFactory.createDefault();
    doReturn(ResponseEntity.ok(responseMock)).when(restTemplate).postForEntity(anyString(), any(), any());

    // when
    ButlerReleasesResponse response = underTest.queryReleases(request);

    // then
    assertThat(response).isEqualTo(responseMock);
  }

  @Test
  @DisplayName("If the releases response is null, an ExternalServiceException is thrown")
  void test_exception_if_releases_response_is_null() {
    // given
    doReturn("releases-url").when(butlerConfig).getUnpaginatedReleasesUrl();
    ButlerReleasesRequest request = new ButlerReleasesRequest();
    doReturn(ResponseEntity.ok(null)).when(restTemplate).postForEntity(anyString(), any(), any());

    // when
    Throwable throwable = catchThrowable(() -> underTest.queryReleases(request));

    // then
    assertThat(throwable).isInstanceOf(ExternalServiceException.class);
  }

  @ParameterizedTest(name = "If the status is {0}, an ExternalServiceException is thrown")
  @MethodSource("httpStatusCodeProvider")
  @DisplayName("If the status code is not OK on query, an ExternalServiceException is thrown")
  void test_releases_exception_if_status_is_not_ok(HttpStatus httpStatus) {
    // given
    doReturn("releases-url").when(butlerConfig).getUnpaginatedReleasesUrl();
    ButlerReleasesRequest request = new ButlerReleasesRequest();
    ButlerReleasesResponse responseMock = ButlerReleasesResponseFactory.createDefault();
    doReturn(ResponseEntity.status(httpStatus).body(responseMock)).when(restTemplate).postForEntity(anyString(), any(), any());

    // when
    Throwable throwable = catchThrowable(() -> underTest.queryReleases(request));

    // then
    assertThat(throwable).isInstanceOf(ExternalServiceException.class);
  }

  @Test
  @DisplayName("A GET call is made")
  void test_get() {
    // given
    ButlerImportJobResponse responseMock = ButlerImportJobResponseFactory.createDefault();
    doReturn(ResponseEntity.ok(responseMock)).when(restTemplate).exchange(anyString(), any(), any(), any(Class.class), anyString());

    // when
    underTest.createImportJob();

    // then
    verify(restTemplate, times(1)).exchange(anyString(), eq(HttpMethod.GET), any(), any(Class.class), anyString());
  }

  @Test
  @DisplayName("A GET call is made on the injected import URL with correct path parameter")
  void test_get_on_import_url() {
    // given
    var butlerUrl = "import-url";
    doReturn(butlerUrl).when(butlerConfig).getImportUrl();
    ButlerImportJobResponse responseMock = ButlerImportJobResponseFactory.createDefault();
    doReturn(ResponseEntity.ok(responseMock)).when(restTemplate).exchange(anyString(), any(), any(), any(Class.class), anyString());
    String actionPathParam = "?action={action}";
    String importAction = "import";

    // when
    underTest.createImportJob();

    // then
    verify(restTemplate, times(1)).exchange(eq(butlerUrl + actionPathParam), any(), any(), eq(ButlerImportJobResponse.class), eq(importAction));
  }

  @Test
  @DisplayName("The HttpEntity should contain the correct headers")
  void test_import_http_entity() {
    // given
    ButlerImportJobResponse responseMock = ButlerImportJobResponseFactory.createDefault();
    doReturn(ResponseEntity.ok(responseMock)).when(restTemplate).exchange(anyString(), any(), any(), any(Class.class), anyString());

    // when
    underTest.createImportJob();

    // then
    verify(restTemplate, times(1)).exchange(anyString(), any(), argumentCaptorImport.capture(), any(Class.class), anyString());

    HttpEntity<ButlerReleasesRequest> httpEntity = argumentCaptorImport.getValue();
    assertThat(httpEntity.getHeaders().getAccept()).isEqualTo(Collections.singletonList(MediaType.APPLICATION_JSON));
    assertThat(httpEntity.getHeaders().getAcceptCharset()).isEqualTo(Collections.singletonList(Charset.defaultCharset()));
  }

  @Test
  @DisplayName("The body of the import job result is returned")
  void get_import_valid_result() {
    // given
    ButlerImportJobResponse responseMock = ButlerImportJobResponseFactory.createDefault();
    doReturn(ResponseEntity.ok(responseMock)).when(restTemplate).exchange(anyString(), any(), any(), any(Class.class), anyString());

    // when
    ButlerImportJobResponse response = underTest.createImportJob();

    // then
    assertThat(response).isEqualTo(responseMock);
  }

  @Test
  @DisplayName("If the import job response is null, an ExternalServiceException is thrown")
  void test_exception_if_import_response_is_null() {
    // given
    doReturn(ResponseEntity.ok(null)).when(restTemplate).exchange(anyString(), any(), any(), any(Class.class), anyString());

    // when
    Throwable throwable = catchThrowable(() -> underTest.createImportJob());

    // then
    assertThat(throwable).isInstanceOf(ExternalServiceException.class);
  }

  @ParameterizedTest(name = "If the status is {0}, an ExternalServiceException is thrown")
  @MethodSource("httpStatusCodeProvider")
  @DisplayName("If the status code is not OK on import job, an ExternalServiceException is thrown")
  void test_import_exception_if_status_is_not_ok(HttpStatus httpStatus) {
    // given
    ButlerImportJobResponse responseMock = ButlerImportJobResponseFactory.createDefault();
    doReturn(ResponseEntity.status(httpStatus).body(responseMock)).when(restTemplate).exchange(anyString(), any(), any(), any(Class.class), anyString());

    // when
    Throwable throwable = catchThrowable(() -> underTest.createImportJob());

    // then
    assertThat(throwable).isInstanceOf(ExternalServiceException.class);
  }

  private static Stream<Arguments> httpStatusCodeProvider() {
    return Stream.of(HttpStatus.values()).filter(status -> !status.is2xxSuccessful()).map(Arguments::of);
  }
}
