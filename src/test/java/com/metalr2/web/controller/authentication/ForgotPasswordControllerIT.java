package com.metalr2.web.controller.authentication;

import com.metalr2.config.constants.Endpoints;
import com.metalr2.config.constants.MessageKeys;
import com.metalr2.config.constants.ViewNames;
import com.metalr2.model.user.events.OnResetPasswordRequestCompleteEvent;
import com.metalr2.service.user.UserService;
import com.metalr2.web.DtoFactory;
import com.metalr2.web.dto.UserDto;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Locale;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(MockitoExtension.class)
@Tag("integration-test")
class ForgotPasswordControllerIT implements WithAssertions {

  private static final String EXISTING_EMAIL = "john.doe@example.com";
  private static final String NOT_EXISTING_EMAIL = "not.existing@example.com";

  @Mock
  private UserService userService;

  @Mock
  private ApplicationEventPublisher eventPublisher;

  @Mock
  private MessageSource messages;

  @InjectMocks
  private ForgotPasswordController controller;

  private MockMvc mockMvc;

  @BeforeEach
  void setup() {
    mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
  }

  @Test
  @DisplayName("Requesting '" + Endpoints.Guest.FORGOT_PASSWORD + "' should return the view to request a new password")
  void given_forgot_password_uri_should_return_forgot_password_view() throws Exception {
    mockMvc.perform(get(Endpoints.Guest.FORGOT_PASSWORD))
            .andExpect(status().isOk())
            .andExpect(view().name(ViewNames.Guest.FORGOT_PASSWORD))
            .andExpect(model().attributeExists(ForgotPasswordController.FORM_DTO));
  }

  @Test
  @DisplayName("Request a password reset for an existing user should be ok")
  void request_password_reset_for_existing_user_should_be_ok() throws Exception {
    // given
    ArgumentCaptor<OnResetPasswordRequestCompleteEvent> eventCaptor = ArgumentCaptor.forClass(OnResetPasswordRequestCompleteEvent.class);
    UserDto userDto = DtoFactory.createUser("JohnD", EXISTING_EMAIL);
    when(userService.getUserByEmailOrUsername(EXISTING_EMAIL)).thenReturn(Optional.of(userDto));
    when(messages.getMessage(MessageKeys.ForgotPassword.SUCCESS, null, Locale.US)).thenReturn("success message");

    // when
    mockMvc.perform(post(Endpoints.Guest.FORGOT_PASSWORD).param("emailOrUsername", EXISTING_EMAIL))
            .andExpect(status().isOk())
            .andExpect(view().name(ViewNames.Guest.FORGOT_PASSWORD))
            .andExpect(model().attributeExists(ForgotPasswordController.FORM_DTO, "successMessage"))
            .andExpect(model().hasNoErrors());

    // then
    verify(userService, times(1)).getUserByEmailOrUsername(EXISTING_EMAIL);
    verify(messages, times(1)).getMessage(MessageKeys.ForgotPassword.SUCCESS, null, Locale.US);
    verify(eventPublisher, times(1)).publishEvent(eventCaptor.capture());
    assertThat(eventCaptor.getValue().getUserDto()).isEqualTo(userDto);
  }

  @Test
  @DisplayName("Request a password reset for a not existing user should be a bad request")
  void request_password_reset_for_not_existing_user_should_be_bad_request() throws Exception {
    // given
    when(userService.getUserByEmailOrUsername(NOT_EXISTING_EMAIL)).thenReturn(Optional.empty());
    when(messages.getMessage(MessageKeys.ForgotPassword.USER_DOES_NOT_EXIST, null, Locale.US)).thenReturn("user does not exist");

    // when
    mockMvc.perform(post(Endpoints.Guest.FORGOT_PASSWORD).param("emailOrUsername", NOT_EXISTING_EMAIL))
            .andExpect(status().isBadRequest())
            .andExpect(view().name(ViewNames.Guest.FORGOT_PASSWORD))
            .andExpect(model().errorCount(1))
            .andExpect(model().attributeHasFieldErrorCode(ForgotPasswordController.FORM_DTO, "emailOrUsername", "userDoesNotExist"));

    // then
    verify(userService, times(1)).getUserByEmailOrUsername(NOT_EXISTING_EMAIL);
    verify(messages, times(1)).getMessage(MessageKeys.ForgotPassword.USER_DOES_NOT_EXIST, null, Locale.US);
    verifyZeroInteractions(eventPublisher);
  }

}
