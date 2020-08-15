package rocks.metaldetector.web.controller.rest;

import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import io.restassured.module.mockmvc.response.ValidatableMockMvcResponse;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.metaldetector.butler.facade.ReleaseService;
import rocks.metaldetector.butler.facade.dto.ImportJobResultDto;
import rocks.metaldetector.config.constants.Endpoints;
import rocks.metaldetector.service.exceptions.RestExceptionsHandler;
import rocks.metaldetector.testutil.DtoFactory.DetectorReleaseRequestFactory;
import rocks.metaldetector.testutil.DtoFactory.ImportJobResultDtoFactory;
import rocks.metaldetector.testutil.DtoFactory.ReleaseDtoFactory;
import rocks.metaldetector.web.RestAssuredMockMvcUtils;
import rocks.metaldetector.web.api.request.DetectorReleasesRequest;
import rocks.metaldetector.web.api.response.DetectorReleasesResponse;
import rocks.metaldetector.web.transformer.DetectorReleasesResponseTransformer;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@ExtendWith(MockitoExtension.class)
class ReleasesRestControllerTest implements WithAssertions {

  @Mock
  private ReleaseService releasesService;

  @Mock
  private DetectorReleasesResponseTransformer releasesResponseTransformer;

  @InjectMocks
  private ReleasesRestController underTest;

  @BeforeEach
  void setUp() {
    RestAssuredMockMvc.standaloneSetup(underTest,
                                       springSecurity((request, response, chain) -> chain.doFilter(request, response)),
                                       RestExceptionsHandler.class);
  }

  @AfterEach
  void tearDown() {
    reset(releasesService, releasesResponseTransformer);
  }

  @Nested
  @DisplayName("Tests for endpoint '" + Endpoints.Rest.QUERY_RELEASES + "'")
  class QueryReleasesTest {

    private RestAssuredMockMvcUtils restAssuredUtils;

    @BeforeEach
    void setUp() {
      restAssuredUtils = new RestAssuredMockMvcUtils(Endpoints.Rest.QUERY_RELEASES);
    }

    @Test
    @DisplayName("Should pass query request parameter to release service")
    void should_pass_query_parameter_to_release_service() {
      // given
      DetectorReleasesRequest request = DetectorReleaseRequestFactory.createDefault();

      // when
      restAssuredUtils.doPost(request);

      // then
      verify(releasesService, times(1)).findAllReleases(request.getArtists(), request.getDateFrom(), request.getDateTo());
    }

    @Test
    @DisplayName("Should use transformer to transform releases to a list of DetectorReleasesResponse")
    void should_use_releases_transformer() {
      // given
      var request = DetectorReleaseRequestFactory.createDefault();
      var releases = List.of(ReleaseDtoFactory.createDefault());
      doReturn(releases).when(releasesService).findAllReleases(any(), any(), any());

      // when
      restAssuredUtils.doPost(request);

      // then
      verify(releasesResponseTransformer, times(1)).transformListOf(releases);
    }

    @Test
    @DisplayName("Should return the transformed releases response")
    void should_return_releases() {
      // given
      var request = DetectorReleaseRequestFactory.createDefault();
      var transformedResponse = List.of(new DetectorReleasesResponse());
      doReturn(Collections.emptyList()).when(releasesService).findAllReleases(any(), any(), any());
      doReturn(transformedResponse).when(releasesResponseTransformer).transformListOf(any());

      // when
      var validatableResponse = restAssuredUtils.doPost(request);

      // then
      validatableResponse
          .contentType(ContentType.JSON)
          .statusCode(OK.value());

      var result = validatableResponse.extract().as(DetectorReleasesResponse[].class);
      assertThat(Arrays.asList(result)).isEqualTo(transformedResponse);
    }

    @Test
    @DisplayName("Should return 400 on invalid query request")
    void test_invalid_query_requests() {
      // given
      DetectorReleasesRequest request = new DetectorReleasesRequest(LocalDate.now(), LocalDate.now().plusDays(1), null);

      // when
      var validatableResponse = restAssuredUtils.doPost(request);

      // then
      validatableResponse
          .contentType(ContentType.JSON)
          .statusCode(BAD_REQUEST.value());
    }
  }

  @Nested
  @DisplayName("Tests creating an import job")
  class CreateImportTest {

    private RestAssuredMockMvcUtils restAssuredUtils;

    @BeforeEach
    void setUp() {
      restAssuredUtils = new RestAssuredMockMvcUtils(Endpoints.Rest.IMPORT_JOB);
    }

    @Test
    @DisplayName("Should call release service")
    void should_call_release_service() {
      // when
      restAssuredUtils.doPost();

      // then
      verify(releasesService, times(1)).createImportJob();
    }

    @Test
    @DisplayName("Should return CREATED")
    void should_return_status_created() {
      // when
      ValidatableMockMvcResponse validatableResponse = restAssuredUtils.doPost();

      // then
      validatableResponse.statusCode(CREATED.value());
    }
  }

  @Nested
  @DisplayName("Tests creating a job for retrying cover downloads")
  class CreateRetryDownloadTest {

    private RestAssuredMockMvcUtils restAssuredUtils;

    @BeforeEach
    void setUp() {
      restAssuredUtils = new RestAssuredMockMvcUtils(Endpoints.Rest.COVER_JOB);
    }

    @Test
    @DisplayName("Should call release service")
    void should_call_release_service() {
      // when
      restAssuredUtils.doPost();

      // then
      verify(releasesService, times(1)).createRetryCoverDownloadJob();
    }

    @Test
    @DisplayName("Should return OK")
    void should_return_status_ok() {
      // when
      ValidatableMockMvcResponse validatableResponse = restAssuredUtils.doPost();

      // then
      validatableResponse.statusCode(OK.value());
    }
  }

  @Nested
  @DisplayName("Tests for querying import job results")
  class QueryImportJobResultsTest {

    private RestAssuredMockMvcUtils restAssuredUtils;

    @BeforeEach
    void setUp() {
      restAssuredUtils = new RestAssuredMockMvcUtils(Endpoints.Rest.IMPORT_JOB);
    }

    @Test
    @DisplayName("Should call release service")
    void should_call_release_service() {
      // when
      restAssuredUtils.doGet();

      // then
      verify(releasesService, times(1)).queryImportJobResults();
    }

    @Test
    @DisplayName("Should return result from release service with status OK")
    void should_return_status_created() {
      // given
      var importJobResultDto = List.of(
              ImportJobResultDtoFactory.createDefault(),
              ImportJobResultDtoFactory.createDefault()
      );
      doReturn(importJobResultDto).when(releasesService).queryImportJobResults();

      // when
      ValidatableMockMvcResponse validatableResponse = restAssuredUtils.doGet();

      // then
      validatableResponse.statusCode(OK.value());
      var result = validatableResponse.extract().body().jsonPath().getList(".", ImportJobResultDto.class);
      assertThat(result).isEqualTo(importJobResultDto);
    }
  }
}
