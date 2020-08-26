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
import rocks.metaldetector.config.constants.Endpoints;
import rocks.metaldetector.service.summary.SummaryService;
import rocks.metaldetector.testutil.DtoFactory.ReleaseDtoFactory;
import rocks.metaldetector.web.RestAssuredMockMvcUtils;
import rocks.metaldetector.web.api.response.SummaryResponse;

import java.util.List;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class SummaryRestControllerTest implements WithAssertions {

  @Mock
  private SummaryService summaryService;

  @InjectMocks
  private SummaryRestController underTest;

  private RestAssuredMockMvcUtils restAssuredMockMvcUtils;

  @BeforeEach
  void setUp() {
    restAssuredMockMvcUtils = new RestAssuredMockMvcUtils(Endpoints.Rest.HOME);
    RestAssuredMockMvc.standaloneSetup(underTest,
                                       springSecurity((request, response, chain) -> chain.doFilter(request, response)));
  }

  @AfterEach
  void tearDown() {
    reset(summaryService);
  }

  @Test
  @DisplayName("homeService is called on GET")
  void test_home_service_is_called() {
    // when
    restAssuredMockMvcUtils.doGet();

    // then
    verify(summaryService, times(1)).createSummaryResponse();
  }

  @Test
  @DisplayName("httpStatus OK is returned")
  void test_http_200() {
    // when
    var result = restAssuredMockMvcUtils.doGet();

    // then
    result.assertThat(status().isOk());
  }

  @Test
  @DisplayName("upcoming releases are returned")
  void test_response() {
    // given
    var upcomingReleases = List.of(ReleaseDtoFactory.withArtistName("A"), ReleaseDtoFactory.withArtistName("B"));
    var responseMock = SummaryResponse.builder().upcomingReleases(upcomingReleases).build();
    doReturn(responseMock).when(summaryService).createSummaryResponse();

    // when
    var result = restAssuredMockMvcUtils.doGet();

    // then
    var responseBody = (SummaryResponse) result.extract().as(SummaryResponse.class);
    assertThat(responseBody.getUpcomingReleases()).isEqualTo(upcomingReleases);
  }
}