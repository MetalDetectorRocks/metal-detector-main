package rocks.metaldetector.web.controller.rest;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import io.restassured.module.mockmvc.response.ValidatableMockMvcResponse;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.metaldetector.security.AuthenticationFacade;
import rocks.metaldetector.service.exceptions.RestExceptionsHandler;
import rocks.metaldetector.web.RestAssuredMockMvcUtils;
import rocks.metaldetector.web.api.response.AuthenticationResponse;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.springframework.http.HttpStatus.OK;
import static rocks.metaldetector.support.Endpoints.Rest.AUTHENTICATION;

@ExtendWith(MockitoExtension.class)
class AuthenticationRestControllerTest implements WithAssertions {

  @Mock
  private AuthenticationFacade authenticationFacade;

  @InjectMocks
  private AuthenticationRestController underTest;

  private RestAssuredMockMvcUtils restAssuredUtils;

  @BeforeEach
  void setup() {
    restAssuredUtils = new RestAssuredMockMvcUtils(AUTHENTICATION);
    RestAssuredMockMvc.standaloneSetup(underTest, RestExceptionsHandler.class);
  }

  @AfterEach
  void tearDown() {
    reset(authenticationFacade);
  }

  @Test
  @DisplayName("should return false if user is not authenticated")
  void should_return_false_if_user_is_not_authenticated() {
    // given
    doReturn(false).when(authenticationFacade).isAuthenticated();

    // when
    ValidatableMockMvcResponse response = restAssuredUtils.doGet();

    // then
    var authenticationResponse = response.extract().as(AuthenticationResponse.class);
    response.status(OK);
    assertThat(authenticationResponse.isAuthenticated()).isFalse();
  }

  @Test
  @DisplayName("should return true if user is authenticated")
  void should_return_true_if_user_is_authenticated() {
    // given
    doReturn(true).when(authenticationFacade).isAuthenticated();

    // when
    ValidatableMockMvcResponse response = restAssuredUtils.doGet();

    // then
    var authenticationResponse = response.extract().as(AuthenticationResponse.class);
    response.status(OK);
    assertThat(authenticationResponse.isAuthenticated()).isTrue();
  }
}
