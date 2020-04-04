package rocks.metaldetector.web.controller.mvc.authentication;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import rocks.metaldetector.config.constants.Endpoints;
import rocks.metaldetector.config.constants.ViewNames;
import rocks.metaldetector.service.exceptions.TokenExpiredException;
import rocks.metaldetector.service.exceptions.UserAlreadyExistsException;
import rocks.metaldetector.service.token.TokenService;
import rocks.metaldetector.service.user.OnRegistrationCompleteEvent;
import rocks.metaldetector.service.user.UserDto;
import rocks.metaldetector.service.user.UserService;
import rocks.metaldetector.support.exceptions.ResourceNotFoundException;
import rocks.metaldetector.testutil.DtoFactory.RegisterUserRequestFactory;
import rocks.metaldetector.testutil.DtoFactory.UserDtoFactory;
import rocks.metaldetector.testutil.WithExceptionResolver;
import rocks.metaldetector.web.RestAssuredMockMvcUtils;
import rocks.metaldetector.web.api.request.RegisterUserRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@ExtendWith(MockitoExtension.class)
class RegistrationControllerTest implements WithAssertions, WithExceptionResolver {

  private static final String PARAM_USERNAME = "username";
  private static final String PARAM_EMAIL = "email";
  private static final String PARAM_PASSWORD = "plainPassword";
  private static final String PARAM_VERIFY_PASSWORD = "verifyPlainPassword";
  private static final String NOT_EXISTING_TOKEN = "not_existing_token";
  private static final String EXPIRED_TOKEN = "expired_token";

  private Map<String, String> paramValues = new HashMap<>();

  @Mock
  private UserService userService;

  @Mock
  private TokenService tokenService;

  @Mock
  private ApplicationEventPublisher eventPublisher;

  @Spy
  private ModelMapper modelMapper;

  @InjectMocks
  private RegistrationController underTest;

  private RestAssuredMockMvcUtils restAssuredUtils;
  private ObjectMapper objectMapper;

  @BeforeEach
  void setup() {
    restAssuredUtils = new RestAssuredMockMvcUtils(Endpoints.Guest.REGISTER);
    RestAssuredMockMvc.standaloneSetup(underTest,
                                       springSecurity((request, response, chain) -> chain.doFilter(request, response)),
                                       exceptionResolver());

    objectMapper = new ObjectMapper();
    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

    // create valid param map per default
    paramValues.put(PARAM_USERNAME, "JohnD");
    paramValues.put(PARAM_EMAIL, "john.d@example.com");
    paramValues.put(PARAM_PASSWORD, "valid-password");
    paramValues.put(PARAM_VERIFY_PASSWORD, "valid-password");

  }

  @AfterEach
  void tearDown() {
    paramValues.clear();
    reset(userService, tokenService, eventPublisher, modelMapper);
  }

  @Test
  @DisplayName("Requesting '" + Endpoints.Guest.REGISTER + "' should return the view to register")
  void given_register_uri_should_return_register_view() {
    // when
    var validatableResponse = restAssuredUtils.doGet();

    // then
    validatableResponse.assertThat(model().hasNoErrors())
        .assertThat(model().attributeExists(RegistrationController.FORM_DTO))
        .assertThat(status().isOk())
        .assertThat(view().name(ViewNames.Guest.REGISTER));
  }

  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  @DisplayName("Testing registration of a new user account")
  class RegisterNewUserAccountTest {

    @Test
    @DisplayName("Register a new user account with a valid request should be ok")
    void register_new_user_account_should_be_ok() {
      // given
      UserDto userDto = UserDtoFactory.withUsernameAndEmail("JohnD", "john.d@example.com");
      when(userService.createUser(any(UserDto.class))).thenReturn(userDto);

      // when
      var validatableResponse = restAssuredUtils.doPost(paramValues, ContentType.HTML);

      // then
      validatableResponse.assertThat(model().hasNoErrors())
          .assertThat(model().attributeExists(RegistrationController.FORM_DTO, "isSuccessful"))
          .assertThat(status().isOk())
          .assertThat(view().name(ViewNames.Guest.REGISTER));
    }

    @Test
    @DisplayName("Register a new user account with a valid request should call UserService")
    void register_new_user_account_should_call_user_service() {
      // given
      UserDto userDto = UserDtoFactory.withUsernameAndEmail("JohnD", "john.d@example.com");
      when(userService.createUser(any(UserDto.class))).thenReturn(userDto);

      // when
      restAssuredUtils.doPost(paramValues, ContentType.HTML);

      // then
      verify(userService, times(1)).createUser(any());
    }

    @Test
    @DisplayName("Register a new user account with a valid request should call EventPublisher")
    void register_new_user_account_should_call_event_publisher() {
      // given
      UserDto userDto = UserDtoFactory.withUsernameAndEmail("JohnD", "john.d@example.com");
      when(userService.createUser(any(UserDto.class))).thenReturn(userDto);

      // when
      restAssuredUtils.doPost(paramValues, ContentType.HTML);

      // when
      verify(eventPublisher, times(1)).publishEvent(any());
    }

    @Test
    @DisplayName("Register a new user account with a valid request should call ModelMapper")
    void register_new_user_account_should_call_model_mapper() {
      // given
      UserDto userDto = UserDtoFactory.withUsernameAndEmail("JohnD", "john.d@example.com");
      when(userService.createUser(any(UserDto.class))).thenReturn(userDto);

      // when
      restAssuredUtils.doPost(paramValues, ContentType.HTML);

      // when
      verify(modelMapper, times(1)).map(objectMapper.convertValue(paramValues, RegisterUserRequest.class), UserDto.class);
    }

    @Test
    @DisplayName("Register a new user account with a valid request should create correct user")
    void created_user_should_be_correct() {
      // given
      UserDto userDto = UserDtoFactory.withUsernameAndEmail("JohnD", "john.d@example.com");
      when(userService.createUser(any(UserDto.class))).thenReturn(userDto);
      ArgumentCaptor<UserDto> userDtoCaptor = ArgumentCaptor.forClass(UserDto.class);

      // when
      restAssuredUtils.doPost(paramValues, ContentType.HTML);

      // when
      verify(userService).createUser(userDtoCaptor.capture());
      assertThat(userDtoCaptor.getValue().getPublicId()).isNullOrEmpty();
      assertThat(userDtoCaptor.getValue().getUsername()).isEqualTo(paramValues.get(PARAM_USERNAME));
      assertThat(userDtoCaptor.getValue().getEmail()).isEqualTo(paramValues.get(PARAM_EMAIL));
      assertThat(userDtoCaptor.getValue().getPlainPassword()).isEqualTo(paramValues.get(PARAM_PASSWORD));
    }

    @Test
    @DisplayName("Register a new user account with a valid request should pass returned dto to EventPublisher")
    void user_dto_should_be_passed() {
      // given
      UserDto userDto = UserDtoFactory.withUsernameAndEmail("JohnD", "john.d@example.com");
      when(userService.createUser(any(UserDto.class))).thenReturn(userDto);
      ArgumentCaptor<OnRegistrationCompleteEvent> eventCaptor = ArgumentCaptor.forClass(OnRegistrationCompleteEvent.class);

      // when
      restAssuredUtils.doPost(paramValues, ContentType.HTML);

      // when
      verify(eventPublisher, times(1)).publishEvent(eventCaptor.capture());
      assertThat(eventCaptor.getValue().getUserDto()).isEqualTo(userDto);
    }

    @ParameterizedTest(name = "[{index}]: {0}")
    @MethodSource("registerUserRequestProvider")
    @DisplayName("Register a new user account with an invalid request dto should fail")
    void register_new_user_account_with_invalid_request_dto_should_fail(RegisterUserRequest request, int expectedErrorCount, String[] incorrectFieldNames) {
      // when
      var validatableResponse = restAssuredUtils.doPost(objectMapper.convertValue(request, new TypeReference<>() {}), ContentType.HTML);

      // then
      validatableResponse
          .assertThat(model().errorCount(expectedErrorCount))
          .assertThat(model().attributeHasFieldErrors(RegistrationController.FORM_DTO, incorrectFieldNames))
          .assertThat(status().isBadRequest())
          .assertThat(view().name(ViewNames.Guest.REGISTER));
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
    void register_user_account_with_username_that_already_exists_should_fail() {
      // given
      when(userService.createUser(any(UserDto.class))).thenThrow(UserAlreadyExistsException.createUserWithUsernameAlreadyExistsException());

      // when
      var validatableResponse = restAssuredUtils.doPost(paramValues, ContentType.HTML);

      // then
      validatableResponse
          .assertThat(model().errorCount(1))
          .assertThat(model().attributeHasFieldErrors(RegistrationController.FORM_DTO, PARAM_USERNAME))
          .assertThat(status().isBadRequest())
          .assertThat(view().name(ViewNames.Guest.REGISTER));
    }

    @Test
    @DisplayName("Register a new user account with an email that already exists should fail")
    void register_user_account_with_email_that_already_exists_should_fail() {
      // given
      when(userService.createUser(any(UserDto.class))).thenThrow(UserAlreadyExistsException.createUserWithEmailAlreadyExistsException());

      // when
      var validatableResponse = restAssuredUtils.doPost(paramValues, ContentType.HTML);

      // then
      validatableResponse
          .assertThat(model().errorCount(1))
          .assertThat(model().attributeHasFieldErrors(RegistrationController.FORM_DTO, PARAM_EMAIL))
          .assertThat(status().isBadRequest())
          .assertThat(view().name(ViewNames.Guest.REGISTER));
    }
  }

  @Nested
  @DisplayName("Testing verification of a created user account")
  class VerifyRegistrationTest {

    @BeforeEach
    void setup() {
      restAssuredUtils = new RestAssuredMockMvcUtils(Endpoints.Guest.REGISTRATION_VERIFICATION);
    }

    @Test
    @DisplayName("Requesting '" + Endpoints.Guest.REGISTRATION_VERIFICATION + "' with valid token should return the login view with success message")
    void given_valid_token_on_registration_verification_uri_should_redirect_to_login_view() {
      // when
      var validatableResponse = restAssuredUtils.doGet("?token=valid_token");

      // then
      validatableResponse
          .assertThat(model().hasNoErrors())
          .assertThat(status().is3xxRedirection())
          .assertThat(redirectedUrl(Endpoints.Guest.LOGIN + "?verificationSuccess"));
    }

    @Test
    @DisplayName("Requesting '" + Endpoints.Guest.REGISTRATION_VERIFICATION + "' with valid token should call UserService")
    void given_valid_token_on_registration_verification_uri_should_call_user_service() {
      //given
      String token = "valid_token";

      // when
      restAssuredUtils.doGet("?token=" + token);

      // then
      verify(userService, times(1)).verifyEmailToken(token);
    }

    @Test
    @DisplayName("Requesting '" + Endpoints.Guest.REGISTRATION_VERIFICATION + "' with not existing token should return the login view login with error message")
    void given_not_existing_token_on_registration_verification_uri_should_redirect_to_login_view() {
      // given
      doThrow(ResourceNotFoundException.class).when(userService).verifyEmailToken(NOT_EXISTING_TOKEN);

      // when
      var validatableResponse = restAssuredUtils.doGet("?token=" + NOT_EXISTING_TOKEN);

      // then
      validatableResponse
          .assertThat(status().is3xxRedirection())
          .assertThat(redirectedUrl(Endpoints.Guest.LOGIN + "?tokenNotFound"));
    }

    @Test
    @DisplayName("Requesting '" + Endpoints.Guest.REGISTRATION_VERIFICATION + "' with not existing token should call UserService")
    void given_not_existing_token_on_registration_verification_uri_should_call_user_service() {
      // given
      doThrow(ResourceNotFoundException.class).when(userService).verifyEmailToken(NOT_EXISTING_TOKEN);

      // when
      restAssuredUtils.doGet("?token=" + NOT_EXISTING_TOKEN);

      // then
      verify(userService, times(1)).verifyEmailToken(NOT_EXISTING_TOKEN);
    }

    @Test
    @DisplayName("Requesting '" + Endpoints.Guest.REGISTRATION_VERIFICATION + "' with expired token should return the login view with error message")
    void given_expired_token_on_registration_verification_uri_should_redirect_to_login_view() {
      // given
      doThrow(TokenExpiredException.class).when(userService).verifyEmailToken(EXPIRED_TOKEN);

      // when
      var validatableResponse = restAssuredUtils.doGet("?token=" + EXPIRED_TOKEN);

      // then
      validatableResponse
          .assertThat(status().is3xxRedirection())
          .assertThat(redirectedUrl(Endpoints.Guest.LOGIN + "?tokenExpired&token=" + EXPIRED_TOKEN));
    }

    @Test
    @DisplayName("Requesting '" + Endpoints.Guest.REGISTRATION_VERIFICATION + "' with expired token should call UserService")
    void given_expired_token_on_registration_verification_uri_should_call_user_service() {
      // given
      doThrow(TokenExpiredException.class).when(userService).verifyEmailToken(EXPIRED_TOKEN);

      // when
      restAssuredUtils.doGet("?token=" + EXPIRED_TOKEN);

      // then
      verify(userService, times(1)).verifyEmailToken(EXPIRED_TOKEN);
    }
  }

  @Nested
  @DisplayName("Testing resend email verification token")
  class ResendEmailVerificationTokenTest {

    @BeforeEach
    void setup() {
      restAssuredUtils = new RestAssuredMockMvcUtils(Endpoints.Guest.RESEND_VERIFICATION_TOKEN);
    }

    @Test
    @DisplayName("Requesting '" + Endpoints.Guest.RESEND_VERIFICATION_TOKEN + "' with valid token should return the login view with success message")
    void given_valid_token_on_resend_verification_token_uri_should_redirect_to_login_view() {
      // when
      var validatableResponse = restAssuredUtils.doGet("?token=valid-token");

      // then
      validatableResponse
          .assertThat(status().is3xxRedirection())
          .assertThat(redirectedUrl(Endpoints.Guest.LOGIN + "?resendVerificationTokenSuccess"));
    }

    @Test
    @DisplayName("Requesting '" + Endpoints.Guest.RESEND_VERIFICATION_TOKEN + "' with valid token should call TokenService")
    void given_valid_token_on_resend_verification_token_uri_should_call_token_service() {
      // given
      String token = "valid-token";

      // when
      restAssuredUtils.doGet("?token=" + token);

      // then
      verify(tokenService, times(1)).resendExpiredEmailVerificationToken(token);
    }

    @Test
    @DisplayName("Requesting '" + Endpoints.Guest.RESEND_VERIFICATION_TOKEN + "' with not existing token should return the login view with token not found message")
    void given_not_existing_token_on_resend_verification_token_uri_should_redirect_to_login_view() {
      // given
      doThrow(ResourceNotFoundException.class).when(tokenService).resendExpiredEmailVerificationToken(NOT_EXISTING_TOKEN);

      // when
      var validatableResponse = restAssuredUtils.doGet("?token=" + NOT_EXISTING_TOKEN);

      // then
      validatableResponse
          .assertThat(status().is3xxRedirection())
          .assertThat(redirectedUrl(Endpoints.Guest.LOGIN + "?tokenNotFound"));
    }

    @Test
    @DisplayName("Requesting '" + Endpoints.Guest.RESEND_VERIFICATION_TOKEN + "' with not existing should call TokenService")
    void given_not_existing_token_on_resend_verification_token_uri_should_call_token_service() {
      // given
      doThrow(ResourceNotFoundException.class).when(tokenService).resendExpiredEmailVerificationToken(NOT_EXISTING_TOKEN);

      // when
      restAssuredUtils.doGet("?token=" + NOT_EXISTING_TOKEN);

      // then
      verify(tokenService, times(1)).resendExpiredEmailVerificationToken(NOT_EXISTING_TOKEN);
    }
  }
}
