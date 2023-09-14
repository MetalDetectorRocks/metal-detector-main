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
import rocks.metaldetector.service.statistics.StatisticsService;
import rocks.metaldetector.web.RestAssuredMockMvcUtils;
import rocks.metaldetector.web.api.response.StatisticsResponse;
import rocks.metaldetector.web.api.response.UserInfo;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static rocks.metaldetector.support.Endpoints.Rest.STATISTICS;

@ExtendWith(MockitoExtension.class)
class StatisticsRestControllerTest implements WithAssertions {

  @Mock
  private StatisticsService statisticsService;

  @InjectMocks
  private StatisticsRestController underTest;

  private RestAssuredMockMvcUtils restAssuredUtils;

  @BeforeEach
  void setUp() {
    restAssuredUtils = new RestAssuredMockMvcUtils(STATISTICS);
    RestAssuredMockMvc.standaloneSetup(underTest);
  }

  @AfterEach
  void tearDown() {
    reset(statisticsService);
  }

  @Test
  @DisplayName("statisticsService is called on GET")
  void test_get_statistics_calls_statistics_service() {
    // when
    restAssuredUtils.doGet();

    // then
    verify(statisticsService).createStatisticsResponse();
  }

  @Test
  @DisplayName("httpStatus OK is returned on GET dashboard")
  void test_get_statistics_http_200() {
    // when
    var result = restAssuredUtils.doGet();

    // then
    result.assertThat(status().isOk());
  }

  @Test
  @DisplayName("statistics response is returned on GET statistics")
  void test_get_statistics_response() {
    // given
    var userInfo = UserInfo.builder().totalUsers(666).build();
    var responseMock = StatisticsResponse.builder().userInfo(userInfo).build();
    doReturn(responseMock).when(statisticsService).createStatisticsResponse();

    // when
    var result = restAssuredUtils.doGet();

    // then
    var responseBody = (StatisticsResponse) result.extract().as(StatisticsResponse.class);
    assertThat(responseBody.getUserInfo()).isEqualTo(userInfo);
  }
}
