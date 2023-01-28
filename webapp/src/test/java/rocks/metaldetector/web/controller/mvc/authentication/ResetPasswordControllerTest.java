package rocks.metaldetector.web.controller.mvc.authentication;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.restassured.http.ContentType;
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
import org.springframework.validation.BindingResult;
import rocks.metaldetector.config.constants.ViewNames;
import rocks.metaldetector.service.exceptions.RestExceptionsHandler;
import rocks.metaldetector.service.user.UserService;
import rocks.metaldetector.support.Endpoints;
import rocks.metaldetector.support.JwtsSupport;
import rocks.metaldetector.testutil.DtoFactory.ChangePasswordRequestFactory;
import rocks.metaldetector.web.RestAssuredMockMvcUtils;
import rocks.metaldetector.web.api.auth.ChangePasswordRequest;

import java.time.Duration;
import java.util.Date;
import java.util.Map;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.instanceOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static rocks.metaldetector.support.Endpoints.Authentication.FORGOT_PASSWORD;
import static rocks.metaldetector.support.Endpoints.Authentication.RESET_PASSWORD;

@ExtendWith(MockitoExtension.class)
class ResetPasswordControllerTest implements WithAssertions {

  private static final String PARAM_TOKEN_STRING = "tokenString";
  private static final String PARAM_PASSWORD = "newPlainPassword";
  private static final String PARAM_VERIFY_PASSWORD = "verifyNewPlainPassword";

  @Mock
  private UserService userService;

  @Mock
  private JwtsSupport jwtsSupport;

  @InjectMocks
  private ResetPasswordController underTest;

  private RestAssuredMockMvcUtils restAssuredUtils;
  private ObjectMapper objectMapper;

  @BeforeEach
  void setup() {
    restAssuredUtils = new RestAssuredMockMvcUtils(RESET_PASSWORD);
    RestAssuredMockMvc.standaloneSetup(underTest, RestExceptionsHandler.class);
    objectMapper = new ObjectMapper();
    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
  }

  @AfterEach
  void tearDown() {
    reset(userService, jwtsSupport);
  }

  @Test
  @DisplayName("Requesting '" + RESET_PASSWORD + "' should call JwtsSupport")
  void reset_password_with_not_existing_token_should_call_token_service() {
    // given
    String token = "token";

    // when
    restAssuredUtils.doGet("?token=" + token);

    // then
    verify(jwtsSupport).getClaims(token);
  }

  @Test
  @DisplayName("Requesting '" + RESET_PASSWORD + "' with expired token should return the forgot password view with error message")
  void reset_password_with_expired_token_should_return_error() {
    // given
    var claims = mock(Claims.class);
    var now = new Date();
    doReturn(now).when(claims).getExpiration();
    doReturn(claims).when(jwtsSupport).getClaims(any());

    // when
    var validatableResponse = restAssuredUtils.doGet("?token=expired-token");

    // then
    validatableResponse
        .assertThat(model().hasNoErrors())
        .assertThat(flash().attributeExists("tokenExpiredError"))
        .assertThat(status().is3xxRedirection())
        .assertThat(redirectedUrl(FORGOT_PASSWORD));
  }

  @Test
  @DisplayName("Requesting '" + RESET_PASSWORD + "' with valid token should return the reset password view")
  void reset_password_with_valid_token_should_return_view() {
    // given
    var claims = mock(Claims.class);
    doReturn(new Date(System.currentTimeMillis() + Duration.ofMinutes(1).toMillis())).when(claims).getExpiration();
    doReturn(claims).when(jwtsSupport).getClaims(any());

    // when
    var validatableResponse = restAssuredUtils.doGet("?token=valid-token");

    // then
    validatableResponse
        .assertThat(model().hasNoErrors())
        .assertThat(model().attributeExists(ResetPasswordController.FORM_DTO))
        .assertThat(status().isOk())
        .assertThat(view().name(ViewNames.Authentication.RESET_PASSWORD));
  }

  @Test
  @DisplayName("POSTing on '" + RESET_PASSWORD + "' with valid change password request should be ok")
  void reset_password_should_work() {
    // given
    String password = "valid-password";

    // when
    var validatableResponse = restAssuredUtils.doPost(Map.of(PARAM_TOKEN_STRING, "valid-token",
                                                             PARAM_PASSWORD, password,
                                                             PARAM_VERIFY_PASSWORD, password),
                                                      ContentType.HTML);

    // then
    validatableResponse
        .assertThat(model().errorCount(0))
        .assertThat(status().is3xxRedirection())
        .assertThat(redirectedUrl(Endpoints.Authentication.LOGIN + "?resetSuccess"));
  }

  @Test
  @DisplayName("POSTing on '" + RESET_PASSWORD + "' with valid change password request should call UserService")
  void reset_password_should_call_user_service() {
    // given
    String token = "valid-token";
    String password = "valid-password";

    // when
    restAssuredUtils.doPost(Map.of(PARAM_TOKEN_STRING, token,
                                   PARAM_PASSWORD, password,
                                   PARAM_VERIFY_PASSWORD, password),
                            ContentType.HTML);

    // then
    verify(userService).resetPasswordWithToken(token, password);
  }

  @ParameterizedTest(name = "[{index}]: {0}")
  @MethodSource("changePasswordRequestProvider")
  @DisplayName("POSTing on '" + RESET_PASSWORD + "' with invalid change password request should fail")
  void reset_password_with_invalid_request_should_fail(ChangePasswordRequest request) {
    // when
    var validatableResponse = restAssuredUtils.doPost(objectMapper.convertValue(request, new TypeReference<>() {}), ContentType.HTML);

    // then
    validatableResponse
        .assertThat(flash().attribute(ResetPasswordController.FORM_DTO, instanceOf(ChangePasswordRequest.class)))
        .assertThat(flash().attribute(BindingResult.class.getName() + "." + ResetPasswordController.FORM_DTO, instanceOf(BindingResult.class)))
        .assertThat(status().is3xxRedirection())
        .assertThat(redirectedUrl(RESET_PASSWORD + "?token=" + request.getTokenString()));
  }

  @ParameterizedTest(name = "[{index}]: {0}")
  @MethodSource("changePasswordRequestProvider")
  @DisplayName("POSTing on '" + RESET_PASSWORD + "' with invalid change password request should not call anything")
  void reset_password_with_invalid_request_should_not_call_service_services(ChangePasswordRequest request) {
    // when
    restAssuredUtils.doPost(objectMapper.convertValue(request, new TypeReference<>() {}), ContentType.HTML);

    // then
    verifyNoInteractions(jwtsSupport, userService);
  }

  private static Stream<Arguments> changePasswordRequestProvider() {
    return Stream.of(
        // invalid token strings
        Arguments.of(ChangePasswordRequestFactory.withTokenString("")),
        Arguments.of(ChangePasswordRequestFactory.withTokenString(null)),

        // invalid passwords
        Arguments.of(ChangePasswordRequestFactory.withPassword("secret")),
        Arguments.of(ChangePasswordRequestFactory.withPassword("")),
        Arguments.of(ChangePasswordRequestFactory.withPassword(null))
    );
  }
}
