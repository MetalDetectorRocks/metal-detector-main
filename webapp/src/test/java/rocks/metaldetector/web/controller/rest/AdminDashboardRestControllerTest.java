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
import rocks.metaldetector.service.admin.dashboard.AdminDashboardService;
import rocks.metaldetector.web.RestAssuredMockMvcUtils;
import rocks.metaldetector.web.api.response.AdminDashboardResponse;
import rocks.metaldetector.web.api.response.UserInfo;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static rocks.metaldetector.support.Endpoints.Rest.ADMIN_DASHBOARD;

@ExtendWith(MockitoExtension.class)
class AdminDashboardRestControllerTest implements WithAssertions {

  @Mock
  private AdminDashboardService adminDashboardService;

  @InjectMocks
  private AdminDashboardRestController underTest;

  private RestAssuredMockMvcUtils restAssuredUtils;

  @BeforeEach
  void setUp() {
    restAssuredUtils = new RestAssuredMockMvcUtils(ADMIN_DASHBOARD);
    RestAssuredMockMvc.standaloneSetup(underTest);
  }

  @AfterEach
  void tearDown() {
    reset(adminDashboardService);
  }

  @Test
  @DisplayName("adminDashboardService is called on GET")
  void test_get_dashboard_calls_dashboard_service() {
    // when
    restAssuredUtils.doGet();

    // then
    verify(adminDashboardService).createAdminDashboardResponse();
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
  @DisplayName("user info is returned on GET dashboard")
  void test_get_admin_dashboard_response() {
    // given
    var userInfos = UserInfo.builder().totalUsers(666).build();
    var responseMock = AdminDashboardResponse.builder().userInfo(userInfos).build();
    doReturn(responseMock).when(adminDashboardService).createAdminDashboardResponse();

    // when
    var result = restAssuredUtils.doGet();

    // then
    var responseBody = (AdminDashboardResponse) result.extract().as(AdminDashboardResponse.class);
    assertThat(responseBody.getUserInfo()).isEqualTo(userInfos);
  }
}
