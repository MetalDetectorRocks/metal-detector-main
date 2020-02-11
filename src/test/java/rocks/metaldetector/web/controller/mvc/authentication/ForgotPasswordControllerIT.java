package rocks.metaldetector.web.controller.mvc.authentication;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
import rocks.metaldetector.config.constants.Endpoints;
import rocks.metaldetector.config.constants.MessageKeys;
import rocks.metaldetector.config.constants.ViewNames;
import rocks.metaldetector.model.user.events.OnResetPasswordRequestCompleteEvent;
import rocks.metaldetector.service.user.UserService;
import rocks.metaldetector.testutil.WithIntegrationTestProfile;
import rocks.metaldetector.web.DtoFactory.UserDtoFactory;
import rocks.metaldetector.web.dto.UserDto;

import java.util.Locale;
import java.util.Optional;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@ExtendWith(MockitoExtension.class)
class ForgotPasswordControllerIT implements WithAssertions, WithIntegrationTestProfile {

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
    // Since ApplicationEventPublisher is not a bean, we cannot work with @WebMvcTest at this point and have to mock the controller
    // we want to test ourselves and initialize the MockMvc itself.
    mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
  }

  @AfterEach
  void tearDown() {
    reset(userService, messages);
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
    UserDto userDto = UserDtoFactory.withUsernameAndEmail("JohnD", EXISTING_EMAIL);
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
  @DisplayName("Request a password reset with an invalid username should be a bad request")
  void request_password_reset_for_invalid_username_should_be_bad_request() throws Exception {
    // when
    mockMvc.perform(post(Endpoints.Guest.FORGOT_PASSWORD).param("emailOrUsername", (String) null))
            .andExpect(model().errorCount(1))
            .andExpect(model().attributeHasFieldErrorCode(ForgotPasswordController.FORM_DTO, "emailOrUsername", "NotBlank"))
            .andExpect(status().isBadRequest())
            .andExpect(view().name(ViewNames.Guest.FORGOT_PASSWORD));

    // then
    verifyZeroInteractions(userService);
    verifyZeroInteractions(messages);
    verifyZeroInteractions(eventPublisher);
  }

  @Test
  @DisplayName("Request a password reset for a not existing user should be a bad request")
  void request_password_reset_for_not_existing_user_should_be_bad_request() throws Exception {
    // given
    when(userService.getUserByEmailOrUsername(NOT_EXISTING_EMAIL)).thenReturn(Optional.empty());
    when(messages.getMessage(MessageKeys.ForgotPassword.USER_DOES_NOT_EXIST, null, Locale.US)).thenReturn("user does not exist");

    // when
    mockMvc.perform(post(Endpoints.Guest.FORGOT_PASSWORD).param("emailOrUsername", NOT_EXISTING_EMAIL))
            .andExpect(model().errorCount(1))
            .andExpect(model().attributeHasFieldErrorCode(ForgotPasswordController.FORM_DTO, "emailOrUsername", "UserDoesNotExist"))
            .andExpect(status().isBadRequest())
            .andExpect(view().name(ViewNames.Guest.FORGOT_PASSWORD));

    // then
    verify(userService, times(1)).getUserByEmailOrUsername(NOT_EXISTING_EMAIL);
    verify(messages, times(1)).getMessage(MessageKeys.ForgotPassword.USER_DOES_NOT_EXIST, null, Locale.US);
    verifyZeroInteractions(eventPublisher);
  }

}
