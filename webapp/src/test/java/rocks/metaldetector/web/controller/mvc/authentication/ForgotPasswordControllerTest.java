package rocks.metaldetector.web.controller.mvc.authentication;

import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
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
import rocks.metaldetector.config.constants.Endpoints;
import rocks.metaldetector.config.constants.ViewNames;
import rocks.metaldetector.service.exceptions.RestExceptionsHandler;
import rocks.metaldetector.service.user.OnResetPasswordRequestCompleteEvent;
import rocks.metaldetector.service.user.UserDto;
import rocks.metaldetector.service.user.UserService;
import rocks.metaldetector.testutil.DtoFactory.UserDtoFactory;
import rocks.metaldetector.web.RestAssuredMockMvcUtils;

import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@ExtendWith(MockitoExtension.class)
class ForgotPasswordControllerTest implements WithAssertions {

  private static final String EXISTING_EMAIL = "john.doe@example.com";
  private static final String NOT_EXISTING_EMAIL = "not.existing@example.com";

  @Mock
  private UserService userService;

  @Mock
  private ApplicationEventPublisher eventPublisher;

  @InjectMocks
  private ForgotPasswordController underTest;

  private RestAssuredMockMvcUtils restAssuredUtils;

  @BeforeEach
  void setup() {
    restAssuredUtils = new RestAssuredMockMvcUtils(Endpoints.Guest.FORGOT_PASSWORD);
    RestAssuredMockMvc.standaloneSetup(underTest,
                                       springSecurity((request, response, chain) -> chain.doFilter(request, response)),
                                       RestExceptionsHandler.class);
  }

  @AfterEach
  void tearDown() {
    reset(userService);
  }

  @Test
  @DisplayName("GET on '" + Endpoints.Guest.FORGOT_PASSWORD + "' should return the view to request a new password")
  void get_should_return_forgot_password_view() {
    // when
    var validatableResponse = restAssuredUtils.doGet();

    // then
    validatableResponse
        .assertThat(status().isOk())
        .assertThat(view().name(ViewNames.Guest.FORGOT_PASSWORD))
        .assertThat(model().attributeExists(ForgotPasswordController.FORM_DTO))
        .assertThat(model().hasNoErrors());
  }

  @Test
  @DisplayName("POST on '" + Endpoints.Guest.FORGOT_PASSWORD + "' should return a model with correct attributes")
  void post_should_return_attributes() {
    // given
    UserDto userDto = UserDtoFactory.withUsernameAndEmail("JohnD", EXISTING_EMAIL);
    when(userService.getUserByEmailOrUsername(EXISTING_EMAIL)).thenReturn(Optional.of(userDto));

    // when
    var validatableResponse = restAssuredUtils.doPost(Map.of("emailOrUsername", EXISTING_EMAIL), ContentType.HTML);

    // then
    validatableResponse
        .assertThat(status().isOk())
        .assertThat(view().name(ViewNames.Guest.FORGOT_PASSWORD))
        .assertThat(model().attributeExists(ForgotPasswordController.FORM_DTO, "isSuccessful"))
        .assertThat(model().hasNoErrors());
  }

  @Test
  @DisplayName("POST on '" + Endpoints.Guest.FORGOT_PASSWORD + "' should call UserService")
  void post_should_call_user_service() {
    // given
    UserDto userDto = UserDtoFactory.withUsernameAndEmail("JohnD", EXISTING_EMAIL);
    when(userService.getUserByEmailOrUsername(EXISTING_EMAIL)).thenReturn(Optional.of(userDto));

    // when
    restAssuredUtils.doPost(Map.of("emailOrUsername", EXISTING_EMAIL), ContentType.HTML);

    // then
    verify(userService, times(1)).getUserByEmailOrUsername(EXISTING_EMAIL);
  }

  @Test
  @DisplayName("POST on '" + Endpoints.Guest.FORGOT_PASSWORD + "' should call EventPublisher")
  void post_should_call_event_publisher() {
    // given
    UserDto userDto = UserDtoFactory.withUsernameAndEmail("JohnD", EXISTING_EMAIL);
    when(userService.getUserByEmailOrUsername(EXISTING_EMAIL)).thenReturn(Optional.of(userDto));

    // when
    restAssuredUtils.doPost(Map.of("emailOrUsername", EXISTING_EMAIL), ContentType.HTML);

    // then
    verify(eventPublisher, times(1)).publishEvent(any());
  }

  @Test
  @DisplayName("POST on '" + Endpoints.Guest.FORGOT_PASSWORD + "' should return correct user dto")
  void post_should_return_dto() {
    // given
    ArgumentCaptor<OnResetPasswordRequestCompleteEvent> eventCaptor = ArgumentCaptor.forClass(OnResetPasswordRequestCompleteEvent.class);
    UserDto userDto = UserDtoFactory.withUsernameAndEmail("JohnD", EXISTING_EMAIL);
    when(userService.getUserByEmailOrUsername(EXISTING_EMAIL)).thenReturn(Optional.of(userDto));

    // when
    restAssuredUtils.doPost(Map.of("emailOrUsername", EXISTING_EMAIL), ContentType.HTML);

    // then
    verify(eventPublisher).publishEvent(eventCaptor.capture());
    assertThat(eventCaptor.getValue().getUserDto()).isEqualTo(userDto);
  }

  @Test
  @DisplayName("Request a password reset with an invalid username should be a bad request")
  void invalid_username_should_return_bad_request() {
    // when
    var validatableResponse = restAssuredUtils.doPost(Map.of("emailOrUsername", ""), ContentType.HTML);

    // then
    validatableResponse
        .assertThat(model().errorCount(1))
        .assertThat(model().attributeHasFieldErrorCode(ForgotPasswordController.FORM_DTO, "emailOrUsername", "NotBlank"))
        .assertThat(status().isBadRequest())
        .assertThat(view().name(ViewNames.Guest.FORGOT_PASSWORD));
  }

  @Test
  @DisplayName("Request a password reset with an invalid username should should not call UserService")
  void invalid_username_should_not_call_user_service() {
    // when
    restAssuredUtils.doPost(Map.of("emailOrUsername", ""), ContentType.HTML);

    // then
    verifyNoInteractions(userService);
  }

  @Test
  @DisplayName("Request a password reset with an invalid username should should not call EventPublisher")
  void invalid_username_should_not_call_event_publisher() {
    // when
    restAssuredUtils.doPost(Map.of("emailOrUsername", ""), ContentType.HTML);

    // then
    verifyNoInteractions(eventPublisher);
  }

  @Test
  @DisplayName("Request a password reset for a not existing user should be a bad request")
  void not_existing_user_should_return_bad_request() {
    // given
    when(userService.getUserByEmailOrUsername(NOT_EXISTING_EMAIL)).thenReturn(Optional.empty());

    // when
    var validatableResponse = restAssuredUtils.doPost(Map.of("emailOrUsername", NOT_EXISTING_EMAIL), ContentType.HTML);

    // then
    validatableResponse
        .assertThat(model().errorCount(1))
        .assertThat(model().attributeHasFieldErrorCode(ForgotPasswordController.FORM_DTO, "emailOrUsername", "UserDoesNotExist"))
        .assertThat(status().isBadRequest())
        .assertThat(view().name(ViewNames.Guest.FORGOT_PASSWORD));
  }

  @Test
  @DisplayName("Request a password reset for a not existing user should call UserService")
  void not_existing_user_should_call_user_service() {
    // given
    when(userService.getUserByEmailOrUsername(NOT_EXISTING_EMAIL)).thenReturn(Optional.empty());

    // when
    restAssuredUtils.doPost(Map.of("emailOrUsername", NOT_EXISTING_EMAIL), ContentType.HTML);

    // then
    verify(userService, times(1)).getUserByEmailOrUsername(NOT_EXISTING_EMAIL);
  }

  @Test
  @DisplayName("Request a password reset for a not existing user should not call EventPublisher")
  void not_existing_user_should_not_call_event_publisher() {
    // given
    when(userService.getUserByEmailOrUsername(NOT_EXISTING_EMAIL)).thenReturn(Optional.empty());

    // when
    restAssuredUtils.doPost(Map.of("emailOrUsername", NOT_EXISTING_EMAIL), ContentType.HTML);

    // then
    verifyNoInteractions(eventPublisher);
  }
}
