package rocks.metaldetector.web.controller.mvc.authentication;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.validation.BindingResult;
import rocks.metaldetector.config.constants.Endpoints;
import rocks.metaldetector.config.constants.MessageKeys;
import rocks.metaldetector.config.constants.ViewNames;
import rocks.metaldetector.service.token.TokenFactory;
import rocks.metaldetector.support.JwtsSupport;
import rocks.metaldetector.persistence.domain.token.TokenEntity;
import rocks.metaldetector.persistence.domain.token.TokenType;
import rocks.metaldetector.service.token.TokenService;
import rocks.metaldetector.testutil.BaseWebMvcTestWithSecurity;
import rocks.metaldetector.testutil.DtoFactory.ChangePasswordRequestFactory;
import rocks.metaldetector.web.api.request.ChangePasswordRequest;

import java.time.Duration;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.instanceOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(value = ResetPasswordController.class)
class ResetPasswordControllerIT extends BaseWebMvcTestWithSecurity {

  private static final String PARAM_TOKEN_STRING = "tokenString";
  private static final String PARAM_PASSWORD = "newPlainPassword";
  private static final String PARAM_VERIFY_PASSWORD = "verifyNewPlainPassword";

  @MockBean
  private TokenService tokenService;

  @MockBean
  private MessageSource messages;

  @MockBean
  private JwtsSupport jwtsSupport;

  @AfterEach
  void tearDown() {
    reset(userService, tokenService, messages, jwtsSupport);
  }

  @Test
  @DisplayName("Requesting '" + Endpoints.Guest.RESET_PASSWORD + "' with not existing token should return the forgot password view with error message")
  void test_reset_password_with_not_existing_token() throws Exception {
    final String ERROR_MESSAGE = "the token was not found";
    final String TOKEN = "not-existing-token";
    when(tokenService.getResetPasswordTokenByTokenString(TOKEN)).thenReturn(Optional.empty());
    when(messages.getMessage(MessageKeys.ForgotPassword.TOKEN_DOES_NOT_EXIST, null, Locale.US)).thenReturn(ERROR_MESSAGE);
    when(redirectionInterceptor.preHandle(any(), any(), any())).thenReturn(true);

    mockMvc.perform(get(Endpoints.Guest.RESET_PASSWORD + "?token={token}", TOKEN))
        .andExpect(model().hasNoErrors())
        .andExpect(flash().attribute("resetPasswordError", ERROR_MESSAGE))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(Endpoints.Guest.FORGOT_PASSWORD));

    verify(tokenService, times(1)).getResetPasswordTokenByTokenString(TOKEN);
    verify(messages, times(1)).getMessage(MessageKeys.ForgotPassword.TOKEN_DOES_NOT_EXIST, null, Locale.US);
  }

  @Test
  @DisplayName("Requesting '" + Endpoints.Guest.RESET_PASSWORD + "' with expired token should return the forgot password view with error message")
  void test_reset_password_with_expired_token() throws Exception {
    final String ERROR_MESSAGE = "the token is expired";
    final String TOKEN = "expired-token";
    TokenEntity tokenEntity = TokenFactory.createToken(TokenType.PASSWORD_RESET, 1);

    when(tokenService.getResetPasswordTokenByTokenString(TOKEN)).thenReturn(Optional.of(tokenEntity));
    when(messages.getMessage(MessageKeys.ForgotPassword.TOKEN_IS_EXPIRED, null, Locale.US)).thenReturn(ERROR_MESSAGE);
    when(redirectionInterceptor.preHandle(any(), any(), any())).thenReturn(true);

    mockMvc.perform(get(Endpoints.Guest.RESET_PASSWORD + "?token={token}", TOKEN))
        .andExpect(model().hasNoErrors())
        .andExpect(flash().attribute("resetPasswordError", ERROR_MESSAGE))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(Endpoints.Guest.FORGOT_PASSWORD));

    verify(tokenService, times(1)).getResetPasswordTokenByTokenString(TOKEN);
    verify(messages, times(1)).getMessage(MessageKeys.ForgotPassword.TOKEN_IS_EXPIRED, null, Locale.US);
  }

  @Test
  @DisplayName("Requesting '" + Endpoints.Guest.RESET_PASSWORD + "' with valid token should return the reset password view")
  void test_reset_password_with_valid_token() throws Exception {
    final String TOKEN = "valid-token";
    TokenEntity tokenEntity = TokenFactory.createToken(TokenType.PASSWORD_RESET, Duration.ofHours(1).toMillis());

    when(tokenService.getResetPasswordTokenByTokenString(TOKEN)).thenReturn(Optional.of(tokenEntity));
    when(redirectionInterceptor.preHandle(any(), any(), any())).thenReturn(true);

    mockMvc.perform(get(Endpoints.Guest.RESET_PASSWORD + "?token={token}", TOKEN))
        .andExpect(model().hasNoErrors())
        .andExpect(model().attributeExists(ResetPasswordController.FORM_DTO))
        .andExpect(status().isOk())
        .andExpect(view().name(ViewNames.Guest.RESET_PASSWORD));

    verify(tokenService, times(1)).getResetPasswordTokenByTokenString(TOKEN);
  }

  @Test
  @DisplayName("POSTing on '" + Endpoints.Guest.RESET_PASSWORD + "' with valid change password request should be ok")
  void test_reset_password() throws Exception {
    final String TOKEN = "valid-token";
    final String PASSWORD = "valid-password";
    when(redirectionInterceptor.preHandle(any(), any(), any())).thenReturn(true);

    mockMvc.perform(post(Endpoints.Guest.RESET_PASSWORD)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param(PARAM_TOKEN_STRING, TOKEN)
                        .param(PARAM_PASSWORD, PASSWORD)
                        .param(PARAM_VERIFY_PASSWORD, PASSWORD))
        .andExpect(model().errorCount(0))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(Endpoints.Guest.LOGIN + "?resetSuccess"));

    verify(userService, times(1)).changePassword(TOKEN, PASSWORD);
  }

  @ParameterizedTest(name = "[{index}]: {0}")
  @MethodSource("changePasswordRequestProvider")
  @DisplayName("POSTing on '" + Endpoints.Guest.RESET_PASSWORD + "' with invalid change password request should fail")
  void test_reset_password_with_invalid_request(ChangePasswordRequest request) throws Exception {
    when(redirectionInterceptor.preHandle(any(), any(), any())).thenReturn(true);

    mockMvc.perform(post(Endpoints.Guest.RESET_PASSWORD)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param(PARAM_TOKEN_STRING, request.getTokenString())
                        .param(PARAM_PASSWORD, request.getNewPlainPassword())
                        .param(PARAM_VERIFY_PASSWORD, request.getVerifyNewPlainPassword()))
        .andExpect(flash().attribute(ResetPasswordController.FORM_DTO, instanceOf(ChangePasswordRequest.class)))
        .andExpect(flash().attribute(BindingResult.class.getName() + "." + ResetPasswordController.FORM_DTO, instanceOf(BindingResult.class)))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(Endpoints.Guest.RESET_PASSWORD + "?token=" + request.getTokenString()));

    verifyNoInteractions(tokenService, userService);
  }

  private static Stream<Arguments> changePasswordRequestProvider() {
    return Stream.of(
        // invalid token strings
        Arguments.of(ChangePasswordRequestFactory.withTokenString("")),
        Arguments.of(ChangePasswordRequestFactory.withTokenString(null)),

        // invalid passwords
        Arguments.of(ChangePasswordRequestFactory.withPassword("secret-password", "other-secret-password")),
        Arguments.of(ChangePasswordRequestFactory.withPassword("secret", "secret")),
        Arguments.of(ChangePasswordRequestFactory.withPassword("", "")),
        Arguments.of(ChangePasswordRequestFactory.withPassword(null, null))
    );
  }
}
