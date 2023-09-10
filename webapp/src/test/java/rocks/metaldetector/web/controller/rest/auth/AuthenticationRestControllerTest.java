package rocks.metaldetector.web.controller.rest.auth;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
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
import rocks.metaldetector.service.auth.ResetPasswordService;
import rocks.metaldetector.service.exceptions.RestExceptionsHandler;
import rocks.metaldetector.service.user.UserService;
import rocks.metaldetector.web.RestAssuredMockMvcUtils;
import rocks.metaldetector.web.api.auth.InitResetPasswordRequest;
import rocks.metaldetector.web.api.auth.RegisterUserRequest;
import rocks.metaldetector.web.api.auth.RegistrationVerificationRequest;
import rocks.metaldetector.web.api.auth.RegistrationVerificationResponse;
import rocks.metaldetector.web.api.auth.ResetPasswordRequest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.OK;
import static rocks.metaldetector.support.Endpoints.Rest.REGISTER;
import static rocks.metaldetector.support.Endpoints.Rest.REGISTRATION_VERIFICATION;
import static rocks.metaldetector.support.Endpoints.Rest.REQUEST_PASSWORD_RESET;
import static rocks.metaldetector.support.Endpoints.Rest.RESET_PASSWORD;

@ExtendWith(MockitoExtension.class)
class AuthenticationRestControllerTest implements WithAssertions {

  @Mock
  private UserService userService;

  @Mock
  private ResetPasswordService resetPasswordService;

  @InjectMocks
  private AuthenticationRestController underTest;

  private RestAssuredMockMvcUtils restAssuredUtils;

  @AfterEach
  void tearDown() {
    reset(userService, resetPasswordService);
  }

  @Nested
  class RegisterUserTests {

    @BeforeEach
    void setup() {
      restAssuredUtils = new RestAssuredMockMvcUtils(REGISTER);
      RestAssuredMockMvc.standaloneSetup(underTest, RestExceptionsHandler.class);
    }

    @Test
    @DisplayName("should return ok")
    void should_return_ok() {
      // given
      RegisterUserRequest request = new RegisterUserRequest("user", "user@example.com", "secret123");

      // when
      var response = restAssuredUtils.doPost(request);

      // then
      response.status(OK);
    }

    @Test
    @DisplayName("should pass received request to user service")
    void should_pass_received_request_to_user_service() {
      // given
      RegisterUserRequest request = new RegisterUserRequest("user", "user@example.com", "secret123");

      // when
      restAssuredUtils.doPost(request);

      // then
      verify(userService).createUser(request);
    }
  }

  @Nested
  class VerifyUserTests {

    @BeforeEach
    void setup() {
      restAssuredUtils = new RestAssuredMockMvcUtils(REGISTRATION_VERIFICATION);
      RestAssuredMockMvc.standaloneSetup(underTest, RestExceptionsHandler.class);
    }

    @Test
    @DisplayName("should return ok")
    void should_return_ok() {
      // given
      RegistrationVerificationRequest request = new RegistrationVerificationRequest("eyFoo");

      // when
      var response = restAssuredUtils.doPost(request);

      // then
      response.status(OK);
    }

    @Test
    @DisplayName("should pass received token to user service")
    void should_pass_received_request_to_user_service() {
      // given
      String token = "eyFoo";
      RegistrationVerificationRequest request = new RegistrationVerificationRequest(token);

      // when
      restAssuredUtils.doPost(request);

      // then
      verify(userService).verifyEmailToken(token);
    }

    @Test
    @DisplayName("should return registration verification response")
    void should_return_registration_verification_response() {
      // given
      RegistrationVerificationRequest request = new RegistrationVerificationRequest("eyFoo");
      RegistrationVerificationResponse verificationResponse = new RegistrationVerificationResponse("JohnD");
      when(userService.verifyEmailToken(any())).thenReturn(verificationResponse);

      // when
      var response = restAssuredUtils.doPost(request);

      // then
      var extractedResponse = response.extract().as(RegistrationVerificationResponse.class);
      assertThat(extractedResponse).isEqualTo(verificationResponse);
    }
  }

  @Nested
  class RequestPasswordResetTests {

    @BeforeEach
    void setup() {
      restAssuredUtils = new RestAssuredMockMvcUtils(REQUEST_PASSWORD_RESET);
      RestAssuredMockMvc.standaloneSetup(underTest, RestExceptionsHandler.class);
    }

    @Test
    @DisplayName("should return ok")
    void should_return_ok() {
      // given
      InitResetPasswordRequest request = new InitResetPasswordRequest("test@example.com");

      // when
      var response = restAssuredUtils.doPost(request);

      // then
      response.status(OK);
    }

    @Test
    @DisplayName("should pass request to password reset service")
    void should_pass_request_to_password_reset_service() {
      // given
      InitResetPasswordRequest request = new InitResetPasswordRequest("test@example.com");

      // when
      restAssuredUtils.doPost(request);

      // then
      verify(resetPasswordService).requestPasswordReset(request);
    }
  }

  @Nested
  class ResetPasswordTests {

    @BeforeEach
    void setup() {
      restAssuredUtils = new RestAssuredMockMvcUtils(RESET_PASSWORD);
      RestAssuredMockMvc.standaloneSetup(underTest, RestExceptionsHandler.class);
    }

    @Test
    @DisplayName("should return ok")
    void should_return_ok() {
      // given
      ResetPasswordRequest request = new ResetPasswordRequest("eyFoobar", "new-password");

      // when
      var response = restAssuredUtils.doPost(request);

      // then
      response.status(OK);
    }

    @Test
    @DisplayName("should pass request to password reset service")
    void should_pass_request_to_password_reset_service() {
      // given
      ResetPasswordRequest request = new ResetPasswordRequest("eyFoobar", "new-password");

      // when
      restAssuredUtils.doPost(request);

      // then
      verify(resetPasswordService).resetPassword(request);
    }
  }
}
