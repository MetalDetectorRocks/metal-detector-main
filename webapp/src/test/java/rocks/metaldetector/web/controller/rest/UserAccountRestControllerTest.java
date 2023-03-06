package rocks.metaldetector.web.controller.rest;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import io.restassured.module.mockmvc.response.ValidatableMockMvcResponse;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.metaldetector.service.exceptions.RestExceptionsHandler;
import rocks.metaldetector.service.user.UserDto;
import rocks.metaldetector.service.user.UserService;
import rocks.metaldetector.testutil.DtoFactory.UserDtoFactory;
import rocks.metaldetector.web.RestAssuredMockMvcUtils;
import rocks.metaldetector.web.api.request.UpdateEmailRequest;
import rocks.metaldetector.web.api.request.UpdatePasswordRequest;
import rocks.metaldetector.web.transformer.UserDtoTransformer;

import java.util.stream.Stream;

import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static rocks.metaldetector.persistence.domain.user.UserRole.ROLE_USER;
import static rocks.metaldetector.support.Endpoints.Rest.CURRENT_USER;
import static rocks.metaldetector.support.Endpoints.Rest.CURRENT_USER_EMAIL;
import static rocks.metaldetector.support.Endpoints.Rest.CURRENT_USER_PASSWORD;

@ExtendWith(MockitoExtension.class)
class UserAccountRestControllerTest implements WithAssertions {

  @Mock
  private UserService userService;

  @Spy
  private UserDtoTransformer userDtoTransformer;

  @BeforeEach
  void setup() {
    UserAccountRestController underTest = new UserAccountRestController(userService, userDtoTransformer);
    RestAssuredMockMvc.standaloneSetup(underTest, RestExceptionsHandler.class);
  }

  @AfterEach
  void tearDown() {
    reset(userService, userDtoTransformer);
  }

  @DisplayName("Get current user tests")
  @Nested
  class GetCurrentUserTest {

    private RestAssuredMockMvcUtils restAssuredUtils;

    @BeforeEach
    void setup() {
      restAssuredUtils = new RestAssuredMockMvcUtils(CURRENT_USER);
    }

    @Test
    @DisplayName("Should use UserService to return the current user dto")
    void should_use_current_user_supplier() {
      // given
      UserDto currentUser = UserDtoFactory.createDefault();
      doReturn(currentUser).when(userService).getCurrentUser();

      // when
      restAssuredUtils.doGet();

      // then
      verify(userService).getCurrentUser();
    }

    @Test
    @DisplayName("Should use ModelMapper to transform entity")
    void should_use_model_mapper() {
      // given
      UserDto currentUser = UserDtoFactory.createDefault();
      doReturn(currentUser).when(userService).getCurrentUser();

      // when
      restAssuredUtils.doGet();

      // then
      verify(userDtoTransformer).transformUserResponse(currentUser);
    }
  }

  @Nested
  @TestInstance(PER_CLASS)
  @DisplayName("Update current user's email address tests")
  class UpdateCurrentEmailAddressTest {

    private RestAssuredMockMvcUtils restAssuredUtils;

    @BeforeEach
    void setup() {
      restAssuredUtils = new RestAssuredMockMvcUtils(CURRENT_USER_EMAIL);
      UserDto userDto = UserDtoFactory.createUser("user", ROLE_USER, true);
      doReturn(userDto).when(userService).updateCurrentEmail(any());
    }

    @Test
    @DisplayName("Should return 200 if updating user is successful")
    void should_return_200() {
      // given
      var request = new UpdateEmailRequest("mail@mail.com");

      // when
      ValidatableMockMvcResponse response = restAssuredUtils.doPatch(request);

      // then
      response.statusCode(OK.value());
    }

    @Test
    @DisplayName("Should call userService")
    void should_call_user_service() {
      // given
      var request = new UpdateEmailRequest("mail@mail.mail");

      // when
      restAssuredUtils.doPatch(request);

      // then
      verify(userService).updateCurrentEmail(request.getEmail());
    }

    @ParameterizedTest
    @MethodSource("inputProvider")
    @DisplayName("Should return 422 for faulty requests")
    void should_return_422(UpdateEmailRequest request) {
      // when
      ValidatableMockMvcResponse response = restAssuredUtils.doPatch(request);

      // then
      response.statusCode(UNPROCESSABLE_ENTITY.value());
    }

    private Stream<Arguments> inputProvider() {
      return Stream.of(
          Arguments.of(new UpdateEmailRequest("")),
          Arguments.of(new UpdateEmailRequest("mailAddress")),
          Arguments.of(new UpdateEmailRequest(null))
      );
    }
  }

  @Nested
  @TestInstance(PER_CLASS)
  @DisplayName("Delete current user")
  class DeleteCurrentUserTest {

    private RestAssuredMockMvcUtils restAssuredUtils;

    @BeforeEach
    void setup() {
      restAssuredUtils = new RestAssuredMockMvcUtils(CURRENT_USER);
    }

    @Test
    @DisplayName("Should return 200 if deleting user is successful")
    void should_return_200() {
      // when
      ValidatableMockMvcResponse response = restAssuredUtils.doDelete();

      // then
      response.statusCode(OK.value());
    }

    @Test
    @DisplayName("Should call userService")
    void should_call_user_service() {
      // when
      restAssuredUtils.doDelete();

      // then
      verify(userService).deleteCurrentUser();
    }
  }

  @Nested
  @TestInstance(PER_CLASS)
  @DisplayName("Update current user's password tests")
  class UpdateCurrentPasswordTest {

    private RestAssuredMockMvcUtils restAssuredUtils;

    @BeforeEach
    void setup() {
      restAssuredUtils = new RestAssuredMockMvcUtils(CURRENT_USER_PASSWORD);
      UserDto userDto = UserDtoFactory.createUser("user", ROLE_USER, true);
      doReturn(userDto).when(userService).updateCurrentEmail(any());
    }

    @Test
    @DisplayName("Should return 200 if updating user is successful")
    void should_return_200() {
      // given
      var request = new UpdatePasswordRequest("oldPassword", "newPassword");

      // when
      ValidatableMockMvcResponse response = restAssuredUtils.doPatch(request);

      // then
      response.statusCode(OK.value());
    }

    @Test
    @DisplayName("Should call userService")
    void should_call_user_service() {
      // given
      var request = new UpdatePasswordRequest("oldPassword", "newPassword");

      // when
      restAssuredUtils.doPatch(request);

      // then
      verify(userService).updateCurrentPassword(request.getOldPlainPassword(), request.getNewPlainPassword());
    }

    @ParameterizedTest
    @MethodSource("inputProvider")
    @DisplayName("Should return 422 for faulty requests")
    void should_return_422(UpdatePasswordRequest request) {
      // when
      ValidatableMockMvcResponse response = restAssuredUtils.doPatch(request);

      // then
      response.statusCode(UNPROCESSABLE_ENTITY.value());
    }

    private Stream<Arguments> inputProvider() {
      return Stream.of(
          Arguments.of(new UpdatePasswordRequest("         ", "newPassword")),
          Arguments.of(new UpdatePasswordRequest("oldPassword", "")),
          Arguments.of(new UpdatePasswordRequest("oldPassword", "         "))
      );
    }
  }
}