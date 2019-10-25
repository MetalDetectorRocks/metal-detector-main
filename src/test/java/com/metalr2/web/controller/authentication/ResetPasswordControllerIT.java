package com.metalr2.web.controller.authentication;

import com.metalr2.config.constants.Endpoints;
import com.metalr2.config.constants.MessageKeys;
import com.metalr2.config.constants.ViewNames;
import com.metalr2.model.ArtifactForFramework;
import com.metalr2.model.token.TokenEntity;
import com.metalr2.model.token.TokenFactory;
import com.metalr2.model.token.TokenType;
import com.metalr2.security.ExpirationTime;
import com.metalr2.security.WebSecurity;
import com.metalr2.service.token.TokenService;
import com.metalr2.service.user.UserService;
import com.metalr2.web.DtoFactory.ChangePasswordRequestFactory;
import com.metalr2.web.dto.request.ChangePasswordRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.BindingResult;

import java.util.Locale;
import java.util.Optional;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.instanceOf;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ResetPasswordController.class)
@Import(WebSecurity.class)
@Tag("integration-test")
class ResetPasswordControllerIT {

  private static final String PARAM_TOKEN_STRING      = "tokenString";
  private static final String PARAM_PASSWORD          = "newPlainPassword";
  private static final String PARAM_VERIFY_PASSWORD   = "verifyNewPlainPassword";

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private UserService userService;

  @MockBean
  private TokenService tokenService;

  @MockBean
  private MessageSource messages;


  @MockBean
  @ArtifactForFramework
  private BCryptPasswordEncoder passwordEncoder; // for WebSecurity

  @AfterEach
  void tearDown() {
    reset(userService, tokenService, messages);
  }

  @Test
  @DisplayName("Requesting '" + Endpoints.Guest.RESET_PASSWORD + "' with not existing token should return the forgot password view with error message")
  void test_reset_password_with_not_existing_token() throws Exception {
    final String ERROR_MESSAGE = "the token was not found";
    final String TOKEN = "not-existing-token";
    when(tokenService.getResetPasswordTokenByTokenString(TOKEN)).thenReturn(Optional.empty());
    when(messages.getMessage(MessageKeys.ForgotPassword.TOKEN_DOES_NOT_EXIST, null, Locale.US)).thenReturn(ERROR_MESSAGE);

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
    TokenEntity tokenEntity = TokenFactory.createToken(TokenType.PASSWORD_RESET, ExpirationTime.ONE_HOUR.toMillis());

    when(tokenService.getResetPasswordTokenByTokenString(TOKEN)).thenReturn(Optional.of(tokenEntity));

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
    TokenEntity tokenEntity = TokenFactory.createToken(TokenType.PASSWORD_RESET, ExpirationTime.ONE_HOUR.toMillis());
    when(tokenService.getResetPasswordTokenByTokenString(TOKEN)).thenReturn(Optional.of(tokenEntity));

    mockMvc.perform(post(Endpoints.Guest.RESET_PASSWORD)
              .param(PARAM_TOKEN_STRING, TOKEN)
              .param(PARAM_PASSWORD, PASSWORD)
              .param(PARAM_VERIFY_PASSWORD, PASSWORD))
           .andExpect(model().errorCount(0))
           .andExpect(status().is3xxRedirection())
           .andExpect(redirectedUrl(Endpoints.Guest.LOGIN + "?resetSuccess"));

    verify(tokenService, times(1)).getResetPasswordTokenByTokenString(TOKEN);
    verify(userService, times(1)).changePassword(tokenEntity.getUser(), PASSWORD);
    verify(tokenService, times(1)).deleteToken(tokenEntity);
  }

  @ParameterizedTest(name = "[{index}]: {0}")
  @MethodSource("changePasswordRequestProvider")
  @DisplayName("POSTing on '" + Endpoints.Guest.RESET_PASSWORD + "' with invalid change password request should be fail")
  void test_reset_password_with_invalid_request(ChangePasswordRequest request) throws Exception {
    mockMvc.perform(post(Endpoints.Guest.RESET_PASSWORD)
            .param(PARAM_TOKEN_STRING, request.getTokenString())
            .param(PARAM_PASSWORD, request.getNewPlainPassword())
            .param(PARAM_VERIFY_PASSWORD, request.getVerifyNewPlainPassword()))
            .andExpect(flash().attribute(ResetPasswordController.FORM_DTO, instanceOf(ChangePasswordRequest.class)))
            .andExpect(flash().attribute(BindingResult.class.getName() + "." + ResetPasswordController.FORM_DTO, instanceOf(BindingResult.class)))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl(Endpoints.Guest.RESET_PASSWORD + "?token=" + request.getTokenString()));

    verifyZeroInteractions(tokenService, userService);
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
