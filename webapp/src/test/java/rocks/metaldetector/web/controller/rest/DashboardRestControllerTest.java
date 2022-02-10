package rocks.metaldetector.web.controller.rest;

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
import rocks.metaldetector.service.dashboard.DashboardService;
import rocks.metaldetector.testutil.DtoFactory.ReleaseDtoFactory;
import rocks.metaldetector.web.RestAssuredMockMvcUtils;
import rocks.metaldetector.web.api.response.DashboardResponse;

import java.util.List;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static rocks.metaldetector.support.Endpoints.Rest.DASHBOARD;

@ExtendWith(MockitoExtension.class)
class DashboardRestControllerTest implements WithAssertions {

  @Mock
  private DashboardService dashboardService;

  @InjectMocks
  private DashboardRestController underTest;

  private RestAssuredMockMvcUtils restAssuredUtils;

  @BeforeEach
  void setUp() {
    restAssuredUtils = new RestAssuredMockMvcUtils(DASHBOARD);
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
    restAssuredUtils.doGet();

    // then
    verify(dashboardService).createDashboardResponse();
  }

  @Test
  @DisplayName("httpStatus OK is returned on GET dashboard")
  void test_get_dashboard_http_200() {
    // when
    var result = restAssuredUtils.doGet();

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
    var result = restAssuredUtils.doGet();

    // then
    var responseBody = (DashboardResponse) result.extract().as(DashboardResponse.class);
    assertThat(responseBody.getUpcomingReleases()).isEqualTo(upcomingReleases);
  }
}
