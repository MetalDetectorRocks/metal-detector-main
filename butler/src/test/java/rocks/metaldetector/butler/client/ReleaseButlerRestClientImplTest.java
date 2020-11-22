package rocks.metaldetector.butler.client;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import rocks.metaldetector.butler.ButlerDtoFactory.ButlerReleasesResponseFactory;
import rocks.metaldetector.butler.api.ButlerImportJob;
import rocks.metaldetector.butler.api.ButlerImportResponse;
import rocks.metaldetector.butler.api.ButlerReleasesRequest;
import rocks.metaldetector.butler.api.ButlerReleasesResponse;
import rocks.metaldetector.butler.config.ButlerConfig;
import rocks.metaldetector.support.exceptions.ExternalServiceException;

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static rocks.metaldetector.butler.ButlerDtoFactory.ButlerImportResponseFactory;

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

  @AfterEach
  void tearDown() {
    reset(restTemplate, butlerConfig);
  }

  @DisplayName("Test of query releases (without pagination)")
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  @Nested
  class QueryAllReleasesTest {

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
      underTest.queryAllReleases(request);

      // then
      verify(restTemplate).postForEntity(eq(butlerUrl), any(), eq(ButlerReleasesResponse.class));
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
      underTest.queryAllReleases(request);

      // then
      verify(restTemplate).postForEntity(anyString(), argumentCaptorReleases.capture(), any());

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
      ButlerReleasesResponse response = underTest.queryAllReleases(request);

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
      Throwable throwable = catchThrowable(() -> underTest.queryAllReleases(request));

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
      Throwable throwable = catchThrowable(() -> underTest.queryAllReleases(request));

      // then
      assertThat(throwable).isInstanceOf(ExternalServiceException.class);
    }

    private Stream<Arguments> httpStatusCodeProvider() {
      return Stream.of(HttpStatus.values()).filter(status -> !status.is2xxSuccessful()).map(Arguments::of);
    }
  }

  @DisplayName("Test of query releases (with pagination)")
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  @Nested
  class QueryReleasesTest {

    @Test
    @DisplayName("A POST call is made on the injected releases URL")
    void test_post_on_releases_url() {
      // given
      var butlerUrl = "releases-url";
      doReturn(butlerUrl).when(butlerConfig).getReleasesUrl();
      ButlerReleasesRequest request = new ButlerReleasesRequest();
      ButlerReleasesResponse responseMock = ButlerReleasesResponseFactory.createDefault();
      doReturn(ResponseEntity.ok(responseMock)).when(restTemplate).postForEntity(anyString(), any(), any());

      // when
      underTest.queryReleases(request);

      // then
      verify(restTemplate).postForEntity(eq(butlerUrl), any(), eq(ButlerReleasesResponse.class));
    }

    @Test
    @DisplayName("Sorting parameter is set if provided")
    void test_sorting_parameter_set() {
      // given
      var butlerUrl = "releases-url";
      doReturn(butlerUrl).when(butlerConfig).getReleasesUrl();
      ButlerReleasesRequest request = new ButlerReleasesRequest();
      request.setSorting("sortingParameter");
      ButlerReleasesResponse responseMock = ButlerReleasesResponseFactory.createDefault();
      doReturn(ResponseEntity.ok(responseMock)).when(restTemplate).postForEntity(anyString(), any(), any());

      // when
      underTest.queryReleases(request);

      // then
      verify(restTemplate).postForEntity(eq(butlerUrl + "?" + request.getSorting()), any(), eq(ButlerReleasesResponse.class));
    }

    @Test
    @DisplayName("The provided ButlerReleasesRequest is packed into a HttpEntity and sent as POST body")
    void test_releases_http_entity() {
      // given
      doReturn("releases-url").when(butlerConfig).getReleasesUrl();
      ButlerReleasesRequest request = new ButlerReleasesRequest();
      ButlerReleasesResponse responseMock = ButlerReleasesResponseFactory.createDefault();
      doReturn(ResponseEntity.ok(responseMock)).when(restTemplate).postForEntity(anyString(), any(), any());

      // when
      underTest.queryReleases(request);

      // then
      verify(restTemplate).postForEntity(anyString(), argumentCaptorReleases.capture(), any());

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
      doReturn("releases-url").when(butlerConfig).getReleasesUrl();
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
      doReturn("releases-url").when(butlerConfig).getReleasesUrl();
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
      doReturn("releases-url").when(butlerConfig).getReleasesUrl();
      ButlerReleasesRequest request = new ButlerReleasesRequest();
      ButlerReleasesResponse responseMock = ButlerReleasesResponseFactory.createDefault();
      doReturn(ResponseEntity.status(httpStatus).body(responseMock)).when(restTemplate).postForEntity(anyString(), any(), any());

      // when
      Throwable throwable = catchThrowable(() -> underTest.queryReleases(request));

      // then
      assertThat(throwable).isInstanceOf(ExternalServiceException.class);
    }

    private Stream<Arguments> httpStatusCodeProvider() {
      return Stream.of(HttpStatus.values()).filter(status -> !status.is2xxSuccessful()).map(Arguments::of);
    }
  }

  @DisplayName("Test of create import job")
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  @Nested
  class CreateImportJobTest {

    @Test
    @DisplayName("A POST call is made on import url")
    void test_post_on_import_url() {
      // given
      var butlerUrl = "import-url";
      doReturn(butlerUrl).when(butlerConfig).getImportUrl();
      doReturn(ResponseEntity.ok().build()).when(restTemplate).postForEntity(anyString(), any(), any());

      // when
      underTest.createImportJob();

      // then
      verify(restTemplate).postForEntity(eq(butlerUrl), any(), any());
    }

    @Test
    @DisplayName("Import url of the butler config is used")
    void test_import_url_from_butler_config_is_used() {
      // given
      doReturn("import-url").when(butlerConfig).getImportUrl();
      doReturn(ResponseEntity.ok().build()).when(restTemplate).postForEntity(anyString(), any(), eq(Void.class));

      // when
      underTest.createImportJob();

      // then
      verify(butlerConfig).getImportUrl();
    }

    @ParameterizedTest(name = "If the status is {0}, an ExternalServiceException is thrown")
    @MethodSource("httpStatusCodeProvider")
    @DisplayName("If the status code is not OK on import job, an ExternalServiceException is thrown")
    void test_import_job_exception_if_status_is_not_ok(HttpStatus httpStatus) {
      // given
      doReturn("import-url").when(butlerConfig).getImportUrl();
      doReturn(ResponseEntity.status(httpStatus).build()).when(restTemplate).postForEntity(anyString(), any(), eq(Void.class));

      // when
      Throwable throwable = catchThrowable(() -> underTest.createImportJob());

      // then
      assertThat(throwable).isInstanceOf(ExternalServiceException.class);
    }

    private Stream<Arguments> httpStatusCodeProvider() {
      return Stream.of(HttpStatus.values()).filter(status -> !status.is2xxSuccessful()).map(Arguments::of);
    }
  }

  @DisplayName("Test of create cover download job")
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  @Nested
  class CreateCoverDownloadJobTest {

    @Test
    @DisplayName("A POST call is made on cover url")
    void test_post_on_cover_url() {
      // given
      var butlerUrl = "cover-url";
      doReturn(butlerUrl).when(butlerConfig).getRetryCoverDownloadUrl();
      doReturn(ResponseEntity.ok().build()).when(restTemplate).postForEntity(anyString(), any(), any());

      // when
      underTest.createRetryCoverDownloadJob();

      // then
      verify(restTemplate).postForEntity(eq(butlerUrl), any(), any());
    }

    @Test
    @DisplayName("Cover url of the butler config is used")
    void test_cover_url_from_butler_config_is_used() {
      // given
      doReturn("cover-url").when(butlerConfig).getRetryCoverDownloadUrl();
      doReturn(ResponseEntity.ok().build()).when(restTemplate).postForEntity(anyString(), any(), eq(Void.class));

      // when
      underTest.createRetryCoverDownloadJob();

      // then
      verify(butlerConfig).getRetryCoverDownloadUrl();
    }

    @ParameterizedTest(name = "If the status is {0}, an ExternalServiceException is thrown")
    @MethodSource("httpStatusCodeProvider")
    @DisplayName("If the status code is not OK on cover download job, an ExternalServiceException is thrown")
    void test_cover_download_job_exception_if_status_is_not_ok(HttpStatus httpStatus) {
      // given
      doReturn("cover-url").when(butlerConfig).getRetryCoverDownloadUrl();
      doReturn(ResponseEntity.status(httpStatus).build()).when(restTemplate).postForEntity(anyString(), any(), eq(Void.class));

      // when
      Throwable throwable = catchThrowable(() -> underTest.createRetryCoverDownloadJob());

      // then
      assertThat(throwable).isInstanceOf(ExternalServiceException.class);
    }

    private Stream<Arguments> httpStatusCodeProvider() {
      return Stream.of(HttpStatus.values()).filter(status -> !status.is2xxSuccessful()).map(Arguments::of);
    }
  }

  @DisplayName("Test of query import job results")
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  @Nested
  class QueryImportJobResultsTest {

    @Test
    @DisplayName("A GET call is made on the injected import URL")
    void test_get_on_import_url() {
      // given
      var butlerUrl = "import-url";
      doReturn(butlerUrl).when(butlerConfig).getImportUrl();
      ButlerImportResponse responseMock = ButlerImportResponseFactory.createDefault();
      doReturn(ResponseEntity.ok(responseMock)).when(restTemplate).getForEntity(anyString(), any());

      // when
      underTest.queryImportJobResults();

      // then
      verify(restTemplate).getForEntity(eq(butlerUrl), eq(ButlerImportResponse.class));
    }

    @Test
    @DisplayName("The body of the import job result is returned")
    void get_import_valid_result() {
      // given
      ButlerImportResponse responseMock = ButlerImportResponseFactory.createDefault();
      doReturn("import-url").when(butlerConfig).getImportUrl();
      doReturn(ResponseEntity.ok(responseMock)).when(restTemplate).getForEntity(anyString(), any());

      // when
      List<ButlerImportJob> response = underTest.queryImportJobResults();

      // then
      assertThat(response).isEqualTo(responseMock.getImportJobs());
    }

    @Test
    @DisplayName("If the import job response is null, an ExternalServiceException is thrown")
    void test_exception_if_import_response_is_null() {
      // given
      doReturn("import-url").when(butlerConfig).getImportUrl();
      doReturn(ResponseEntity.ok(null)).when(restTemplate).getForEntity(anyString(), any());

      // when
      Throwable throwable = catchThrowable(() -> underTest.queryImportJobResults());

      // then
      assertThat(throwable).isInstanceOf(ExternalServiceException.class);
    }

    @ParameterizedTest(name = "If the status is {0}, an ExternalServiceException is thrown")
    @MethodSource("httpStatusCodeProvider")
    @DisplayName("If the status code is not OK on import job, an ExternalServiceException is thrown")
    void test_import_exception_if_status_is_not_ok(HttpStatus httpStatus) {
      // given
      ButlerImportResponse responseMock = ButlerImportResponseFactory.createDefault();
      doReturn("import-url").when(butlerConfig).getImportUrl();
      doReturn(ResponseEntity.status(httpStatus).body(responseMock)).when(restTemplate).getForEntity(anyString(), any());

      // when
      Throwable throwable = catchThrowable(() -> underTest.queryImportJobResults());

      // then
      assertThat(throwable).isInstanceOf(ExternalServiceException.class);
    }

    private Stream<Arguments> httpStatusCodeProvider() {
      return Stream.of(HttpStatus.values()).filter(status -> !status.is2xxSuccessful()).map(Arguments::of);
    }
  }
}
