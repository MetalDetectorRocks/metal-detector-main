package rocks.metaldetector.web.controller.rest.auth;

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
import rocks.metaldetector.persistence.domain.user.UserEntity;
import rocks.metaldetector.persistence.domain.user.UserRepository;
import rocks.metaldetector.service.auth.RefreshTokenService;
import rocks.metaldetector.service.exceptions.RestExceptionsHandler;
import rocks.metaldetector.web.RestAssuredMockMvcUtils;
import rocks.metaldetector.web.api.auth.AuthenticationResponse;
import rocks.metaldetector.web.api.auth.LoginResponse;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.OK;
import static rocks.metaldetector.persistence.domain.user.UserRole.ROLE_USER;
import static rocks.metaldetector.support.Endpoints.Rest.AUTHENTICATION;
import static rocks.metaldetector.support.Endpoints.Rest.REFRESH_ACCESS_TOKEN;

@ExtendWith(MockitoExtension.class)
public class MockRefreshTokenRestControllerTest implements WithAssertions {

  @Mock
  private RefreshTokenService refreshTokenService;

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private MockRefreshTokenRestController underTest;

  private RestAssuredMockMvcUtils restAssuredUtils;

  @AfterEach
  void tearDown() {
    reset(refreshTokenService, userRepository);
  }

  @Nested
  class AuthenticatedTests {

    @BeforeEach
    void setup() {
      restAssuredUtils = new RestAssuredMockMvcUtils(AUTHENTICATION);
      RestAssuredMockMvc.standaloneSetup(underTest, RestExceptionsHandler.class);
    }

    @Test
    @DisplayName("should return true")
    void should_return_false_if_user_is_not_authenticated() {
      // given
      doReturn(false).when(refreshTokenService).isValid(any());

      // when
      ValidatableMockMvcResponse response = restAssuredUtils.doGet();

      // then
      var authenticationResponse = response.extract().as(AuthenticationResponse.class);
      response.status(OK);
      assertThat(authenticationResponse.isAuthenticated()).isTrue();
    }
  }

  @Nested
  class RefreshAccessTokenTests {

    @BeforeEach
    void setup() {
      restAssuredUtils = new RestAssuredMockMvcUtils(REFRESH_ACCESS_TOKEN);
      RestAssuredMockMvc.standaloneSetup(underTest, RestExceptionsHandler.class);
    }

    @Test
    @DisplayName("should fetch Administrator user")
    void should_fetch_administrator_user() {
      // when
      restAssuredUtils.doGet();

      // then
      verify(userRepository).getByUsername("Administrator");
    }

    @Test
    @DisplayName("should create new access token with administrator's public id")
    void should_create_new_access_token_with_administrators_public_id() {
      // given
      UserEntity user = mock(UserEntity.class);
      when(user.getPublicId()).thenReturn("test-public-id");
      when(userRepository.getByUsername(any())).thenReturn(user);

      // when
      restAssuredUtils.doGet();

      // then
      verify(refreshTokenService).createAccessToken("test-public-id");
    }

    @Test
    @DisplayName("should return ok")
    void should_return_ok() {
      // given
      when(userRepository.getByUsername(any())).thenReturn(mock(UserEntity.class));

      // when
      var response = restAssuredUtils.doGet();

      // then
      response.status(OK);
    }

    @Test
    @DisplayName("should return login response in body")
    void should_return_login_response() {
      // given
      UserEntity user = mock(UserEntity.class);
      when(user.getUsername()).thenReturn("dummy");
      when(user.getUserRoleNames()).thenReturn(List.of(ROLE_USER.getDisplayName()));
      when(user.getPublicId()).thenReturn("test-public-id");
      when(userRepository.getByUsername(any())).thenReturn(user);
      when(refreshTokenService.createAccessToken(any())).thenReturn("eyAccessToken");

      // when
      var response = restAssuredUtils.doGet();

      // then
      var extractedResponse = response.extract().as(LoginResponse.class);
      assertThat(extractedResponse).isEqualTo(new LoginResponse("dummy", List.of("User"), "eyAccessToken"));
    }
  }
}
