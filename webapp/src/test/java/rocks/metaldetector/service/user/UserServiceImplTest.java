package rocks.metaldetector.service.user;

import io.jsonwebtoken.Claims;
import org.assertj.core.api.WithAssertions;
import org.assertj.core.data.TemporalUnitLessThanOffset;
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
import org.mockito.MockedStatic;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import rocks.metaldetector.persistence.domain.user.UserEntity;
import rocks.metaldetector.persistence.domain.user.UserRepository;
import rocks.metaldetector.persistence.domain.user.UserRole;
import rocks.metaldetector.security.AuthenticationFacade;
import rocks.metaldetector.security.LoginAttemptService;
import rocks.metaldetector.service.exceptions.IllegalUserActionException;
import rocks.metaldetector.service.exceptions.TokenExpiredException;
import rocks.metaldetector.service.exceptions.UserAlreadyExistsException;
import rocks.metaldetector.service.token.TokenService;
import rocks.metaldetector.service.user.events.UserCreationEvent;
import rocks.metaldetector.service.user.events.UserDeletionEvent;
import rocks.metaldetector.support.JwtsSupport;
import rocks.metaldetector.support.exceptions.ResourceNotFoundException;
import rocks.metaldetector.testutil.DtoFactory.UserDtoFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static rocks.metaldetector.persistence.domain.user.UserRole.ROLE_ADMINISTRATOR;
import static rocks.metaldetector.persistence.domain.user.UserRole.ROLE_USER;
import static rocks.metaldetector.service.exceptions.UserAlreadyExistsException.Reason.EMAIL_ALREADY_EXISTS;
import static rocks.metaldetector.service.exceptions.UserAlreadyExistsException.Reason.USERNAME_ALREADY_EXISTS;
import static rocks.metaldetector.service.user.UserErrorMessages.OAUTH_USER_CANNOT_CHANGE_EMAIL;
import static rocks.metaldetector.service.user.UserErrorMessages.OAUTH_USER_CANNOT_CHANGE_PASSWORD;
import static rocks.metaldetector.service.user.UserErrorMessages.TOKEN_EXPIRED;
import static rocks.metaldetector.service.user.UserErrorMessages.USER_NOT_FOUND;
import static rocks.metaldetector.service.user.UserErrorMessages.USER_WITH_ID_NOT_FOUND;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest implements WithAssertions {

  private static final String USERNAME = "JohnD";
  private static final String EMAIL = "john.doe@example.com";
  private static final String DUPLICATE_USERNAME = "Duplicate";
  private static final String DUPLICATE_EMAIL = "duplicate@example.com";
  private static final String PUBLIC_ID = "public-id";
  private final String TOKEN = "user-token";
  private final String NEW_PLAIN_PASSWORD = "new-plain-password";
  private final String NEW_ENCRYPTED_PASSWORD = "encryption".repeat(6); // an encrypted password must be 60 characters long

  @Mock
  private UserRepository userRepository;

  @Spy
  private BCryptPasswordEncoder passwordEncoder;

  @Mock
  private TokenService tokenService;

  @Mock
  private JwtsSupport jwtsSupport;

  @Mock
  private AuthenticationFacade authenticationFacade;

  @Mock
  private LoginAttemptService loginAttemptService;

  @Mock
  private HttpServletRequest request;

  @Spy
  private UserTransformer userTransformer;

  @Mock
  private ApplicationEventPublisher applicationEventPublisher;

  private UserServiceImpl underTest;

  @BeforeEach
  void setup() {
    underTest = new UserServiceImpl(userRepository, passwordEncoder, jwtsSupport, userTransformer, authenticationFacade,
                                    loginAttemptService, applicationEventPublisher, request);
  }

  @AfterEach
  void tearDown() {
    reset(userRepository, passwordEncoder, jwtsSupport, tokenService, authenticationFacade, userTransformer, loginAttemptService, request, applicationEventPublisher);
  }

  @DisplayName("Create user entity tests")
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  @Nested
  class CreateUserEntityTest {

    @Test
    @DisplayName("Should return UserDto")
    void should_return_user_dto() {
      // given
      UserDto givenUserDto = UserDtoFactory.withUsernameAndEmail(USERNAME, EMAIL);
      UserEntity expectedUserEntity = UserEntityFactory.createUser(USERNAME, EMAIL);
      when(userRepository.save(any(UserEntity.class))).thenReturn(expectedUserEntity);

      // when
      UserDto createdUserDto = underTest.createUser(givenUserDto);

      // then
      assertThat(createdUserDto).isEqualTo(userTransformer.transform(expectedUserEntity));
    }

    @DisplayName("Should pass disabled UserEntity with Role USER to UserRepository")
    @Test
    void should_use_user_repository() {
      // given
      ArgumentCaptor<UserEntity> userEntityCaptor = ArgumentCaptor.forClass(UserEntity.class);
      UserDto givenUserDto = UserDtoFactory.withUsernameAndEmail(USERNAME, EMAIL);
      when(userRepository.save(any(UserEntity.class))).thenReturn(UserEntityFactory.createUser(USERNAME, EMAIL));

      // when
      underTest.createUser(givenUserDto);

      // then
      verify(userRepository).save(userEntityCaptor.capture());
      UserEntity passedUserEntity = userEntityCaptor.getValue();
      assertThat(passedUserEntity.getUsername()).isEqualTo(USERNAME);
      assertThat(passedUserEntity.getEmail()).isEqualTo(EMAIL);
      assertThat(passedUserEntity.getPassword()).isNotEmpty();
      assertThat(passedUserEntity.getUserRoles()).containsExactly(ROLE_USER);
      assertThat(passedUserEntity.isEnabled()).isFalse();
    }

    @Test
    @DisplayName("Creating a new user should check if email or username is already used")
    void should_check_email_and_username() {
      // given
      UserDto userDto = UserDtoFactory.withUsernameAndEmail(USERNAME, EMAIL);
      when(userRepository.save(any(UserEntity.class))).thenReturn(UserEntityFactory.createUser(USERNAME, EMAIL));

      // when
      underTest.createUser(userDto);

      // then
      verify(userRepository, times(2)).existsByEmail(anyString());
      verify(userRepository, times(2)).existsByUsername(anyString());
    }

    @Test
    @DisplayName("Creation event is published")
    void creation_event_published() {
      // given
      ArgumentCaptor<UserCreationEvent> argumentCaptor = ArgumentCaptor.forClass(UserCreationEvent.class);
      UserDto userDto = UserDtoFactory.withUsernameAndEmail(USERNAME, EMAIL);
      UserEntity user = UserEntityFactory.createUser(USERNAME, EMAIL);
      doReturn(user).when(userRepository).save(any(UserEntity.class));

      // when
      underTest.createUser(userDto);

      // then
      verify(applicationEventPublisher).publishEvent(argumentCaptor.capture());
      UserCreationEvent userCreationEvent = argumentCaptor.getValue();

      assertThat(userCreationEvent.getSource()).isEqualTo(underTest);
      assertThat(userCreationEvent.getUserEntity()).isEqualTo(user);
    }

    @ParameterizedTest(name = "[{index}] => Username <{0}> | Email <{1}>")
    @MethodSource("userDtoProvider")
    @DisplayName("Creating a new user with a username and email that already exists should throw exception")
    void create_user_with_username_or_email_that_already_exists_should_throw_exception(String username, String email, UserAlreadyExistsException.Reason reason) {
      // given
      UserDto userDto = UserDtoFactory.withUsernameAndEmail(username, email);

      when(userRepository.existsByUsername(anyString())).thenAnswer(invocationOnMock -> {
        String usernameArg = invocationOnMock.getArgument(0);
        return usernameArg.equalsIgnoreCase(DUPLICATE_USERNAME) || usernameArg.equalsIgnoreCase(DUPLICATE_EMAIL);
      });

      when(userRepository.existsByEmail(anyString())).thenAnswer(invocationOnMock -> {
        String emailArg = invocationOnMock.getArgument(0);
        return emailArg.equalsIgnoreCase(DUPLICATE_EMAIL) || emailArg.equalsIgnoreCase(DUPLICATE_USERNAME);
      });

      // when
      Throwable throwable = catchThrowable(() -> underTest.createUser(userDto));

      // then
      assertThat(throwable).isInstanceOf(UserAlreadyExistsException.class);
      assertThat(((UserAlreadyExistsException) throwable).getReason()).isEqualTo(reason);
      verify(userRepository, atMost(2)).existsByEmail(anyString());
      verify(userRepository, atMost(2)).existsByUsername(anyString());
    }

    private Stream<Arguments> userDtoProvider() {
      return Stream.of(
          Arguments.of(DUPLICATE_USERNAME, EMAIL, USERNAME_ALREADY_EXISTS),
          Arguments.of(DUPLICATE_EMAIL, EMAIL, USERNAME_ALREADY_EXISTS),
          Arguments.of(USERNAME, DUPLICATE_EMAIL, EMAIL_ALREADY_EXISTS),
          Arguments.of(USERNAME, DUPLICATE_USERNAME, EMAIL_ALREADY_EXISTS)
      );
    }
  }

  @DisplayName("Create administrator tests")
  @Nested
  class CreateAdministratorTest {

    @Test
    @DisplayName("Should return UserDto")
    void should_return_user_dto() {
      // given
      UserDto givenUserDto = UserDtoFactory.withUsernameAndEmail(USERNAME, EMAIL);
      UserEntity expectedUserEntity = UserEntityFactory.createUser(USERNAME, EMAIL);
      when(userRepository.save(any(UserEntity.class))).thenReturn(expectedUserEntity);

      // when
      UserDto createdUserDto = underTest.createAdministrator(givenUserDto);

      // then
      assertThat(createdUserDto).isEqualTo(userTransformer.transform(expectedUserEntity));
    }

    @Test
    @DisplayName("Should check if email or username is already used")
    void should_check_email_and_username() {
      // given
      UserDto userDto = UserDtoFactory.withUsernameAndEmail(USERNAME, EMAIL);
      when(userRepository.save(any(UserEntity.class))).thenReturn(UserEntityFactory.createUser(USERNAME, EMAIL));

      // when
      underTest.createAdministrator(userDto);

      // then
      verify(userRepository, times(2)).existsByEmail(anyString());
      verify(userRepository, times(2)).existsByUsername(anyString());
    }

    @DisplayName("Should pass enabled UserEntity with Role ADMINISTRATOR to UserRepository")
    @Test
    void should_use_user_repository() {
      // given
      ArgumentCaptor<UserEntity> userEntityCaptor = ArgumentCaptor.forClass(UserEntity.class);
      UserDto givenUserDto = UserDtoFactory.withUsernameAndEmail(USERNAME, EMAIL);
      when(userRepository.save(any(UserEntity.class))).thenReturn(UserEntityFactory.createUser(USERNAME, EMAIL));

      // when
      underTest.createAdministrator(givenUserDto);

      // then
      verify(userRepository).save(userEntityCaptor.capture());
      UserEntity passedUserEntity = userEntityCaptor.getValue();
      assertThat(passedUserEntity.getUsername()).isEqualTo(USERNAME);
      assertThat(passedUserEntity.getEmail()).isEqualTo(EMAIL);
      assertThat(passedUserEntity.getPassword()).isNotEmpty();
      assertThat(passedUserEntity.getUserRoles()).containsExactly(ROLE_ADMINISTRATOR);
      assertThat(passedUserEntity.isEnabled()).isTrue();
    }

    @Test
    @DisplayName("Creation event is published")
    void creation_event_published() {
      // given
      ArgumentCaptor<UserCreationEvent> argumentCaptor = ArgumentCaptor.forClass(UserCreationEvent.class);
      UserDto userDto = UserDtoFactory.withUsernameAndEmail(USERNAME, EMAIL);
      UserEntity user = UserEntityFactory.createUser(USERNAME, EMAIL);
      doReturn(user).when(userRepository).save(any(UserEntity.class));

      // when
      underTest.createAdministrator(userDto);

      // then
      verify(applicationEventPublisher).publishEvent(argumentCaptor.capture());
      UserCreationEvent userCreationEvent = argumentCaptor.getValue();

      assertThat(userCreationEvent.getSource()).isEqualTo(underTest);
      assertThat(userCreationEvent.getUserEntity()).isEqualTo(user);
    }
  }

  @DisplayName("Get user tests")
  @Nested
  class GetUserTest {

    @Test
    @DisplayName("Requesting an existing user by his public id should work")
    void get_user_by_public_id_for_existing_user() {
      // given
      UserEntity user = UserEntityFactory.createUser(USERNAME, EMAIL);
      when(userRepository.findByPublicId(anyString())).thenReturn(Optional.of(user));
      when(userTransformer.transform(user)).thenReturn(UserDtoFactory.withUsernameAndEmail(USERNAME, EMAIL));

      // when
      UserDto userDto = underTest.getUserByPublicId(PUBLIC_ID);

      // then
      verify(userRepository).findByPublicId(PUBLIC_ID);
      assertThat(userDto.getUsername()).isEqualTo(user.getUsername());
      assertThat(userDto.getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    @DisplayName("Requesting a not existing user by a public id should throw exception")
    void get_user_by_public_id_for_not_existing_user() {
      // given
      when(userRepository.findByPublicId(PUBLIC_ID)).thenReturn(Optional.empty());

      // when
      Throwable throwable = catchThrowable(() -> underTest.getUserByPublicId(PUBLIC_ID));

      // then
      verify(userRepository).findByPublicId(PUBLIC_ID);
      assertThat(throwable).isInstanceOf(ResourceNotFoundException.class);
      assertThat(throwable).hasMessageContaining(USER_WITH_ID_NOT_FOUND.toDisplayString());
    }

    @Test
    @DisplayName("Requesting an existing user by his email should work")
    void get_user_by_email_or_username_with_email() {
      // given
      UserEntity user = UserEntityFactory.createUser(USERNAME, EMAIL);
      when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
      when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
      when(userTransformer.transform(user)).thenReturn(UserDtoFactory.withUsernameAndEmail(USERNAME, EMAIL));
      when(request.getHeader(anyString())).thenReturn("666");

      // when
      Optional<UserDto> userDto = underTest.getUserByEmailOrUsername(EMAIL);

      // then
      verify(userRepository).findByEmail(EMAIL);
      verify(userRepository, never()).findByUsername(EMAIL);
      assertThat(userDto).isPresent();
      assertThat(userDto.get().getUsername()).isEqualTo(USERNAME);
      assertThat(userDto.get().getEmail()).isEqualTo(EMAIL);
    }

    @Test
    @DisplayName("Requesting an existing user by his username should work")
    void get_user_by_email_or_username_with_username() {
      // given
      UserEntity user = UserEntityFactory.createUser(USERNAME, EMAIL);
      when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
      when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
      when(userTransformer.transform(user)).thenReturn(UserDtoFactory.withUsernameAndEmail(USERNAME, EMAIL));
      when(request.getHeader(anyString())).thenReturn("666");

      // when
      Optional<UserDto> userDto = underTest.getUserByEmailOrUsername(USERNAME);

      // then
      verify(userRepository).findByEmail(USERNAME);
      verify(userRepository).findByUsername(USERNAME);
      assertThat(userDto).isPresent();
      assertThat(userDto.get().getUsername()).isEqualTo(USERNAME);
      assertThat(userDto.get().getEmail()).isEqualTo(EMAIL);
    }

    @Test
    @DisplayName("Requesting a not existing user by email or username should return empty optional")
    void get_user_by_email_or_username_for_not_existing_user() {
      // given
      String NOT_EXISTING = "not-existing";
      when(request.getHeader(anyString())).thenReturn("666");
      when(userRepository.findByEmail(NOT_EXISTING)).thenReturn(Optional.empty());
      when(userRepository.findByUsername(NOT_EXISTING)).thenReturn(Optional.empty());

      // when
      Optional<UserDto> userDto = underTest.getUserByEmailOrUsername(NOT_EXISTING);

      // then
      verify(userRepository).findByEmail(NOT_EXISTING);
      verify(userRepository).findByUsername(NOT_EXISTING);
      assertThat(userDto).isEmpty();
    }

    @Test
    @DisplayName("Requesting a user by email or username via blocked ip should throw an exception")
    void get_user_by_email_or_username_from_blocked_ip() {
      // given
      when(request.getHeader(anyString())).thenReturn("666");
      when(loginAttemptService.isBlocked(anyString())).thenReturn(true);

      // when
      Throwable throwable = catchThrowable(() -> underTest.getUserByEmailOrUsername(USERNAME));

      // then
      assertThat(throwable).isInstanceOf(LockedException.class);
      verifyNoInteractions(userRepository);
    }

    @Test
    @DisplayName("Should return all users")
    void get_all_users() {
      // given
      UserEntity user1 = UserEntityFactory.createUser("a", "a@example.com");
      UserEntity user2 = UserEntityFactory.createUser("b", "b@example.com");
      when(userRepository.findAll()).thenReturn(List.of(user1, user2));

      UserDto userDto1 = UserDtoFactory.withUsernameAndEmail("a", "a@example.com");
      UserDto userDto2 = UserDtoFactory.withUsernameAndEmail("b", "b@example.com");
      when(userTransformer.transform(user1)).thenReturn(userDto1);
      when(userTransformer.transform(user2)).thenReturn(userDto2);

      // when
      List<UserDto> userDtoList = underTest.getAllUsers();

      // then
      assertThat(userDtoList).hasSize(2);
      assertThat(userDtoList.get(0)).isEqualTo(userDto1);
      assertThat(userDtoList.get(1)).isEqualTo(userDto2);
    }

    @Test
    @DisplayName("Should use UserRepository to return a list of all users")
    void get_all_users_uses_user_repository() {
      // given
      when(userRepository.findAll()).thenReturn(Collections.emptyList());

      // when
      underTest.getAllUsers();

      // then
      verify(userRepository).findAll();
    }

    @Test
    @DisplayName("Should return all active users")
    void get_all_active_users() {
      // given
      UserEntity user1 = UserEntityFactory.createUser("a", "a@example.com");
      UserEntity user2 = UserEntityFactory.createUser("b", "b@example.com");
      user2.setEnabled(false);
      when(userRepository.findAll()).thenReturn(List.of(user1, user2));

      UserDto userDto1 = UserDtoFactory.withUsernameAndEmail("a", "a@example.com");
      UserDto userDto2 = UserDtoFactory.withUsernameAndEmail("b", "b@example.com");
      userDto2.setEnabled(false);
      when(userTransformer.transform(user1)).thenReturn(userDto1);
      when(userTransformer.transform(user2)).thenReturn(userDto2);

      // when
      List<UserDto> userDtoList = underTest.getAllActiveUsers();

      // then
      assertThat(userDtoList).hasSize(1);
      assertThat(userDtoList.get(0)).isEqualTo(userTransformer.transform(user1));
    }

    @Test
    @DisplayName("Should use UserRepository to return a list of all active users")
    void get_all_active_users_uses_user_repository() {
      // given
      when(userRepository.findAll()).thenReturn(Collections.emptyList());

      // when
      underTest.getAllActiveUsers();

      // then
      verify(userRepository).findAll();
    }

    @Test
    @DisplayName("Should return all users in a certain order")
    void get_all_users_ordered() {
      // given
      UserEntity user1 = UserEntityFactory.createUser("a1", ROLE_USER, false);
      UserEntity user2 = UserEntityFactory.createUser("b1", ROLE_USER, true);
      UserEntity user3 = UserEntityFactory.createUser("a2", ROLE_USER, true);
      UserEntity user4 = UserEntityFactory.createUser("c1", ROLE_ADMINISTRATOR, false);
      UserEntity user5 = UserEntityFactory.createUser("c2", ROLE_ADMINISTRATOR, true);
      UserEntity user6 = UserEntityFactory.createUser("a3", ROLE_ADMINISTRATOR, true);
      when(userRepository.findAll()).thenReturn(List.of(user1, user2, user3, user4, user5, user6));

      // when
      List<UserDto> userDtoList = underTest.getAllUsers();

      // then
      assertThat(userDtoList).hasSize(6);

      assertDtoIsCorrect(userDtoList.get(0), "a3", ROLE_ADMINISTRATOR, true);
      assertDtoIsCorrect(userDtoList.get(1), "c2", ROLE_ADMINISTRATOR, true);
      assertDtoIsCorrect(userDtoList.get(2), "a2", ROLE_USER, true);
      assertDtoIsCorrect(userDtoList.get(3), "b1", ROLE_USER, true);
      assertDtoIsCorrect(userDtoList.get(4), "c1", ROLE_ADMINISTRATOR, false);
      assertDtoIsCorrect(userDtoList.get(5), "a1", ROLE_USER, false);
    }

    @Test
    @DisplayName("currentUserSupplier is called")
    void test_get_current_user_calls_supplier() {
      // given
      doReturn(UserEntityFactory.createUser("user", "mail@mail.de")).when(authenticationFacade).getCurrentUser();

      // when
      underTest.getCurrentUser();

      // then
      verify(authenticationFacade).getCurrentUser();
    }

    @Test
    @DisplayName("current user supplier is transformed")
    void test_get_current_user_is_transformed() {
      // given
      var user = UserEntityFactory.createUser("user", "mail@mail.de");
      doReturn(user).when(authenticationFacade).getCurrentUser();

      // when
      underTest.getCurrentUser();

      // then
      verify(userTransformer).transform(user);
    }

    @Test
    @DisplayName("current user dto is returned")
    void test_get_current_user_dto_returned() {
      // given
      var user = UserDtoFactory.createDefault();
      doReturn(user).when(userTransformer).transform(any());

      // when
      var result = underTest.getCurrentUser();

      // then
      assertThat(result).isEqualTo(user);
    }

    private void assertDtoIsCorrect(UserDto userDto, String userName, UserRole role, boolean enabled) {
      assertThat(userDto.getUsername()).isEqualTo(userName);
      assertThat(userDto.getRole()).isEqualTo(role.getDisplayName());
      assertThat(userDto.isEnabled()).isEqualTo(enabled);
    }
  }

  @DisplayName("Update user tests")
  @Nested
  class UpdateUserTest {

    @Test
    @DisplayName("Updating an existing user should update the user's role")
    void update_user_role_for_existing_user() {
      // given
      ArgumentCaptor<UserEntity> userEntityCaptor = ArgumentCaptor.forClass(UserEntity.class);
      UserDto userDtoForUpdate = UserDtoFactory.withUsernameAndEmail(USERNAME, EMAIL);
      userDtoForUpdate.setRole("Administrator");
      UserEntity user = UserEntityFactory.createUser(USERNAME, EMAIL);

      when(authenticationFacade.getCurrentUser()).thenReturn(user);
      when(userRepository.findByPublicId(PUBLIC_ID)).thenReturn(Optional.of(user));
      // return the same user without changing the mail is ok here, we don't want to concentrate on the DTO conversion in this test
      when(userRepository.save(any())).thenReturn(user);
      when(userTransformer.transform(user)).thenReturn(UserDtoFactory.createDefault());

      // when
      UserDto userDto = underTest.updateUser(PUBLIC_ID, userDtoForUpdate);

      // then
      verify(userRepository).findByPublicId(PUBLIC_ID);
      verify(userRepository).save(userEntityCaptor.capture());
      verify(authenticationFacade).getCurrentUser();
      assertThat(userDto).isNotNull();
      assertThat(userEntityCaptor.getValue().getUsername()).isEqualTo(userDtoForUpdate.getUsername());
      assertThat(userEntityCaptor.getValue().getUserRoles()).containsExactly(ROLE_ADMINISTRATOR);
    }

    @Test
    @DisplayName("Updating an existing user should update the user's status")
    void update_user_status_for_existing_user() {
      // given
      ArgumentCaptor<UserEntity> userEntityCaptor = ArgumentCaptor.forClass(UserEntity.class);
      UserDto userDtoForUpdate = UserDtoFactory.withUsernameAndEmail(USERNAME, EMAIL);
      userDtoForUpdate.setEnabled(false);
      UserEntity user = UserEntityFactory.createUser(USERNAME, EMAIL);

      when(authenticationFacade.getCurrentUser()).thenReturn(user);
      when(userRepository.findByPublicId(PUBLIC_ID)).thenReturn(Optional.of(user));
      // return the same user without changing the mail is ok here, we don't want to concentrate on the DTO conversion in this test
      when(userRepository.save(any())).thenReturn(user);
      when(userTransformer.transform(user)).thenReturn(UserDtoFactory.createDefault());

      // when
      UserDto userDto = underTest.updateUser(PUBLIC_ID, userDtoForUpdate);

      // then
      verify(userRepository).findByPublicId(PUBLIC_ID);
      verify(userRepository).save(userEntityCaptor.capture());
      verify(authenticationFacade).getCurrentUser();
      assertThat(userDto).isNotNull();
      assertThat(userEntityCaptor.getValue().getUsername()).isEqualTo(userDtoForUpdate.getUsername());
      assertThat(userEntityCaptor.getValue().isEnabled()).isFalse();
    }

    @Test
    @DisplayName("Updating a not existing user should throw exception")
    void update_user_for_not_existing_user() {
      // given
      String NEW_EMAIL = "updatedEmail@example.com";
      UserDto userDtoForUpdate = UserDtoFactory.withUsernameAndEmail(USERNAME, NEW_EMAIL);
      when(userRepository.findByPublicId(PUBLIC_ID)).thenReturn(Optional.empty());

      // when
      Throwable throwable = catchThrowable(() -> underTest.updateUser(PUBLIC_ID, userDtoForUpdate));

      // then
      verify(userRepository).findByPublicId(PUBLIC_ID);
      assertThat(throwable).isInstanceOf(ResourceNotFoundException.class);
      assertThat(throwable).hasMessageContaining(USER_WITH_ID_NOT_FOUND.toDisplayString());
    }

    @Test
    @DisplayName("Updating should fail if an admin tries to remove his own role")
    void update_should_fail_when_admin_removes_his_role() {
      // given
      UserDto userDtoForUpdate = UserDtoFactory.withUsernameAndEmail(USERNAME, EMAIL);
      userDtoForUpdate.setRole("User");
      UserEntity user = UserEntityFactory.createUser(USERNAME, EMAIL);
      user.setPublicId(PUBLIC_ID);
      user.setUserRoles(UserRole.createAdministratorRole());

      when(authenticationFacade.getCurrentUser()).thenReturn(user);
      when(userRepository.findByPublicId(PUBLIC_ID)).thenReturn(Optional.of(user));

      // when
      Throwable throwable = catchThrowable(() -> underTest.updateUser(PUBLIC_ID, userDtoForUpdate));

      // then
      assertThat(throwable).isInstanceOf(IllegalUserActionException.class);
      assertThat(throwable).hasMessage(UserErrorMessages.ADMINISTRATOR_DISCARD_ROLE.toDisplayString());
    }

    @Test
    @DisplayName("Updating should fail if an admin tries to disable himself")
    void update_should_fail_when_admin_disabled() {
      // given
      UserDto userDtoForUpdate = UserDtoFactory.withUsernameAndEmail(USERNAME, EMAIL);
      userDtoForUpdate.setEnabled(false);
      UserEntity user = UserEntityFactory.createUser(USERNAME, EMAIL);
      user.setPublicId(PUBLIC_ID);
      user.setUserRoles(UserRole.createAdministratorRole());

      when(authenticationFacade.getCurrentUser()).thenReturn(user);
      when(userRepository.findByPublicId(PUBLIC_ID)).thenReturn(Optional.of(user));

      // when
      Throwable throwable = catchThrowable(() -> underTest.updateUser(PUBLIC_ID, userDtoForUpdate));

      // then
      assertThat(throwable).isInstanceOf(IllegalUserActionException.class);
      assertThat(throwable).hasMessage(UserErrorMessages.ADMINISTRATOR_CANNOT_DISABLE_HIMSELF.toDisplayString());
    }

    @Test
    @DisplayName("Changing the password of a user should call JwtsSupport")
    void change_password_calls_jwts_support() {
      // given
      var token = "token";
      var claims = mock(Claims.class);
      var date = new Date(System.currentTimeMillis() + Duration.ofMinutes(1).toMillis());
      var user = UserEntityFactory.createUser("JohnD", "johnd@example.com");
      doReturn(date).when(claims).getExpiration();
      doReturn(claims).when(jwtsSupport).getClaims(any());
      doReturn(Optional.of(user)).when(userRepository).findByPublicId(any());

      // when
      underTest.resetPasswordWithToken(token, "newPW");

      // then
      verify(jwtsSupport).getClaims(token);
    }

    @Test
    @DisplayName("Changing the password of a user with valid token should call UserRepository to find user")
    void change_password_should_call_user_repository_to_find() {
      // given
      var claims = mock(Claims.class);
      var publicUserId = "publicUserId";
      var userEntity = UserEntityFactory.createUser(USERNAME, EMAIL);
      doReturn(claims).when(jwtsSupport).getClaims(any());
      doReturn(new Date(System.currentTimeMillis() + Duration.ofMinutes(1).toMillis())).when(claims).getExpiration();
      doReturn(publicUserId).when(claims).getSubject();
      doReturn(Optional.of(userEntity)).when(userRepository).findByPublicId(any());

      // when
      underTest.resetPasswordWithToken("token", "newPW");

      // then
      verify(userRepository).findByPublicId(publicUserId);
    }

    @Test
    @DisplayName("Changing the password of a user with valid token should call PasswordEncoder")
    void change_password_should_call_password_encoder() {
      // given
      var claims = mock(Claims.class);
      var newPassword = "newPw";
      var userEntity = UserEntityFactory.createUser(USERNAME, EMAIL);
      doReturn(claims).when(jwtsSupport).getClaims(any());
      doReturn(new Date(System.currentTimeMillis() + Duration.ofMinutes(1).toMillis())).when(claims).getExpiration();
      doReturn(Optional.of(userEntity)).when(userRepository).findByPublicId(any());

      // when
      underTest.resetPasswordWithToken("token", newPassword);

      // then
      verify(passwordEncoder).encode(newPassword);
    }

    @Test
    @DisplayName("Changing the password of a user with valid token should call UserRepository to save user")
    void change_password_should_call_user_repository_to_save() {
      // given
      ArgumentCaptor<UserEntity> argumentCaptor = ArgumentCaptor.forClass(UserEntity.class);
      var claims = mock(Claims.class);
      var userEntity = UserEntityFactory.createUser(USERNAME, EMAIL);
      doReturn(claims).when(jwtsSupport).getClaims(any());
      doReturn(new Date(System.currentTimeMillis() + Duration.ofMinutes(1).toMillis())).when(claims).getExpiration();
      doReturn(Optional.of(userEntity)).when(userRepository).findByPublicId(any());
      doReturn(NEW_ENCRYPTED_PASSWORD).when(passwordEncoder).encode(any());

      // when
      underTest.resetPasswordWithToken("token", "newPW");

      // then
      verify(userRepository).save(argumentCaptor.capture());
      assertThat(argumentCaptor.getValue().getPassword()).isEqualTo(NEW_ENCRYPTED_PASSWORD);
    }

    @Test
    @DisplayName("Changing the password of a not existing user should throw exception")
    void change_password_should_throw_user_not_found() {
      // given
      var claims = mock(Claims.class);
      doReturn(claims).when(jwtsSupport).getClaims(any());
      doReturn(new Date(System.currentTimeMillis() + Duration.ofMinutes(1).toMillis())).when(claims).getExpiration();

      // when
      Throwable throwable = catchThrowable(() -> underTest.resetPasswordWithToken(TOKEN, NEW_PLAIN_PASSWORD));

      // then
      assertThat(throwable).isInstanceOf(ResourceNotFoundException.class);
      assertThat(throwable).hasMessageContaining(USER_WITH_ID_NOT_FOUND.toDisplayString());
    }

    @Test
    @DisplayName("Changing the password with an expired token should throw exception")
    void change_password_should_throw_token_expired() {
      // given
      var claims = mock(Claims.class);
      doReturn(claims).when(jwtsSupport).getClaims(any());
      doReturn(new Date(System.currentTimeMillis() - Duration.ofMinutes(1).toMillis())).when(claims).getExpiration();

      // when
      Throwable throwable = catchThrowable(() -> underTest.resetPasswordWithToken(TOKEN, NEW_PLAIN_PASSWORD));

      // then
      assertThat(throwable).isInstanceOf(TokenExpiredException.class);
      assertThat(throwable).hasMessageContaining(TOKEN_EXPIRED.toDisplayString());
    }

    @Test
    @DisplayName("Updating the current user's email address calls currentUserSupplier")
    void test_updating_email_calls_current_user_supplier() {
      // given
      doReturn(UserEntityFactory.createUser("user", "email")).when(authenticationFacade).getCurrentUser();
      doReturn(null).when(userTransformer).transform(any());

      // when
      underTest.updateCurrentEmail("email");

      // then
      verify(authenticationFacade).getCurrentUser();
    }

    @Test
    @DisplayName("should check if email already exists")
    void should_check_if_email_already_exists() {
      // given
      doReturn(UserEntityFactory.createUser("user", "mail@example.com")).when(authenticationFacade).getCurrentUser();
      doReturn(null).when(userTransformer).transform(any());

      // when
      underTest.updateCurrentEmail("new-mail@example.com");

      // then
      verify(userRepository).existsByEmail("new-mail@example.com");
    }

    @Test
    @DisplayName("should throw IllegalArgumentException if new email address already exists")
    void should_throw_exception_if_new_email_address_already_exists() {
      // given
      doReturn(UserEntityFactory.createUser("user", "mail@example.com")).when(authenticationFacade).getCurrentUser();
      doReturn(true).when(userRepository).existsByEmail(anyString());
      doReturn(null).when(userTransformer).transform(any());

      // when
      Throwable throwable = catchThrowable(() -> underTest.updateCurrentEmail("new-mail@example.com"));

      // then
      assertThat(throwable).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("should throw IllegalUserActionException if current user is oauth user")
    void should_throw_exception_if_user_is_oauth_user() {
      // given
      doReturn(OAuthUserFactory.createUser("user", "mail@mail.mail")).when(authenticationFacade).getCurrentUser();

      // when
      Throwable throwable = catchThrowable(() -> underTest.updateCurrentEmail("new-mail@example.com"));

      // then
      assertThat(throwable).isInstanceOf(IllegalUserActionException.class);
      assertThat(throwable).hasMessage(OAUTH_USER_CANNOT_CHANGE_EMAIL.toDisplayString());
    }

    @Test
    @DisplayName("should not throw IllegalArgumentException if the new email already exists and if it is the user's current email")
    void should_not_throw_exception_if_new_email_address_already_exists() {
      // given
      doReturn(UserEntityFactory.createUser("user", "new-mail@example.com")).when(authenticationFacade).getCurrentUser();
      doReturn(true).when(userRepository).existsByEmail(anyString());
      doReturn(null).when(userTransformer).transform(any());

      // when
      underTest.updateCurrentEmail("new-mail@example.com");

      // then
      assertThatNoException();
    }

    @Test
    @DisplayName("New email address is set on current user")
    void test_new_email_set() {
      // given
      ArgumentCaptor<UserEntity> userEntityCaptor = ArgumentCaptor.forClass(UserEntity.class);
      doReturn(UserEntityFactory.createUser("user", "email")).when(authenticationFacade).getCurrentUser();
      doReturn(null).when(userTransformer).transform(any());
      var newEmail = "newEmail";

      // when
      underTest.updateCurrentEmail(newEmail);

      // then
      verify(userRepository).save(userEntityCaptor.capture());
      UserEntity savedUser = userEntityCaptor.getValue();
      assertThat(savedUser.getEmail()).isEqualTo(newEmail);
    }

    @Test
    @DisplayName("Updated user is transformed")
    void test_updated_user_is_transformed() {
      // given
      var user = UserEntityFactory.createUser("user", "email");
      doReturn(user).when(authenticationFacade).getCurrentUser();
      doReturn(user).when(userRepository).save(any());
      doReturn(null).when(userTransformer).transform(any());

      // when
      underTest.updateCurrentEmail("email");

      // then
      verify(userTransformer).transform(user);
    }

    @Test
    @DisplayName("Updated user is returned")
    void test_updated_user_is_returned() {
      // given
      var user = UserEntityFactory.createUser("user", "email");
      var userDto = UserDtoFactory.createDefault();
      doReturn(user).when(authenticationFacade).getCurrentUser();
      doReturn(userDto).when(userTransformer).transform(any());

      // when
      var result = underTest.updateCurrentEmail("email");

      // then
      assertThat(result).isEqualTo(userDto);
    }

    @Test
    @DisplayName("Updating the current user's password calls currentUserSupplier")
    void test_updating_password_calls_current_user_supplier() {
      // given
      doReturn(UserEntityFactory.createUser("user", "email")).when(authenticationFacade).getCurrentUser();
      doReturn(true).when(passwordEncoder).matches(any(), any());

      // when
      underTest.updateCurrentPassword("oldPassword", "newPassword");

      // then
      verify(authenticationFacade).getCurrentUser();
    }

    @Test
    @DisplayName("Updating the current user's password matches old password")
    void test_updating_password_matches_old_password() {
      // given
      var user = UserEntityFactory.createUser("user", "email");
      doReturn(user).when(authenticationFacade).getCurrentUser();
      doReturn(true).when(passwordEncoder).matches(any(), any());
      var oldPassword = "oldPassword";
      var oldEncryptedPassword = user.getPassword();

      // when
      underTest.updateCurrentPassword(oldPassword, "newPassword");

      // then
      verify(passwordEncoder).matches(oldPassword, oldEncryptedPassword);
    }

    @Test
    @DisplayName("Updating the current user's password encodes new password")
    void test_updating_password_encodes_new_password() {
      // given
      doReturn(UserEntityFactory.createUser("user", "email")).when(authenticationFacade).getCurrentUser();
      doReturn(true).when(passwordEncoder).matches(any(), any());
      var newPassword = "newPassword";

      // when
      underTest.updateCurrentPassword("oldPassword", newPassword);

      // then
      verify(passwordEncoder).encode(newPassword);
    }

    @Test
    @DisplayName("Updating the current user's password saves updated user")
    void test_updating_password_saves_user() {
      // given
      ArgumentCaptor<UserEntity> argumentCaptor = ArgumentCaptor.forClass(UserEntity.class);
      doReturn(UserEntityFactory.createUser("user", "email")).when(authenticationFacade).getCurrentUser();
      doReturn(true).when(passwordEncoder).matches(any(), any());
      doReturn(NEW_ENCRYPTED_PASSWORD).when(passwordEncoder).encode(any());

      // when
      underTest.updateCurrentPassword("oldPassword", "newPassword");

      // then
      verify(userRepository).save(argumentCaptor.capture());
      UserEntity updatedUser = argumentCaptor.getValue();

      assertThat(updatedUser.getPassword()).isEqualTo(NEW_ENCRYPTED_PASSWORD);
    }

    @Test
    @DisplayName("Updating the current user's password throws Exception when old password does not match")
    void test_updating_password_throws_exception() {
      // given
      var user = UserEntityFactory.createUser("user", "email");
      doReturn(user).when(authenticationFacade).getCurrentUser();
      doReturn(false).when(passwordEncoder).matches(any(), any());

      // when
      var throwable = catchThrowable(() -> underTest.updateCurrentPassword("oldPassword", "newPassword"));

      // then
      assertThat(throwable).isInstanceOf(IllegalArgumentException.class);
      assertThat(throwable).hasMessageContaining("Old password does not match");
    }

    @Test
    @DisplayName("Updating the current user's password throws Exception when user is oauth user")
    void test_updating_password_throws_exception_for_oauth_user() {
      // given
      doReturn(OAuthUserFactory.createUser("user", "mail@mail.mail")).when(authenticationFacade).getCurrentUser();

      // when
      var throwable = catchThrowable(() -> underTest.updateCurrentPassword("oldPassword", "newPassword"));

      // then
      assertThat(throwable).isInstanceOf(IllegalUserActionException.class);
      assertThat(throwable).hasMessage(OAUTH_USER_CANNOT_CHANGE_PASSWORD.toDisplayString());
    }
  }

  @DisplayName("Delete current user tests")
  @Nested
  class DeleteCurrentUserTest {

    @Test
    @DisplayName("Current user is fetched")
    void delete_user_for_existing_user() {
      // given
      UserEntity user = UserEntityFactory.createUser(USERNAME, EMAIL);
      doReturn(user).when(authenticationFacade).getCurrentUser();

      // when
      underTest.deleteCurrentUser();

      // then
      verify(authenticationFacade).getCurrentUser();
    }

    @Test
    @DisplayName("Deletion event is published")
    void deletion_event_published() {
      // given
      ArgumentCaptor<UserDeletionEvent> argumentCaptor = ArgumentCaptor.forClass(UserDeletionEvent.class);
      UserEntity user = UserEntityFactory.createUser(USERNAME, EMAIL);
      doReturn(user).when(authenticationFacade).getCurrentUser();

      // when
      underTest.deleteCurrentUser();

      // then
      verify(applicationEventPublisher).publishEvent(argumentCaptor.capture());
      UserDeletionEvent userDeletionEvent = argumentCaptor.getValue();

      assertThat(userDeletionEvent.getSource()).isEqualTo(underTest);
      assertThat(userDeletionEvent.getUserEntity()).isEqualTo(user);
    }

    @Test
    @DisplayName("SecurityContext is cleared")
    void test_security_context_cleared() {
      try (MockedStatic<SecurityContextHolder> securityContextHolder = mockStatic(SecurityContextHolder.class)) {
        // when
        underTest.deleteCurrentUser();

        //then
        securityContextHolder.verify(SecurityContextHolder::clearContext);
      }
    }

    @Test
    @DisplayName("HttpSession is fetched from request")
    void http_session_fetched() {
      // given
      doReturn(mock(HttpSession.class)).when(request).getSession(anyBoolean());

      // when
      underTest.deleteCurrentUser();

      // then
      verify(request).getSession(false);
    }

    @Test
    @DisplayName("HttpSession is invalidated")
    void http_session_invalidated() {
      // given
      var mockSession = mock(HttpSession.class);
      doReturn(mockSession).when(request).getSession(anyBoolean());

      // when
      underTest.deleteCurrentUser();

      // then
      verify(mockSession).invalidate();
    }
  }

  @DisplayName("Load user tests")
  @Nested
  class LoadUserTest {

    @Test
    @DisplayName("Requesting an existing user by his username should return his user details")
    void load_user_by_username_for_existing_user() {
      // given
      UserEntity user = UserEntityFactory.createUser(USERNAME, EMAIL);
      when(userRepository.findByEmail(USERNAME)).thenReturn(Optional.empty());
      when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(user));
      when(request.getHeader(anyString())).thenReturn("666");

      // when
      UserDetails userDetails = underTest.loadUserByUsername(USERNAME);

      // then
      verify(userRepository).findByEmail(USERNAME);
      verify(userRepository).findByUsername(USERNAME);
      assertThat(userDetails).isNotNull();
      assertThat(userDetails.getUsername()).isEqualTo(user.getUsername());
      assertThat(userDetails.isEnabled()).isEqualTo(user.isEnabled());
    }

    @Test
    @DisplayName("Requesting a not existing user by username should throw an exception")
    void load_user_by_username_for_not_existing_user() {
      // given
      when(request.getHeader(anyString())).thenReturn("666");
      when(userRepository.findByEmail(USERNAME)).thenReturn(Optional.empty());
      when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.empty());

      // when
      Throwable throwable = catchThrowable(() -> underTest.loadUserByUsername(USERNAME));

      // then
      verify(userRepository).findByEmail(USERNAME);
      verify(userRepository).findByUsername(USERNAME);
      assertThat(throwable).isInstanceOf(UsernameNotFoundException.class);
      assertThat(throwable).hasMessageContaining(USER_NOT_FOUND.toDisplayString());
    }
  }

  @DisplayName("Login date tests")
  @Nested
  class LoginDateTest {

    @Test
    @DisplayName("Setting last login should set value on user")
    void set_last_login_should_set_value() {
      // given
      ArgumentCaptor<UserEntity> userEntityCaptor = ArgumentCaptor.forClass(UserEntity.class);
      UserEntity userEntity = UserEntityFactory.createUser(USERNAME, EMAIL);
      when(userRepository.findByPublicId(any())).thenReturn(Optional.of(userEntity));
      when(userRepository.save(any())).thenReturn(userEntity);

      // when
      underTest.persistSuccessfulLogin(userEntity.getPublicId());

      // then
      verify(userRepository).save(userEntityCaptor.capture());

      UserEntity entity = userEntityCaptor.getValue();
      assertThat(entity.getLastLogin()).isCloseTo(LocalDateTime.now(), new TemporalUnitLessThanOffset(1, ChronoUnit.SECONDS));
    }

    @Test
    @DisplayName("Setting last login should call user repository")
    void set_last_login_should_call_user_repository() {
      // given
      UserEntity userEntity = UserEntityFactory.createUser(USERNAME, EMAIL);
      when(userRepository.findByPublicId(any())).thenReturn(Optional.of(userEntity));

      // when
      underTest.persistSuccessfulLogin(userEntity.getPublicId());

      // then
      verify(userRepository).findByPublicId(userEntity.getPublicId());
    }

    @Test
    @DisplayName("Setting last login should do nothing when user not found")
    void set_last_login_should_do_nothing() {
      // given
      String notExistingId = "id";
      when(userRepository.findByPublicId(anyString())).thenReturn(Optional.empty());

      // when
      underTest.persistSuccessfulLogin(notExistingId);

      // then
      verify(userRepository).findByPublicId(notExistingId);

      // and
      verifyNoMoreInteractions(userRepository);
    }
  }
}
