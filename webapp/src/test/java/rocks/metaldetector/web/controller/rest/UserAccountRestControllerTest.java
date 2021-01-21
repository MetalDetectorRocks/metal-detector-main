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
import org.modelmapper.ModelMapper;
import rocks.metaldetector.persistence.domain.user.UserRole;
import rocks.metaldetector.service.exceptions.RestExceptionsHandler;
import rocks.metaldetector.service.user.UserDto;
import rocks.metaldetector.service.user.UserService;
import rocks.metaldetector.support.Endpoints;
import rocks.metaldetector.testutil.DtoFactory.UserDtoFactory;
import rocks.metaldetector.web.RestAssuredMockMvcUtils;
import rocks.metaldetector.web.api.request.UpdateEmailRequest;
import rocks.metaldetector.web.api.response.UserResponse;

import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

@ExtendWith(MockitoExtension.class)
class UserAccountRestControllerTest implements WithAssertions {

  @Mock
  private UserService userService;

  @Spy
  private ModelMapper modelMapper;

  private UserAccountRestController underTest;

  @BeforeEach
  void setup() {
    underTest = new UserAccountRestController(userService, modelMapper);
    RestAssuredMockMvc.standaloneSetup(underTest, RestExceptionsHandler.class);
  }

  @AfterEach
  void tearDown() {
    reset(userService, modelMapper);
  }

  @DisplayName("Get current user tests")
  @Nested
  class GetCurrentUserTest {

    private RestAssuredMockMvcUtils restAssuredUtils;

    @BeforeEach
    void setup() {
      restAssuredUtils = new RestAssuredMockMvcUtils(Endpoints.Rest.CURRENT_USER);
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
      verify(modelMapper).map(currentUser, UserResponse.class);
    }
  }

  @Nested
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  @DisplayName("Update current user's email address tests")
  class UpdateCurrentEmailAddressTest {

    private RestAssuredMockMvcUtils restAssuredUtils;
    private UserDto userDto;

    @BeforeEach
    void setup() {
      restAssuredUtils = new RestAssuredMockMvcUtils(Endpoints.Rest.CURRENT_USER_EMAIL);
      userDto = UserDtoFactory.createUser("user", UserRole.ROLE_USER, true);
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
    @DisplayName("Should return updated email address if updating user is successful")
    void should_return_updated_email() {
      // given
      var request = new UpdateEmailRequest("mail@mail.com");
      userDto.setEmail(request.getEmailAddress());
      doReturn(userDto).when(userService).updateCurrentEmail(any());

      // when
      ValidatableMockMvcResponse response = restAssuredUtils.doPatch(request);

      // then
      String emailAddress = response.extract().asString();
      assertThat(emailAddress).isEqualTo(request.getEmailAddress());
    }

    @Test
    @DisplayName("Should call userService")
    void should_call_user_service() {
      // given
      var request = new UpdateEmailRequest("mail@mail.mail");

      // when
      restAssuredUtils.doPatch(request);

      // then
      verify(userService).updateCurrentEmail(request.getEmailAddress());
    }

    @ParameterizedTest
    @MethodSource("inputProvider")
    @DisplayName("Should return 400 for faulty requests")
    void should_return_400(UpdateEmailRequest request) {
      // when
      ValidatableMockMvcResponse response = restAssuredUtils.doPatch(request);

      // then
      response.statusCode(BAD_REQUEST.value());
    }

    private Stream<Arguments> inputProvider() {
      return Stream.of(
          Arguments.of(new UpdateEmailRequest("")),
          Arguments.of(new UpdateEmailRequest("mailAddress")),
          Arguments.of(new UpdateEmailRequest(null))
      );
    }
  }
}