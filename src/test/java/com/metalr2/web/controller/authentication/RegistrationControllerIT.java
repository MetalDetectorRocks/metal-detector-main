package com.metalr2.web.controller.authentication;

import com.metalr2.config.constants.Endpoints;
import com.metalr2.config.constants.MessageKeys;
import com.metalr2.config.constants.ViewNames;
import com.metalr2.model.exceptions.EmailVerificationTokenExpiredException;
import com.metalr2.model.exceptions.ResourceNotFoundException;
import com.metalr2.model.exceptions.UserAlreadyExistsException;
import com.metalr2.model.user.events.OnRegistrationCompleteEvent;
import com.metalr2.service.token.TokenService;
import com.metalr2.service.user.UserService;
import com.metalr2.web.DtoFactory.RegisterUserRequestFactory;
import com.metalr2.web.DtoFactory.UserDtoFactory;
import com.metalr2.web.dto.UserDto;
import com.metalr2.web.dto.request.RegisterUserRequest;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@Tag("integration-test")
class RegistrationControllerIT implements WithAssertions {

  private static final String PARAM_USERNAME          = "username";
  private static final String PARAM_EMAIL             = "email";
  private static final String PARAM_PASSWORD          = "plainPassword";
  private static final String PARAM_VERIFY_PASSWORD   = "verifyPlainPassword";
  private static final String NOT_EXISTING_TOKEN      = "not_existing_token";
  private static final String EXPIRED_TOKEN           = "expired_token";

  private Map<String, String> paramValues = new HashMap<>();

  @Mock
  private UserService userService;

  @Mock
  private TokenService tokenService;

  @Mock
  private ApplicationEventPublisher eventPublisher;

  @Mock
  private MessageSource messages;

  @InjectMocks
  private RegistrationController controller;

  private MockMvc mockMvc;

  @BeforeEach
  void setup(){
    // Since ApplicationEventPublisher is not a bean, we cannot work with @WebMvcTest at this point and have to mock the controller
    // we want to test ourselves and initialize the MockMvc itself.
    mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

    // create valid param map per default
    paramValues.put(PARAM_USERNAME, "JohnD");
    paramValues.put(PARAM_EMAIL, "john.d@example.com");
    paramValues.put(PARAM_PASSWORD, "valid-password");
    paramValues.put(PARAM_VERIFY_PASSWORD, "valid-password");
  }

  @AfterEach
  void tearDown() {
    paramValues.clear();
    reset(userService, tokenService, messages);
  }

  @Test
  @DisplayName("Requesting '" + Endpoints.Guest.REGISTER + "' should return the view to register")
  void given_register_uri_should_return_register_view() throws Exception {
    mockMvc.perform(get(Endpoints.Guest.REGISTER))
            .andExpect(model().hasNoErrors())
            .andExpect(model().attributeExists(RegistrationController.FORM_DTO))
            .andExpect(status().isOk())
            .andExpect(view().name(ViewNames.Guest.REGISTER));
  }

  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  @DisplayName("Testing registration of a new user account")
  class RegisterNewUserAccountTest {

    @Test
    @DisplayName("Register a new user account with a valid request should be ok")
    void register_new_user_account_should_be_ok() throws Exception {
      UserDto userDto = UserDtoFactory.withUsernameAndEmail("JohnD", "john.d@example.com");
      when(userService.createUser(any(UserDto.class))).thenReturn(userDto);
      when(messages.getMessage(MessageKeys.Registration.SUCCESS, null, Locale.US)).thenReturn("success message");

      ArgumentCaptor<UserDto> userDtoCaptor = ArgumentCaptor.forClass(UserDto.class);
      ArgumentCaptor<OnRegistrationCompleteEvent> eventCaptor = ArgumentCaptor.forClass(OnRegistrationCompleteEvent.class);

      mockMvc.perform(createRequest())
              .andExpect(model().hasNoErrors())
              .andExpect(model().attributeExists(RegistrationController.FORM_DTO, "successMessage"))
              .andExpect(model().attributeExists(RegistrationController.FORM_DTO, "registerUserRequest"))
              .andExpect(status().isOk())
              .andExpect(view().name(ViewNames.Guest.REGISTER));

      verify(userService, times(1)).createUser(userDtoCaptor.capture());
      assertThat(userDtoCaptor.getValue().getId()).isEqualTo(0);
      assertThat(userDtoCaptor.getValue().getPublicId()).isNullOrEmpty();
      assertThat(userDtoCaptor.getValue().getUsername()).isEqualTo(paramValues.get(PARAM_USERNAME));
      assertThat(userDtoCaptor.getValue().getEmail()).isEqualTo(paramValues.get(PARAM_EMAIL));
      assertThat(userDtoCaptor.getValue().getPlainPassword()).isEqualTo(paramValues.get(PARAM_PASSWORD));

      verify(eventPublisher, times(1)).publishEvent(eventCaptor.capture());
      assertThat(eventCaptor.getValue().getUserDto()).isEqualTo(userDto);

      verify(messages, times(1)).getMessage(MessageKeys.Registration.SUCCESS, null, Locale.US);
    }

    @ParameterizedTest(name = "[{index}]: {0}")
    @MethodSource("registerUserRequestProvider")
    @DisplayName("Register a new user account with an invalid request dto should fail")
    void register_new_user_account_with_invalid_request_dto_should_fail(RegisterUserRequest request, int expectedErrorCount, String[] incorrectFieldNames) throws Exception {
      paramValues.put(PARAM_USERNAME, request.getUsername());
      paramValues.put(PARAM_EMAIL, request.getEmail());
      paramValues.put(PARAM_PASSWORD, request.getPlainPassword());
      paramValues.put(PARAM_VERIFY_PASSWORD, request.getVerifyPlainPassword());

      mockMvc.perform(createRequest())
              .andExpect(model().errorCount(expectedErrorCount))
              .andExpect(model().attributeHasFieldErrors(RegistrationController.FORM_DTO, incorrectFieldNames))
              .andExpect(status().isBadRequest())
              .andExpect(view().name(ViewNames.Guest.REGISTER));
    }

    private Stream<Arguments> registerUserRequestProvider() {
      return Stream.of(
              // invalid username
              Arguments.of(RegisterUserRequestFactory.withUsername(""), 1, new String[] {PARAM_USERNAME}),
              Arguments.of(RegisterUserRequestFactory.withUsername("    "), 1, new String[] {PARAM_USERNAME}),
              Arguments.of(RegisterUserRequestFactory.withUsername(null), 1, new String[] {PARAM_USERNAME}),

              // invalid email
              Arguments.of(RegisterUserRequestFactory.withEmail("john.doe.example.de"), 1, new String[] {PARAM_EMAIL}),
              Arguments.of(RegisterUserRequestFactory.withEmail(""), 1, new String[] {PARAM_EMAIL}),
              Arguments.of(RegisterUserRequestFactory.withEmail("    "), 2, new String[] {PARAM_EMAIL}),
              Arguments.of(RegisterUserRequestFactory.withEmail("@com"), 1, new String[] {PARAM_EMAIL}),
              Arguments.of(RegisterUserRequestFactory.withEmail(null), 1, new String[] {PARAM_EMAIL}),

              // invalid passwords
              Arguments.of(RegisterUserRequestFactory.withPassword("secret-password", "other-secret-password"), 1, new String[] {}),
              Arguments.of(RegisterUserRequestFactory.withPassword("secret", "secret"), 2, new String[] {PARAM_PASSWORD, PARAM_VERIFY_PASSWORD}),
              Arguments.of(RegisterUserRequestFactory.withPassword("", ""), 4, new String[] {PARAM_PASSWORD, PARAM_VERIFY_PASSWORD}),
              Arguments.of(RegisterUserRequestFactory.withPassword(null, null), 2, new String[] {PARAM_VERIFY_PASSWORD})
      );
    }

    @Test
    @DisplayName("Register a new user account with an username that already exists should fail")
    void register_user_account_with_username_that_already_exists_should_fail() throws Exception {
      when(userService.createUser(any(UserDto.class))).thenThrow(UserAlreadyExistsException.createUserWithUsernameAlreadyExistsException());

      mockMvc.perform(createRequest())
              .andExpect(model().errorCount(1))
              .andExpect(model().attributeHasFieldErrors(RegistrationController.FORM_DTO, PARAM_USERNAME))
              .andExpect(status().isBadRequest())
              .andExpect(view().name(ViewNames.Guest.REGISTER));
    }

    @Test
    @DisplayName("Register a new user account with an email that already exists should fail")
    void register_user_account_with_email_that_already_exists_should_fail() throws Exception {
      when(userService.createUser(any(UserDto.class))).thenThrow(UserAlreadyExistsException.createUserWithEmailAlreadyExistsException());

      mockMvc.perform(createRequest())
              .andExpect(model().errorCount(1))
              .andExpect(model().attributeHasFieldErrors(RegistrationController.FORM_DTO, PARAM_EMAIL))
              .andExpect(status().isBadRequest())
              .andExpect(view().name(ViewNames.Guest.REGISTER));
    }

    private MockHttpServletRequestBuilder createRequest() {
      return MockMvcRequestBuilders
              .post(Endpoints.Guest.REGISTER)
              .accept(MediaType.TEXT_HTML)
              .param(PARAM_USERNAME, paramValues.get(PARAM_USERNAME))
              .param(PARAM_EMAIL, paramValues.get(PARAM_EMAIL))
              .param(PARAM_PASSWORD, paramValues.get(PARAM_PASSWORD))
              .param(PARAM_VERIFY_PASSWORD, paramValues.get(PARAM_VERIFY_PASSWORD));
    }

  }

  @Nested
  @DisplayName("Testing verification of a created user account")
  class VerifyRegistrationTest {

    @Test
    @DisplayName("Requesting '" + Endpoints.Guest.REGISTRATION_VERIFICATION + "' with valid token should return the login view with success message")
    void given_valid_token_on_registration_verification_uri_should_redirect_to_login_view() throws Exception {
      String token = "valid_token";

      mockMvc.perform(get(Endpoints.Guest.REGISTRATION_VERIFICATION + "?token={token}", token))
              .andExpect(model().hasNoErrors())
              .andExpect(status().is3xxRedirection())
              .andExpect(redirectedUrl(Endpoints.Guest.LOGIN + "?verificationSuccess"));

      verify(userService, times(1)).verifyEmailToken(token);
    }

    @Test
    @DisplayName("Requesting '" + Endpoints.Guest.REGISTRATION_VERIFICATION + "' with not existing token should return the login view login with error message")
    void given_not_existing_token_on_registration_verification_uri_should_redirect_to_login_view() throws Exception {
      doThrow(ResourceNotFoundException.class).when(userService).verifyEmailToken(NOT_EXISTING_TOKEN);

      mockMvc.perform(get(Endpoints.Guest.REGISTRATION_VERIFICATION + "?token=" + NOT_EXISTING_TOKEN))
              .andExpect(status().is3xxRedirection())
              .andExpect(redirectedUrl(Endpoints.Guest.LOGIN + "?tokenNotFound"));

      verify(userService, times(1)).verifyEmailToken(NOT_EXISTING_TOKEN);
    }

    @Test
    @DisplayName("Requesting '" + Endpoints.Guest.REGISTRATION_VERIFICATION + "' with expired token should return the login view with error message")
    void given_expired_token_on_registration_verification_uri_should_redirect_to_login_view() throws Exception {
      doThrow(EmailVerificationTokenExpiredException.class).when(userService).verifyEmailToken(EXPIRED_TOKEN);

      mockMvc.perform(get(Endpoints.Guest.REGISTRATION_VERIFICATION + "?token=" + EXPIRED_TOKEN))
              .andExpect(status().is3xxRedirection())
              .andExpect(redirectedUrl(Endpoints.Guest.LOGIN + "?tokenExpired&token=" + EXPIRED_TOKEN));

      verify(userService, times(1)).verifyEmailToken(EXPIRED_TOKEN);
    }

  }

  @Nested
  @DisplayName("Testing resend email verification token")
  class ResendEmailVerificationTokenTest {

    @Test
    @DisplayName("Requesting '" + Endpoints.Guest.RESEND_VERIFICATION_TOKEN + "' with valid token should return the login view with success message")
    void given_valid_token_on_resend_verification_token_uri_should_redirect_to_login_view() throws Exception {
      final String VALID_TOKEN = "valid-token";

      mockMvc.perform(get(Endpoints.Guest.RESEND_VERIFICATION_TOKEN + "?token=" + VALID_TOKEN))
              .andExpect(status().is3xxRedirection())
              .andExpect(redirectedUrl(Endpoints.Guest.LOGIN + "?resendVerificationTokenSuccess"));

      verify(tokenService, times(1)).resendExpiredEmailVerificationToken(VALID_TOKEN);
    }

    @Test
    @DisplayName("Requesting '" + Endpoints.Guest.RESEND_VERIFICATION_TOKEN + "' with valid expired should return the login view with token not found message")
    void given_not_existing_token_on_resend_verification_token_uri_should_redirect_to_login_view() throws Exception {
      doThrow(ResourceNotFoundException.class).when(tokenService).resendExpiredEmailVerificationToken(NOT_EXISTING_TOKEN);

      mockMvc.perform(get(Endpoints.Guest.RESEND_VERIFICATION_TOKEN + "?token=" + NOT_EXISTING_TOKEN))
              .andExpect(status().is3xxRedirection())
              .andExpect(redirectedUrl(Endpoints.Guest.LOGIN + "?tokenNotFound"));

      verify(tokenService, times(1)).resendExpiredEmailVerificationToken(NOT_EXISTING_TOKEN);
    }

  }

}
