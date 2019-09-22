package com.metalr2.web.controller.authentication;

import com.metalr2.config.constants.MessageKeys;
import com.metalr2.config.constants.ViewNames;
import com.metalr2.model.user.events.OnResetPasswordRequestCompleteEvent;
import com.metalr2.service.user.UserService;
import com.metalr2.web.dto.UserDto;
import com.metalr2.web.dto.request.ForgotPasswordRequest;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;

import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ForgotPasswordControllerTest implements WithAssertions {

  private static final String EXISTING_EMAIL = "john.doe@example.com";
  private static final String NOT_EXISTING_EMAIL = "not.existing@example.com";

  @Mock private ApplicationEventPublisher eventPublisher;
  @Mock private UserService userService;
  @Mock private MessageSource messages;
  @Mock private BindingResult bindingResult;

  @InjectMocks
  private ForgotPasswordController controller;

  @BeforeEach
  void setup() {
    when(userService.getUserByEmailOrUsername(anyString())).thenAnswer(invocationOnMock -> {
      String email = invocationOnMock.getArgument(0);
      if (email.equals(EXISTING_EMAIL)) {
        return Optional.of(
                UserDto.builder()
                        .id(1)
                        .publicId(UUID.randomUUID().toString())
                        .username("JohnD")
                        .email(EXISTING_EMAIL)
                        .plainPassword("xxx")
                        .enabled(true)
                        .build());
      }
      else {
        return Optional.empty();
      }
    });
  }

  @Test
  void given_forgot_password_uri_should_return_forgot_password_view() {

  }

  @Test
  void request_password_reset_for_existing_user() {
    // given
    ForgotPasswordRequest request = ForgotPasswordRequest.builder().emailOrUsername(EXISTING_EMAIL).build();

    // when
    ModelAndView modelAndView = controller.requestPasswordReset(request, bindingResult);

    // then
    verify(userService, times(1)).getUserByEmailOrUsername(EXISTING_EMAIL);
    verifyZeroInteractions(bindingResult);
    verify(eventPublisher, times(1)).publishEvent(any(OnResetPasswordRequestCompleteEvent.class));
    verify(messages, times(1)).getMessage(MessageKeys.ForgotPassword.SUCCESS, null, Locale.US);
    assertThat(modelAndView.getStatus()).isEqualTo(HttpStatus.OK);
    assertThat(modelAndView.getViewName()).isEqualTo(ViewNames.Guest.FORGOT_PASSWORD);
    assertThat(modelAndView.getModel()).containsKeys("successMessage", "forgotPasswordRequest");
  }

  @Test
  void request_password_reset_for_not_existing_user() {
    // given
    ForgotPasswordRequest request = ForgotPasswordRequest.builder().emailOrUsername(NOT_EXISTING_EMAIL).build();
    when(messages.getMessage(MessageKeys.ForgotPassword.USER_DOES_NOT_EXIST, null, Locale.US)).thenReturn("user does not exist");

    // when
    ModelAndView modelAndView = controller.requestPasswordReset(request, bindingResult);

    // then
    verify(userService, times(1)).getUserByEmailOrUsername(NOT_EXISTING_EMAIL);
    verify(bindingResult, times(1)).rejectValue("emailOrUsername", "userDoesNotExist", "user does not exist");
    verify(messages, times(1)).getMessage(MessageKeys.ForgotPassword.USER_DOES_NOT_EXIST, null, Locale.US);
    verifyZeroInteractions(eventPublisher);
    assertThat(modelAndView.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(modelAndView.getViewName()).isEqualTo(ViewNames.Guest.FORGOT_PASSWORD);
    assertThat(modelAndView.getModel()).isEmpty();
  }

}
