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
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import rocks.metaldetector.service.exceptions.RestExceptionsHandler;
import rocks.metaldetector.service.exceptions.UserAlreadyExistsException;
import rocks.metaldetector.service.user.UserDto;
import rocks.metaldetector.service.user.UserService;
import rocks.metaldetector.support.Endpoints;
import rocks.metaldetector.support.exceptions.ResourceNotFoundException;
import rocks.metaldetector.testutil.DtoFactory.RegisterUserRequestFactory;
import rocks.metaldetector.testutil.DtoFactory.UserDtoFactory;
import rocks.metaldetector.web.RestAssuredMockMvcUtils;
import rocks.metaldetector.web.api.request.RegisterUserRequest;
import rocks.metaldetector.web.api.request.UpdateUserRequest;
import rocks.metaldetector.web.api.response.ErrorResponse;
import rocks.metaldetector.web.api.response.UserResponse;
import rocks.metaldetector.web.transformer.UserDtoTransformer;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

@ExtendWith(MockitoExtension.class)
class UserRestControllerTest implements WithAssertions {

  private static final String USER_ID = "public-user-id";

  @Mock
  private UserService userService;

  @Spy
  private UserDtoTransformer userDtoTransformer;

  private UserRestController underTest;

  private RestAssuredMockMvcUtils restAssuredUtils;

  @BeforeEach
  void setup() {
    underTest = new UserRestController(userService, userDtoTransformer);
    restAssuredUtils = new RestAssuredMockMvcUtils(Endpoints.Rest.USERS);
    RestAssuredMockMvc.standaloneSetup(underTest, RestExceptionsHandler.class);
  }

  @AfterEach
  void tearDown() {
    reset(userService, userDtoTransformer);
  }

  @DisplayName("Get all users tests")
  @Nested
  class GetAllUsersTest {

    @Test
    @DisplayName("Should return all users")
    void should_return_all_users() {
      // given
      UserDto dto1 = UserDtoFactory.withUsernameAndEmail("user1", "user1@example.com");
      UserDto dto2 = UserDtoFactory.withUsernameAndEmail("user2", "user2@example.com");
      UserDto dto3 = UserDtoFactory.withUsernameAndEmail("user3", "user3@example.com");
      when(userService.getAllUsers()).thenReturn(List.of(dto1, dto2, dto3));

      // when
      ValidatableMockMvcResponse response = restAssuredUtils.doGet();

      // then
      response.statusCode(OK.value());

      List<UserResponse> userList = response.extract().body().jsonPath().getList(".", UserResponse.class);
      assertThat(userList).hasSize(3);
      assertThat(userList.get(0)).isEqualTo(userDtoTransformer.transformUserResponse(dto1));
      assertThat(userList.get(1)).isEqualTo(userDtoTransformer.transformUserResponse(dto2));
      assertThat(userList.get(2)).isEqualTo(userDtoTransformer.transformUserResponse(dto3));
    }

    @Test
    @DisplayName("Should return empty response if no users exist")
    void should_return_empty_response() {
      // given
      when(userService.getAllUsers()).thenReturn(Collections.emptyList());

      // when
      ValidatableMockMvcResponse response = restAssuredUtils.doGet();

      // then
      response.statusCode(OK.value());

      List<UserResponse> userList = response.extract().body().jsonPath().getList(".", UserResponse.class);
      assertThat(userList).isEmpty();
    }

    @Test
    @DisplayName("Should use UserService to return all users")
    void should_use_user_service() {
      // given
      when(userService.getAllUsers()).thenReturn(Collections.emptyList());

      // when
      restAssuredUtils.doGet();

      // then
      verify(userService, times((1))).getAllUsers();
    }
  }

  @DisplayName("Get certain user tests")
  @Nested
  class GetCertainUserTest {

    @Test
    @DisplayName("Should return a certain user")
    void should_return_a_certain_user() {
      // given
      UserDto dto = UserDtoFactory.withUsernameAndEmail("user1", "user1@example.com");
      when(userService.getUserByPublicId(anyString())).thenReturn(dto);

      // when
      ValidatableMockMvcResponse response = restAssuredUtils.doGet("/dummy-user-id");

      // then
      response.statusCode(OK.value());

      UserResponse user = response.extract().as(UserResponse.class);
      assertThat(user).isEqualTo(userDtoTransformer.transformUserResponse(dto));
    }

    @Test
    @DisplayName("Should use UserService to return a certain user")
    void should_use_user_service() {
      // given
      when(userService.getUserByPublicId(USER_ID)).thenReturn(new UserDto());

      // when
      restAssuredUtils.doGet("/" + USER_ID);

      // then
      verify(userService).getUserByPublicId(USER_ID);
    }

    @Test
    @DisplayName("Should return 404 if no user exist")
    void should_return_404() {
      // given
      when(userService.getUserByPublicId(USER_ID)).thenThrow(new ResourceNotFoundException("msg"));

      // when
      ValidatableMockMvcResponse response = restAssuredUtils.doGet("/" + USER_ID);

      // then
      response.statusCode(HttpStatus.NOT_FOUND.value());
    }
  }

  @DisplayName("Create administrator tests")
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  @Nested
  class CreateAdministratorTest {

    @Test
    @DisplayName("Should pass expected UserDto to UserService")
    void should_use_user_service() {
      // given
      RegisterUserRequest request = RegisterUserRequestFactory.createDefault();
      UserDto expectedPassedUserDto = userDtoTransformer.transformUserDto(request);
      when(userService.createAdministrator(any())).thenReturn(UserDtoFactory.createDefault());
      ArgumentCaptor<UserDto> userDtoCaptor = ArgumentCaptor.forClass(UserDto.class);

      // when
      restAssuredUtils.doPost(request);

      // then
      verify(userService).createAdministrator(userDtoCaptor.capture());
      assertThat(userDtoCaptor.getValue()).isEqualTo(expectedPassedUserDto);
    }

    @Test
    @DisplayName("Should return UserResponse and status 201 if creating of administrator was successful")
    void should_return_201() {
      // given
      RegisterUserRequest request = RegisterUserRequestFactory.createDefault();
      UserDto createdUserDto = UserDtoFactory.createDefault();
      when(userService.createAdministrator(any())).thenReturn(createdUserDto);

      // when
      ValidatableMockMvcResponse response = restAssuredUtils.doPost(request);

      // then
      response.statusCode(HttpStatus.CREATED.value());

      UserResponse user = response.extract().as(UserResponse.class);
      assertThat(user).isEqualTo(userDtoTransformer.transformUserResponse(createdUserDto));
    }

    @Test
    @DisplayName("Should return status 409 and ErrorResponse if administrator with username or email already exists")
    void should_return_409() {
      // given
      RegisterUserRequest request = RegisterUserRequestFactory.createDefault();
      when(userService.createAdministrator(any())).thenThrow(UserAlreadyExistsException.class);

      // when
      ValidatableMockMvcResponse response = restAssuredUtils.doPost(request);

      // then
      response.statusCode(HttpStatus.CONFLICT.value())
          .extract().as(ErrorResponse.class);
    }

    @ParameterizedTest
    @MethodSource("createAdministratorRequestProvider")
    @DisplayName("Should return status 400 if creating of administrator does not pass validation")
    void should_return_400(RegisterUserRequest request, int expectedErrorCount) {
      // when
      ValidatableMockMvcResponse response = restAssuredUtils.doPost(request);

      // then
      response.statusCode(BAD_REQUEST.value());

      ErrorResponse errorResponse = response.extract().as(ErrorResponse.class);
      System.out.println(errorResponse);
      assertThat(errorResponse.getMessages()).hasSize(expectedErrorCount);
    }

    private Stream<Arguments> createAdministratorRequestProvider() {
      return Stream.of(
          // invalid username
          Arguments.of(RegisterUserRequestFactory.withUsername(""), 1),
          Arguments.of(RegisterUserRequestFactory.withUsername("    "), 1),
          Arguments.of(RegisterUserRequestFactory.withUsername(null), 1),

          // invalid email
          Arguments.of(RegisterUserRequestFactory.withEmail("john.doe.example.de"), 1),
          Arguments.of(RegisterUserRequestFactory.withEmail(""), 1),
          Arguments.of(RegisterUserRequestFactory.withEmail("    "), 2),
          Arguments.of(RegisterUserRequestFactory.withEmail("@com"), 1),
          Arguments.of(RegisterUserRequestFactory.withEmail(null), 1),

          // invalid passwords
          Arguments.of(RegisterUserRequestFactory.withPassword("secret-password", "other-secret-password"), 1),
          Arguments.of(RegisterUserRequestFactory.withPassword("secret", "secret"), 2),
          Arguments.of(RegisterUserRequestFactory.withPassword("", ""), 4),
          Arguments.of(RegisterUserRequestFactory.withPassword(null, null), 2),

          // all null
          Arguments.of(RegisterUserRequest.builder().build(), 4)
      );
    }
  }

  @Nested
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  @DisplayName("Update user tests")
  class UpdateUserTest {

    @Test
    @DisplayName("Should return 200 if updating user is successful")
    void should_return_200() {
      // given
      final String NEW_ROLE = "Administrator";
      UpdateUserRequest updateUserRequest = new UpdateUserRequest(USER_ID, NEW_ROLE, false);
      UserDto userDto = UserDtoFactory.createDefault();
      userDto.setRole(NEW_ROLE);
      userDto.setEnabled(false);
      when(userService.updateUser(USER_ID, userDtoTransformer.transformUserDto(updateUserRequest))).thenReturn(userDto);

      // when
      ValidatableMockMvcResponse response = restAssuredUtils.doPut(updateUserRequest);

      // then
      response.statusCode(OK.value());

      UserResponse user = response.extract().as(UserResponse.class);
      assertThat(user.getRole()).isEqualTo(NEW_ROLE);
      assertThat(user.isEnabled()).isFalse();
      verify(userService).updateUser(eq(USER_ID), any());
    }

    @ParameterizedTest
    @MethodSource("inputProvider")
    @DisplayName("Should return 400 for faulty requests")
    void should_return_400(String userId, String role, boolean enabled) {
      // given
      UpdateUserRequest updateUserRequest = new UpdateUserRequest(userId, role, enabled);

      // when
      ValidatableMockMvcResponse response = restAssuredUtils.doPut(updateUserRequest);

      // then
      response.statusCode(BAD_REQUEST.value());
    }

    private Stream<Arguments> inputProvider() {
      return Stream.of(
          Arguments.of("", "", false),
          Arguments.of("id", "", false),
          Arguments.of("", "role", false)
      );
    }
  }
}
