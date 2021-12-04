package rocks.metaldetector.web.controller.rest;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.metaldetector.butler.facade.dto.ReleaseDto;
import rocks.metaldetector.service.dashboard.DashboardService;
import rocks.metaldetector.support.Endpoints;
import rocks.metaldetector.support.TimeRange;
import rocks.metaldetector.testutil.DtoFactory;
import rocks.metaldetector.testutil.DtoFactory.ReleaseDtoFactory;
import rocks.metaldetector.web.RestAssuredMockMvcUtils;
import rocks.metaldetector.web.api.request.TopReleasesRequest;
import rocks.metaldetector.web.api.response.DashboardResponse;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class DashboardRestControllerTest implements WithAssertions {

  @Mock
  private DashboardService dashboardService;

  @InjectMocks
  private DashboardRestController underTest;

  private RestAssuredMockMvcUtils dashboardRestAssuredMockMvcUtils;
  private RestAssuredMockMvcUtils topReleasesRestAssuredMockMvcUtils;

  @BeforeEach
  void setUp() {
    dashboardRestAssuredMockMvcUtils = new RestAssuredMockMvcUtils(Endpoints.Rest.DASHBOARD);
    topReleasesRestAssuredMockMvcUtils = new RestAssuredMockMvcUtils(Endpoints.Rest.TOP_RELEASES);
    RestAssuredMockMvc.standaloneSetup(underTest);
  }

  @AfterEach
  void tearDown() {
    reset(dashboardService);
  }

  @Test
  @DisplayName("dashboardService is called on GET")
  void test_get_dashboard_calls_dashboard_service() {
    // when
    dashboardRestAssuredMockMvcUtils.doGet();

    // then
    verify(dashboardService).createDashboardResponse();
  }

  @Test
  @DisplayName("httpStatus OK is returned on GET dashboard")
  void test_get_dashboard_http_200() {
    // when
    var result = dashboardRestAssuredMockMvcUtils.doGet();

    // then
    result.assertThat(status().isOk());
  }

  @Test
  @DisplayName("upcoming releases are returned on GET dashboard")
  void test_get_dashboard_response() {
    // given
    var upcomingReleases = List.of(ReleaseDtoFactory.withArtistName("A"), ReleaseDtoFactory.withArtistName("B"));
    var responseMock = DashboardResponse.builder().upcomingReleases(upcomingReleases).build();
    doReturn(responseMock).when(dashboardService).createDashboardResponse();

    // when
    var result = dashboardRestAssuredMockMvcUtils.doGet();

    // then
    var responseBody = (DashboardResponse) result.extract().as(DashboardResponse.class);
    assertThat(responseBody.getUpcomingReleases()).isEqualTo(upcomingReleases);
  }

  @Test
  @DisplayName("dashboardService is called on GET top releases")
  void test_get_top_releases_calls_dashboard_service() {
    // given
    var request = DtoFactory.TopReleasesRequestFactory.createDefault();
    var expectedTimeRange = new TimeRange(request.getDateFrom(), request.getDateTo());

    // when
    topReleasesRestAssuredMockMvcUtils.doGet(toMap(request));

    // then
    verify(dashboardService).findTopReleases(expectedTimeRange, request.getMinFollowers(), request.getMaxReleases());
  }

  @Test
  @DisplayName("httpStatus OK is returned on GET top releases")
  void test_get_top_releases_http_200() {
    // given
    var request = DtoFactory.TopReleasesRequestFactory.createDefault();

    // when
    var result = topReleasesRestAssuredMockMvcUtils.doGet(toMap(request));

    // then
    result.assertThat(status().isOk());
  }

  @ParameterizedTest
  @MethodSource("badRequestInputProvider")
  @DisplayName("httpStatus BAD_REQUEST is returned on GET top releases with bad request")
  void test_get_top_releases_http_400(TopReleasesRequest request) {
    // when
    var result = topReleasesRestAssuredMockMvcUtils.doGet(toMap(request));

    // then
    result.assertThat(status().isBadRequest());
  }

  @Test
  @DisplayName("upcoming releases are returned on GET top releases")
  void test_get_top_releases_response() {
    // given
    var request = DtoFactory.TopReleasesRequestFactory.createDefault();
    var releases = List.of(ReleaseDtoFactory.withArtistName("A"), ReleaseDtoFactory.withArtistName("B"));
    doReturn(releases).when(dashboardService).findTopReleases(any(), anyInt(), anyInt());

    // when
    var result = topReleasesRestAssuredMockMvcUtils.doGet(toMap(request));

    // then
    var responseBody = result.extract().as(ReleaseDto[].class);
    assertThat(responseBody).containsExactly(releases.get(0), releases.get(1));
  }

  private static Stream<Arguments> badRequestInputProvider() {
    var badRequest1 = new TopReleasesRequest(0, 1);
    var badRequest2 = new TopReleasesRequest(1, 0);
    var badRequest3 = new TopReleasesRequest(1, 1);
    badRequest3.setDateTo(LocalDate.now());
    var badRequest4 = new TopReleasesRequest(1, 1);
    badRequest4.setDateFrom(LocalDate.now().plusDays(1));
    badRequest4.setDateTo(LocalDate.now());
    return Stream.of(
        Arguments.of(badRequest1),
        Arguments.of(badRequest2),
        Arguments.of(badRequest3),
        Arguments.of(badRequest4)
    );
  }

  private Map<String, Object> toMap(TopReleasesRequest request) {
    Map<String, Object> map = new HashMap<>();
    map.put("dateFrom", request.getDateFrom() != null ? request.getDateFrom().toString() : null);
    map.put("dateTo", request.getDateTo() != null ? request.getDateTo().toString() : null);
    map.put("minFollowers", request.getMinFollowers());
    map.put("maxReleases", request.getMaxReleases());

    return map;
  }
}
