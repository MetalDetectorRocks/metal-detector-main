package rocks.metaldetector.butler.client;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestOperations;
import rocks.metaldetector.butler.ButlerDtoFactory.ButlerReleasesResponseFactory;
import rocks.metaldetector.butler.api.ButlerImportCreatedResponse;
import rocks.metaldetector.butler.api.ButlerImportJob;
import rocks.metaldetector.butler.api.ButlerImportResponse;
import rocks.metaldetector.butler.api.ButlerReleaseInfo;
import rocks.metaldetector.butler.api.ButlerReleasesRequest;
import rocks.metaldetector.butler.api.ButlerReleasesResponse;
import rocks.metaldetector.butler.api.ButlerStatisticsResponse;
import rocks.metaldetector.butler.api.ButlerUpdateReleaseStateRequest;
import rocks.metaldetector.butler.config.ButlerConfig;
import rocks.metaldetector.support.exceptions.ExternalServiceException;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpMethod.PUT;
import static rocks.metaldetector.butler.ButlerDtoFactory.ButlerImportResponseFactory;
import static rocks.metaldetector.butler.client.ReleaseButlerRestClientImpl.UPDATE_ENDPOINT_PATH_PARAM;

@ExtendWith(MockitoExtension.class)
class ReleaseButlerRestClientImplTest implements WithAssertions {

  @Mock
  private RestOperations restOperations;

  @Mock
  private ButlerConfig butlerConfig;

  private ReleaseButlerRestClientImpl underTest;

  @Captor
  private ArgumentCaptor<HttpEntity<ButlerReleasesRequest>> argumentCaptorReleases;

  @Captor
  private ArgumentCaptor<HttpEntity<ButlerUpdateReleaseStateRequest>> argumentCaptorReleaseUpdate;

  @BeforeEach
  void setup() {
    underTest = new ReleaseButlerRestClientImpl(restOperations, butlerConfig);
  }

  @AfterEach
  void tearDown() {
    reset(restOperations, butlerConfig);
  }

  @DisplayName("Test of query releases (without pagination)")
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
      doReturn(ResponseEntity.ok(responseMock)).when(restOperations).postForEntity(anyString(), any(), eq(ButlerReleasesResponse.class));

      // when
      underTest.queryAllReleases(request);

      // then
      verify(restOperations).postForEntity(eq(butlerUrl), any(), eq(ButlerReleasesResponse.class));
    }

    @Test
    @DisplayName("The provided ButlerReleasesRequest is packed into a HttpEntity and sent as POST body")
    void test_releases_http_entity() {
      // given
      doReturn("releases-url").when(butlerConfig).getUnpaginatedReleasesUrl();
      ButlerReleasesRequest request = new ButlerReleasesRequest();
      ButlerReleasesResponse responseMock = ButlerReleasesResponseFactory.createDefault();
      doReturn(ResponseEntity.ok(responseMock)).when(restOperations).postForEntity(anyString(), any(), any());

      // when
      underTest.queryAllReleases(request);

      // then
      verify(restOperations).postForEntity(anyString(), argumentCaptorReleases.capture(), any());

      HttpEntity<ButlerReleasesRequest> httpEntity = argumentCaptorReleases.getValue();
      assertThat(httpEntity.getBody()).isEqualTo(request);
    }

    @Test
    @DisplayName("The body of the query result is returned")
    void get_releases_valid_result() {
      // given
      doReturn("releases-url").when(butlerConfig).getUnpaginatedReleasesUrl();
      ButlerReleasesRequest request = new ButlerReleasesRequest();
      ButlerReleasesResponse responseMock = ButlerReleasesResponseFactory.createDefault();
      doReturn(ResponseEntity.ok(responseMock)).when(restOperations).postForEntity(anyString(), any(), any());

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
      doReturn(ResponseEntity.ok(null)).when(restOperations).postForEntity(anyString(), any(), any());

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
      doReturn(ResponseEntity.status(httpStatus).body(responseMock)).when(restOperations).postForEntity(anyString(), any(), any());

      // when
      Throwable throwable = catchThrowable(() -> underTest.queryAllReleases(request));

      // then
      assertThat(throwable).isInstanceOf(ExternalServiceException.class);
    }

    private static Stream<Arguments> httpStatusCodeProvider() {
      return Stream.of(HttpStatus.values()).filter(status -> !status.is2xxSuccessful()).map(Arguments::of);
    }
  }

  @DisplayName("Test of query releases (with pagination)")
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
      doReturn(ResponseEntity.ok(responseMock)).when(restOperations).postForEntity(anyString(), any(), any());

      // when
      underTest.queryReleases(request, null);

      // then
      verify(restOperations).postForEntity(eq(butlerUrl), any(), eq(ButlerReleasesResponse.class));
    }

    @Test
    @DisplayName("Sorting parameter is set if provided")
    void test_sorting_parameter_set() {
      // given
      var butlerUrl = "releases-url";
      var sort = "sort";
      doReturn(butlerUrl).when(butlerConfig).getReleasesUrl();
      ButlerReleasesRequest request = new ButlerReleasesRequest();
      ButlerReleasesResponse responseMock = ButlerReleasesResponseFactory.createDefault();
      doReturn(ResponseEntity.ok(responseMock)).when(restOperations).postForEntity(anyString(), any(), any());

      // when
      underTest.queryReleases(request, sort);

      // then
      verify(restOperations).postForEntity(eq(butlerUrl + "?" + sort), any(), eq(ButlerReleasesResponse.class));
    }

    @Test
    @DisplayName("The provided ButlerReleasesRequest is packed into a HttpEntity and sent as POST body")
    void test_releases_http_entity() {
      // given
      doReturn("releases-url").when(butlerConfig).getReleasesUrl();
      ButlerReleasesRequest request = new ButlerReleasesRequest();
      ButlerReleasesResponse responseMock = ButlerReleasesResponseFactory.createDefault();
      doReturn(ResponseEntity.ok(responseMock)).when(restOperations).postForEntity(anyString(), any(), any());

      // when
      underTest.queryReleases(request, "sort");

      // then
      verify(restOperations).postForEntity(anyString(), argumentCaptorReleases.capture(), any());

      HttpEntity<ButlerReleasesRequest> httpEntity = argumentCaptorReleases.getValue();
      assertThat(httpEntity.getBody()).isEqualTo(request);
    }

    @Test
    @DisplayName("The body of the query result is returned")
    void get_releases_valid_result() {
      // given
      doReturn("releases-url").when(butlerConfig).getReleasesUrl();
      ButlerReleasesRequest request = new ButlerReleasesRequest();
      ButlerReleasesResponse responseMock = ButlerReleasesResponseFactory.createDefault();
      doReturn(ResponseEntity.ok(responseMock)).when(restOperations).postForEntity(anyString(), any(), any());

      // when
      ButlerReleasesResponse response = underTest.queryReleases(request, "sort");

      // then
      assertThat(response).isEqualTo(responseMock);
    }

    @Test
    @DisplayName("If the releases response is null, an ExternalServiceException is thrown")
    void test_exception_if_releases_response_is_null() {
      // given
      doReturn("releases-url").when(butlerConfig).getReleasesUrl();
      ButlerReleasesRequest request = new ButlerReleasesRequest();
      doReturn(ResponseEntity.ok(null)).when(restOperations).postForEntity(anyString(), any(), any());

      // when
      Throwable throwable = catchThrowable(() -> underTest.queryReleases(request, "sort"));

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
      doReturn(ResponseEntity.status(httpStatus).body(responseMock)).when(restOperations).postForEntity(anyString(), any(), any());

      // when
      Throwable throwable = catchThrowable(() -> underTest.queryReleases(request, "sort"));

      // then
      assertThat(throwable).isInstanceOf(ExternalServiceException.class);
    }

    private static Stream<Arguments> httpStatusCodeProvider() {
      return Stream.of(HttpStatus.values()).filter(status -> !status.is2xxSuccessful()).map(Arguments::of);
    }
  }

  @DisplayName("Test of create import jobs")
  @Nested
  class CreateImportJobsTest {

    @Test
    @DisplayName("A POST call is made on import url")
    void test_post_on_import_url() {
      // given
      var butlerUrl = "import-url";
      doReturn(butlerUrl).when(butlerConfig).getImportUrl();
      doReturn(ResponseEntity.ok(new ButlerImportCreatedResponse())).when(restOperations).postForEntity(anyString(), any(), any());

      // when
      underTest.createImportJobs();

      // then
      verify(restOperations).postForEntity(eq(butlerUrl), any(), any());
    }

    @Test
    @DisplayName("The correct response type is set")
    void test_correct_response_type() {
      // given
      doReturn("import-url").when(butlerConfig).getImportUrl();
      doReturn(ResponseEntity.ok(new ButlerImportCreatedResponse())).when(restOperations).postForEntity(anyString(), any(), any());

      // when
      underTest.createImportJobs();

      // then
      verify(restOperations).postForEntity(anyString(), any(), eq(ButlerImportCreatedResponse.class));
    }

    @Test
    @DisplayName("Import url of the butler config is used")
    void test_import_url_from_butler_config_is_used() {
      // given
      doReturn("import-url").when(butlerConfig).getImportUrl();
      doReturn(ResponseEntity.ok(new ButlerImportCreatedResponse())).when(restOperations).postForEntity(anyString(), any(), any());

      // when
      underTest.createImportJobs();

      // then
      verify(butlerConfig).getImportUrl();
    }

    @Test
    @DisplayName("The created jobIds are returned")
    void test_job_ids_returned() {
      // given
      var expectedResponse = ButlerImportCreatedResponse.builder().importJobIds(List.of("1", "2")).build();
      doReturn("import-url").when(butlerConfig).getImportUrl();
      doReturn(ResponseEntity.ok(expectedResponse)).when(restOperations).postForEntity(anyString(), any(), any());

      // when
      var result = underTest.createImportJobs();

      // then
      assertThat(result).isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("If the response is null, an ExternalServiceException is thrown")
    void test_exception_on_null_response() {
      // given
      doReturn("import-url").when(butlerConfig).getImportUrl();
      doReturn(ResponseEntity.ok().build()).when(restOperations).postForEntity(anyString(), any(), any());

      // when
      Throwable throwable = catchThrowable(() -> underTest.createImportJobs());

      // then
      assertThat(throwable).isInstanceOf(ExternalServiceException.class);
    }

    @ParameterizedTest(name = "If the status is {0}, an ExternalServiceException is thrown")
    @MethodSource("httpStatusCodeProvider")
    @DisplayName("If the status code is not OK on import job, an ExternalServiceException is thrown")
    void test_import_job_exception_if_status_is_not_ok(HttpStatus httpStatus) {
      // given
      doReturn("import-url").when(butlerConfig).getImportUrl();
      doReturn(ResponseEntity.status(httpStatus).build()).when(restOperations).postForEntity(anyString(), any(), any());

      // when
      Throwable throwable = catchThrowable(() -> underTest.createImportJobs());

      // then
      assertThat(throwable).isInstanceOf(ExternalServiceException.class);
    }

    private static Stream<Arguments> httpStatusCodeProvider() {
      return Stream.of(HttpStatus.values()).filter(status -> !status.is2xxSuccessful()).map(Arguments::of);
    }
  }

  @DisplayName("Test of create cover download job")
  @Nested
  class CreateCoverDownloadJobTest {

    @Test
    @DisplayName("A POST call is made on cover url")
    void test_post_on_cover_url() {
      // given
      var butlerUrl = "cover-url";
      doReturn(butlerUrl).when(butlerConfig).getRetryCoverDownloadUrl();
      doReturn(ResponseEntity.ok().build()).when(restOperations).postForEntity(anyString(), any(), any());

      // when
      underTest.createRetryCoverDownloadJob();

      // then
      verify(restOperations).postForEntity(eq(butlerUrl), any(), any());
    }

    @Test
    @DisplayName("Cover url of the butler config is used")
    void test_cover_url_from_butler_config_is_used() {
      // given
      doReturn("cover-url").when(butlerConfig).getRetryCoverDownloadUrl();
      doReturn(ResponseEntity.ok().build()).when(restOperations).postForEntity(anyString(), any(), eq(Void.class));

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
      doReturn(ResponseEntity.status(httpStatus).build()).when(restOperations).postForEntity(anyString(), any(), eq(Void.class));

      // when
      Throwable throwable = catchThrowable(() -> underTest.createRetryCoverDownloadJob());

      // then
      assertThat(throwable).isInstanceOf(ExternalServiceException.class);
    }

    private static Stream<Arguments> httpStatusCodeProvider() {
      return Stream.of(HttpStatus.values()).filter(status -> !status.is2xxSuccessful()).map(Arguments::of);
    }
  }

  @DisplayName("Test of query all import jobs")
  @Nested
  class QueryImportJobsTest {

    @Test
    @DisplayName("A GET call is made on the injected import URL")
    void test_get_on_import_url() {
      // given
      var butlerUrl = "import-url";
      doReturn(butlerUrl).when(butlerConfig).getImportUrl();
      ButlerImportResponse responseMock = ButlerImportResponseFactory.createDefault();
      doReturn(ResponseEntity.ok(responseMock)).when(restOperations).getForEntity(anyString(), any());

      // when
      underTest.queryImportJobs();

      // then
      verify(restOperations).getForEntity(eq(butlerUrl), eq(ButlerImportResponse.class));
    }

    @Test
    @DisplayName("The body of the import job result is returned")
    void get_import_valid_result() {
      // given
      ButlerImportResponse responseMock = ButlerImportResponseFactory.createDefault();
      doReturn("import-url").when(butlerConfig).getImportUrl();
      doReturn(ResponseEntity.ok(responseMock)).when(restOperations).getForEntity(anyString(), any());

      // when
      List<ButlerImportJob> response = underTest.queryImportJobs();

      // then
      assertThat(response).isEqualTo(responseMock.getImportJobs());
    }

    @Test
    @DisplayName("If the import job response is null, an ExternalServiceException is thrown")
    void test_exception_if_import_response_is_null() {
      // given
      doReturn("import-url").when(butlerConfig).getImportUrl();
      doReturn(ResponseEntity.ok(null)).when(restOperations).getForEntity(anyString(), any());

      // when
      Throwable throwable = catchThrowable(() -> underTest.queryImportJobs());

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
      doReturn(ResponseEntity.status(httpStatus).body(responseMock)).when(restOperations).getForEntity(anyString(), any());

      // when
      Throwable throwable = catchThrowable(() -> underTest.queryImportJobs());

      // then
      assertThat(throwable).isInstanceOf(ExternalServiceException.class);
    }

    private static Stream<Arguments> httpStatusCodeProvider() {
      return Stream.of(HttpStatus.values()).filter(status -> !status.is2xxSuccessful()).map(Arguments::of);
    }
  }

  @DisplayName("Test of query import job")
  @Nested
  class QueryImportJobTest {

    @Test
    @DisplayName("A GET call is made on the injected import URL")
    void test_get_on_import_url() {
      // given
      var butlerUrl = "import-url";
      var importId = "666";
      doReturn(butlerUrl).when(butlerConfig).getImportUrl();
      ButlerImportJob responseMock = ButlerImportJob.builder().state("Success").build();
      doReturn(ResponseEntity.ok(responseMock)).when(restOperations).getForEntity(anyString(), any());

      // when
      underTest.queryImportJob(importId);

      // then
      verify(restOperations).getForEntity(eq(butlerUrl + "/" + importId), eq(ButlerImportJob.class));
    }

    @Test
    @DisplayName("The body of the import job result is returned")
    void get_import_valid_result() {
      // given
      ButlerImportJob responseMock = ButlerImportJob.builder().state("Success").build();
      doReturn("import-url").when(butlerConfig).getImportUrl();
      doReturn(ResponseEntity.ok(responseMock)).when(restOperations).getForEntity(anyString(), any());

      // when
      ButlerImportJob response = underTest.queryImportJob("666");

      // then
      assertThat(response).isEqualTo(responseMock);
    }

    @Test
    @DisplayName("If the import job is null, an ExternalServiceException is thrown")
    void test_exception_if_import_response_is_null() {
      // given
      doReturn("import-url").when(butlerConfig).getImportUrl();
      doReturn(ResponseEntity.ok(null)).when(restOperations).getForEntity(anyString(), any());

      // when
      Throwable throwable = catchThrowable(() -> underTest.queryImportJob("666"));

      // then
      assertThat(throwable).isInstanceOf(ExternalServiceException.class);
    }

    @ParameterizedTest(name = "If the status is {0}, an ExternalServiceException is thrown")
    @MethodSource("httpStatusCodeProvider")
    @DisplayName("If the status code is not OK on import job, an ExternalServiceException is thrown")
    void test_import_exception_if_status_is_not_ok(HttpStatus httpStatus) {
      // given
      ButlerImportJob responseMock = ButlerImportJob.builder().state("Success").build();
      doReturn("import-url").when(butlerConfig).getImportUrl();
      doReturn(ResponseEntity.status(httpStatus).body(responseMock)).when(restOperations).getForEntity(anyString(), any());

      // when
      Throwable throwable = catchThrowable(() -> underTest.queryImportJob("666"));

      // then
      assertThat(throwable).isInstanceOf(ExternalServiceException.class);
    }

    private static Stream<Arguments> httpStatusCodeProvider() {
      return Stream.of(HttpStatus.values()).filter(status -> !status.is2xxSuccessful()).map(Arguments::of);
    }
  }

  @DisplayName("Test updating a release")
  @Nested
  class UpdateReleaseTest {

    @Test
    @DisplayName("update release url is called")
    void test_update_release_url_called() {
      // given
      var url = "url";
      doReturn(url).when(butlerConfig).getReleasesUrl();
      doReturn(ResponseEntity.ok().build()).when(restOperations).exchange(anyString(), any(), any(), eq(Void.class), anyMap());

      // when
      underTest.updateReleaseState(1L, "state");

      // then
      verify(restOperations).exchange(eq(url + UPDATE_ENDPOINT_PATH_PARAM), any(), any(), eq(Void.class), anyMap());
    }

    @Test
    @DisplayName("PUT is made")
    void test_put_is_made() {
      // given
      doReturn("url").when(butlerConfig).getReleasesUrl();
      doReturn(ResponseEntity.ok().build()).when(restOperations).exchange(anyString(), any(), any(), eq(Void.class), anyMap());

      // when
      underTest.updateReleaseState(1L, "state");

      // then
      verify(restOperations).exchange(anyString(), eq(PUT), any(), eq(Void.class), anyMap());
    }

    @Test
    @DisplayName("httpEntity contains body")
    void test_http_entity_contains_body() {
      // given
      var releaseId = 1L;
      var state = "OK";
      doReturn("url").when(butlerConfig).getReleasesUrl();
      doReturn(ResponseEntity.ok().build()).when(restOperations).exchange(anyString(), any(), any(), eq(Void.class), anyMap());

      // when
      underTest.updateReleaseState(releaseId, state);

      // then
      verify(restOperations).exchange(anyString(), any(), argumentCaptorReleaseUpdate.capture(), eq(Void.class), anyMap());
      ButlerUpdateReleaseStateRequest body = argumentCaptorReleaseUpdate.getValue().getBody();
      assertThat(body).isNotNull();
      assertThat(body.getState()).isEqualTo(state);
    }

    @Test
    @DisplayName("return type is void")
    void test_return_type_void() {
      // given
      doReturn("url").when(butlerConfig).getReleasesUrl();
      doReturn(ResponseEntity.ok().build()).when(restOperations).exchange(anyString(), any(), any(), eq(Void.class), anyMap());

      // when
      underTest.updateReleaseState(1L, "state");

      // then
      verify(restOperations).exchange(anyString(), any(), any(), eq(Void.class), anyMap());
    }

    @Test
    @DisplayName("map with path parameter is set")
    void test_path_param_set() {
      // given
      var releaseId = 1L;
      var expectedMap = Map.of("releaseId", releaseId);
      doReturn("url").when(butlerConfig).getReleasesUrl();
      doReturn(ResponseEntity.ok().build()).when(restOperations).exchange(anyString(), any(), any(), eq(Void.class), anyMap());

      // when
      underTest.updateReleaseState(releaseId, "state");

      // then
      verify(restOperations).exchange(anyString(), any(), any(), eq(Void.class), eq(expectedMap));
    }

    @ParameterizedTest(name = "If the status is {0}, an ExternalServiceException is thrown")
    @MethodSource("httpStatusCodeProvider")
    @DisplayName("If the status code is not OK on update release, an ExternalServiceException is thrown")
    void test_update_exception_if_status_is_not_ok(HttpStatus httpStatus) {
      // given
      doReturn("url").when(butlerConfig).getReleasesUrl();
      doReturn(ResponseEntity.status(httpStatus).build()).when(restOperations).exchange(anyString(), any(), any(), eq(Void.class), anyMap());

      // when
      Throwable throwable = catchThrowable(() -> underTest.updateReleaseState(1L, "state"));

      // then
      assertThat(throwable).isInstanceOf(ExternalServiceException.class);
    }

    private static Stream<Arguments> httpStatusCodeProvider() {
      return Stream.of(HttpStatus.values()).filter(status -> !status.is2xxSuccessful()).map(Arguments::of);
    }
  }

  @DisplayName("Tests getting statistics")
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  @Nested
  class StatisticsTest {

    @Test
    @DisplayName("restTemplate is called with correct url")
    void test_rest_template_called() {
      // given
      var url = "url";
      doReturn(url).when(butlerConfig).getStatisticsUrl();
      doReturn(ResponseEntity.ok(ButlerStatisticsResponse.builder().build())).when(restOperations).getForEntity(anyString(), eq(ButlerStatisticsResponse.class));

      // when
      underTest.getStatistics();

      // then
      verify(restOperations).getForEntity(eq(url), eq(ButlerStatisticsResponse.class));
    }

    @Test
    @DisplayName("response is returned")
    void test_response_returned() {
      // given
      var expectedResponse = ButlerStatisticsResponse.builder()
          .releaseInfo(ButlerReleaseInfo.builder().build())
          .build();
      doReturn("url").when(butlerConfig).getStatisticsUrl();
      doReturn(ResponseEntity.ok(expectedResponse)).when(restOperations).getForEntity(anyString(), eq(ButlerStatisticsResponse.class));

      // when
      var result = underTest.getStatistics();

      // then
      assertThat(result).isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("if the status code is not OK on getting statistics, an ExternalServiceException is thrown")
    void test_exception_response_null() {
      // given
      doReturn("url").when(butlerConfig).getStatisticsUrl();
      doReturn(ResponseEntity.ok().build()).when(restOperations).getForEntity(anyString(), eq(ButlerStatisticsResponse.class));

      // when
      Throwable throwable = catchThrowable(() -> underTest.getStatistics());

      // then
      assertThat(throwable).isInstanceOf(ExternalServiceException.class);
    }

    @ParameterizedTest(name = "if the status is {0}, an ExternalServiceException is thrown")
    @MethodSource("httpStatusCodeProvider")
    @DisplayName("if the status code is not OK on getting statistics, an ExternalServiceException is thrown")
    void test_statistics_exception_if_status_is_not_ok(HttpStatus httpStatus) {
      // given
      doReturn("url").when(butlerConfig).getStatisticsUrl();
      doReturn(ResponseEntity.status(httpStatus).body(ButlerStatisticsResponse.builder().build())).when(restOperations).getForEntity(anyString(), eq(ButlerStatisticsResponse.class));

      // when
      Throwable throwable = catchThrowable(() -> underTest.getStatistics());

      // then
      assertThat(throwable).isInstanceOf(ExternalServiceException.class);
    }

    private static Stream<Arguments> httpStatusCodeProvider() {
      return Stream.of(HttpStatus.values()).filter(status -> !status.is2xxSuccessful()).map(Arguments::of);
    }
  }
}
