package rocks.metaldetector.web.controller.rest;

import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import rocks.metaldetector.butler.facade.ReleaseService;
import rocks.metaldetector.config.constants.Endpoints;
import rocks.metaldetector.support.ExternalServiceException;
import rocks.metaldetector.testutil.DtoFactory.DetectorReleaseRequestFactory;
import rocks.metaldetector.testutil.DtoFactory.ReleaseDtoFactory;
import rocks.metaldetector.testutil.WithExceptionResolver;
import rocks.metaldetector.web.RestAssuredMockMvcUtils;
import rocks.metaldetector.web.api.request.DetectorReleasesRequest;
import rocks.metaldetector.web.api.response.DetectorImportResponse;
import rocks.metaldetector.web.api.response.DetectorReleasesResponse;
import rocks.metaldetector.web.transformer.DetectorImportResponseTransformer;
import rocks.metaldetector.web.transformer.DetectorReleasesResponseTransformer;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyIterable;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static rocks.metaldetector.testutil.DtoFactory.DetectorImportResponseFactory;
import static rocks.metaldetector.testutil.DtoFactory.ImportResultDtoFactory;

@ExtendWith(MockitoExtension.class)
class ReleasesRestControllerTest implements WithAssertions, WithExceptionResolver {

  @Mock
  private ReleaseService releasesService;

  @Mock
  private DetectorReleasesResponseTransformer releasesResponseTransformer;

  @Mock
  private DetectorImportResponseTransformer importResponseTransformer;

  @InjectMocks
  private ReleasesRestController underTest;

  private RestAssuredMockMvcUtils restAssuredUtils;

  @BeforeEach
  void setUp() {
    restAssuredUtils = new RestAssuredMockMvcUtils(Endpoints.Rest.RELEASES);
    RestAssuredMockMvc.standaloneSetup(underTest,
                                       springSecurity((request, response, chain) -> chain.doFilter(request, response)),
                                       exceptionResolver());
  }

  @AfterEach
  void tearDown() {
    reset(releasesService, releasesResponseTransformer, importResponseTransformer);
  }

  @Test
  @DisplayName("Should pass query request parameter to release service")
  void should_pass_query_parameter_to_release_service() {
    // given
    DetectorReleasesRequest request = DetectorReleaseRequestFactory.createDefault();

    // when
    restAssuredUtils.doPost(request);

    // then
    verify(releasesService, times(1)).findReleases(request.getArtists(), request.getDateFrom(), request.getDateTo());
  }

  @Test
  @DisplayName("Should use transformer to transform releases to a list of DetectorReleasesResponse")
  void should_use_releases_transformer() {
    // given
    var request = DetectorReleaseRequestFactory.createDefault();
    var releases = List.of(ReleaseDtoFactory.createDefault());
    doReturn(releases).when(releasesService).findReleases(any(), any(), any());

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
    doReturn(Collections.emptyList()).when(releasesService).findReleases(any(), any(), any());
    doReturn(transformedResponse).when(releasesResponseTransformer).transformListOf(any());

    // when
    var validatableResponse = restAssuredUtils.doPost(request);

    // then
    validatableResponse
            .contentType(ContentType.JSON)
            .statusCode(HttpStatus.OK.value());

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
        .statusCode(HttpStatus.BAD_REQUEST.value());
  }

  @Test
  @DisplayName("Should return 503 when butler is not available")
  void test_butler_unavailable_for_query() {
    // given
    DetectorReleasesRequest request = DetectorReleaseRequestFactory.createDefault();
    when(releasesService.findReleases(anyIterable(), any(), any())).thenThrow(new ExternalServiceException());

    // when
    var validatableResponse = restAssuredUtils.doPost(request);

    // then
    validatableResponse
        .contentType(ContentType.JSON)
        .statusCode(HttpStatus.SERVICE_UNAVAILABLE.value());
  }

  @Test
  @DisplayName("Should call release service on import")
  void should_call_release_service() {
    // when
    restAssuredUtils.doGet("/import");

    // then
    verify(releasesService, times(1)).importReleases();
  }

  @Test
  @DisplayName("Should use transformer to transform import to a DetectorImportResponse")
  void should_use_import_transformer() {
    // given
    var importResult = ImportResultDtoFactory.createDefault();
    doReturn(importResult).when(releasesService).importReleases();

    // when
    restAssuredUtils.doGet("/import");

    // then
    verify(importResponseTransformer, times(1)).transform(importResult);
  }

  @Test
  @DisplayName("Should return the transformed import response")
  void should_return_import_response() {
    // given
    var transformedResponse = DetectorImportResponseFactory.createDefault();
    doReturn(transformedResponse).when(importResponseTransformer).transform(any());

    // when
    var validatableResponse = restAssuredUtils.doGet("/import");

    // then
    validatableResponse
        .contentType(ContentType.JSON)
        .statusCode(HttpStatus.OK.value());

    var result = validatableResponse.extract().as(DetectorImportResponse.class);
    assertThat(result).isEqualTo(transformedResponse);
  }

  @Test
  @DisplayName("Should return 503 when butler is not available")
  void test_butler_unavailable_for_import() {
    // given
    when(releasesService.importReleases()).thenThrow(new ExternalServiceException());

    // when
    var validatableResponse = restAssuredUtils.doGet("/import");

    // then
    validatableResponse
        .contentType(ContentType.JSON)
        .statusCode(HttpStatus.SERVICE_UNAVAILABLE.value());
  }
}
