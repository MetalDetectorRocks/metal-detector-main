package rocks.metaldetector.service.user;

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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import rocks.metaldetector.support.ResourceNotFoundException;
import rocks.metaldetector.service.exceptions.TokenExpiredException;
import rocks.metaldetector.service.exceptions.UserAlreadyExistsException;
import rocks.metaldetector.service.token.TokenFactory;
import rocks.metaldetector.support.JwtsSupport;
import rocks.metaldetector.persistence.domain.token.TokenEntity;
import rocks.metaldetector.persistence.domain.token.TokenRepository;
import rocks.metaldetector.persistence.domain.token.TokenType;
import rocks.metaldetector.persistence.domain.user.UserEntity;
import rocks.metaldetector.persistence.domain.user.UserRepository;
import rocks.metaldetector.persistence.domain.user.UserRole;
import rocks.metaldetector.security.CurrentUserSupplier;
import rocks.metaldetector.service.token.TokenService;
import rocks.metaldetector.testutil.DtoFactory.UserDtoFactory;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static rocks.metaldetector.persistence.domain.user.UserRole.ROLE_ADMINISTRATOR;
import static rocks.metaldetector.persistence.domain.user.UserRole.ROLE_USER;

@ExtendWith(MockitoExtension.class)
class UserServiceTest implements WithAssertions {

  private static final String USERNAME = "JohnD";
  private static final String EMAIL = "john.doe@example.com";
  private static final String DUPLICATE_USERNAME = "Duplicate";
  private static final String DUPLICATE_EMAIL = "duplicate@example.com";
  private static final String PUBLIC_ID = "public-id";
  private final String TOKEN = "user-token";
  private final String NEW_PLAIN_PASSWORD = "new-plain-password";
  private final String NEW_ENCRYPTED_PASSWORD = "encryption".repeat(6); // an encrypted password must be 60 characters long

  @Mock
  private TokenRepository tokenRepository;

  @Mock
  private UserRepository userRepository;

  @Spy
  private BCryptPasswordEncoder passwordEncoder;

  @Mock
  private TokenService tokenService;

  @Mock
  private JwtsSupport jwtsSupport;

  @Mock
  private CurrentUserSupplier currentUserSupplier;

  @Spy
  private UserMapper userMapper;

  @InjectMocks
  private UserServiceImpl userService;

  @BeforeEach
  void setUp() {
  }

  @AfterEach
  void tearDown() {
    reset(tokenRepository, userRepository, passwordEncoder, jwtsSupport, userMapper, tokenService, currentUserSupplier);
  }

  @DisplayName("Create user tests")
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  @Nested
  class CreateUserTest {

    @Test
    @DisplayName("Should return UserDto")
    void should_return_user_dto() {
      // given
      UserDto givenUserDto = UserDtoFactory.withUsernameAndEmail(USERNAME, EMAIL);
      UserEntity expectedUserEntity = UserFactory.createUser(USERNAME, EMAIL);
      when(userRepository.save(any(UserEntity.class))).thenReturn(expectedUserEntity);

      // when
      UserDto createdUserDto = userService.createUser(givenUserDto);

      // then
      assertThat(createdUserDto).isEqualTo(userMapper.mapToDto(expectedUserEntity));
    }

    @DisplayName("Should pass disabled UserEntity with Role USER to UserRepository")
    @Test
    void should_use_user_repository() {
      // given
      ArgumentCaptor<UserEntity> userEntityCaptor = ArgumentCaptor.forClass(UserEntity.class);
      UserDto givenUserDto = UserDtoFactory.withUsernameAndEmail(USERNAME, EMAIL);
      when(userRepository.save(any(UserEntity.class))).thenReturn(UserFactory.createUser(USERNAME, EMAIL));

      // when
      userService.createUser(givenUserDto);

      // then
      verify(userRepository, times(1)).save(userEntityCaptor.capture());
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
      when(userRepository.save(any(UserEntity.class))).thenReturn(UserFactory.createUser(USERNAME, EMAIL));

      // when
      userService.createUser(userDto);

      // then
      verify(userRepository, times(2)).existsByEmail(anyString());
      verify(userRepository, times(2)).existsByUsername(anyString());
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
      Throwable throwable = catchThrowable(() -> userService.createUser(userDto));

      // then
      assertThat(throwable).isInstanceOf(UserAlreadyExistsException.class);
      assertThat(((UserAlreadyExistsException) throwable).getReason()).isEqualTo(reason);
      verify(userRepository, atMost(2)).existsByEmail(anyString());
      verify(userRepository, atMost(2)).existsByUsername(anyString());
    }

    private Stream<Arguments> userDtoProvider() {
      return Stream.of(
          Arguments.of(DUPLICATE_USERNAME, EMAIL, UserAlreadyExistsException.Reason.USERNAME_ALREADY_EXISTS),
          Arguments.of(DUPLICATE_EMAIL, EMAIL, UserAlreadyExistsException.Reason.USERNAME_ALREADY_EXISTS),
          Arguments.of(USERNAME, DUPLICATE_EMAIL, UserAlreadyExistsException.Reason.EMAIL_ALREADY_EXISTS),
          Arguments.of(USERNAME, DUPLICATE_USERNAME, UserAlreadyExistsException.Reason.EMAIL_ALREADY_EXISTS)
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
      UserEntity expectedUserEntity = UserFactory.createUser(USERNAME, EMAIL);
      when(userRepository.save(any(UserEntity.class))).thenReturn(expectedUserEntity);

      // when
      UserDto createdUserDto = userService.createAdministrator(givenUserDto);

      // then
      assertThat(createdUserDto).isEqualTo(userMapper.mapToDto(expectedUserEntity));
    }

    @Test
    @DisplayName("Should check if email or username is already used")
    void should_check_email_and_username() {
      // given
      UserDto userDto = UserDtoFactory.withUsernameAndEmail(USERNAME, EMAIL);
      when(userRepository.save(any(UserEntity.class))).thenReturn(UserFactory.createUser(USERNAME, EMAIL));

      // when
      userService.createAdministrator(userDto);

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
      when(userRepository.save(any(UserEntity.class))).thenReturn(UserFactory.createUser(USERNAME, EMAIL));

      // when
      userService.createAdministrator(givenUserDto);

      // then
      verify(userRepository, times(1)).save(userEntityCaptor.capture());
      UserEntity passedUserEntity = userEntityCaptor.getValue();
      assertThat(passedUserEntity.getUsername()).isEqualTo(USERNAME);
      assertThat(passedUserEntity.getEmail()).isEqualTo(EMAIL);
      assertThat(passedUserEntity.getPassword()).isNotEmpty();
      assertThat(passedUserEntity.getUserRoles()).containsExactly(ROLE_ADMINISTRATOR);
      assertThat(passedUserEntity.isEnabled()).isTrue();
    }
  }

  @Test
  @DisplayName("Requesting an existing user by his public id should work")
  void get_user_by_public_id_for_existing_user() {
    // given
    UserEntity user = UserFactory.createUser(USERNAME, EMAIL);
    when(userRepository.findByPublicId(PUBLIC_ID)).thenReturn(Optional.of(user));

    // when
    UserDto userDto = userService.getUserByPublicId(PUBLIC_ID);

    // then
    verify(userRepository, times(1)).findByPublicId(PUBLIC_ID);
    assertThat(userDto.getUsername()).isEqualTo(user.getUsername());
    assertThat(userDto.getEmail()).isEqualTo(user.getEmail());
  }

  @Test
  @DisplayName("Requesting a not existing user by a public id should throw exception")
  void get_user_by_public_id_for_not_existing_user() {
    // given
    when(userRepository.findByPublicId(PUBLIC_ID)).thenReturn(Optional.empty());

    // when
    Throwable throwable = catchThrowable(() -> userService.getUserByPublicId(PUBLIC_ID));

    // then
    verify(userRepository, times(1)).findByPublicId(PUBLIC_ID);
    assertThat(throwable).isInstanceOf(ResourceNotFoundException.class);
    assertThat(throwable).hasMessageContaining(UserErrorMessages.USER_WITH_ID_NOT_FOUND.toDisplayString());
  }

  @Test
  @DisplayName("Requesting an existing user by his email should work")
  void get_user_by_email_or_username_with_email() {
    // given
    UserEntity user = UserFactory.createUser(USERNAME, EMAIL);
    when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
    when(userRepository.findByUsername(EMAIL)).thenReturn(Optional.empty());

    // when
    Optional<UserDto> userDto = userService.getUserByEmailOrUsername(EMAIL);

    // then
    verify(userRepository, times(1)).findByEmail(EMAIL);
    verify(userRepository, never()).findByUsername(EMAIL);
    assertThat(userDto).isPresent();
    assertThat(userDto.get().getUsername()).isEqualTo(USERNAME);
    assertThat(userDto.get().getEmail()).isEqualTo(EMAIL);
  }

  @Test
  @DisplayName("Requesting an existing user by his username should work")
  void get_user_by_email_or_username_with_username() {
    // given
    UserEntity user = UserFactory.createUser(USERNAME, EMAIL);
    when(userRepository.findByEmail(USERNAME)).thenReturn(Optional.empty());
    when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(user));

    // when
    Optional<UserDto> userDto = userService.getUserByEmailOrUsername(USERNAME);

    // then
    verify(userRepository, times(1)).findByEmail(USERNAME);
    verify(userRepository, times(1)).findByUsername(USERNAME);
    assertThat(userDto).isPresent();
    assertThat(userDto.get().getUsername()).isEqualTo(USERNAME);
    assertThat(userDto.get().getEmail()).isEqualTo(EMAIL);
  }

  @Test
  @DisplayName("Requesting a not existing user by email or username should return empty optional")
  void get_user_by_email_or_username_for_not_existing_user() {
    // given
    String NOT_EXISTING = "not-existing";
    when(userRepository.findByEmail(NOT_EXISTING)).thenReturn(Optional.empty());
    when(userRepository.findByUsername(NOT_EXISTING)).thenReturn(Optional.empty());

    // when
    Optional<UserDto> userDto = userService.getUserByEmailOrUsername(NOT_EXISTING);

    // then
    verify(userRepository, times(1)).findByEmail(NOT_EXISTING);
    verify(userRepository, times(1)).findByUsername(NOT_EXISTING);
    assertThat(userDto).isEmpty();
  }

  @Test
  @DisplayName("Updating an existing user should update the user's role")
  void update_user_role_for_existing_user() {
    // given
    ArgumentCaptor<UserEntity> userEntityCaptor = ArgumentCaptor.forClass(UserEntity.class);
    UserDto userDtoForUpdate = UserDtoFactory.withUsernameAndEmail(USERNAME, EMAIL);
    userDtoForUpdate.setRole("Administrator");
    UserEntity user = UserFactory.createUser(USERNAME, EMAIL);

    when(currentUserSupplier.get()).thenReturn(user);
    when(userRepository.findByPublicId(PUBLIC_ID)).thenReturn(Optional.of(user));
    // return the same user without changing the mail is ok here, we don't want to concentrate on the DTO conversion in this test
    when(userRepository.save(any())).thenReturn(user);

    // when
    UserDto userDto = userService.updateUser(PUBLIC_ID, userDtoForUpdate);

    // then
    verify(userRepository, times(1)).findByPublicId(PUBLIC_ID);
    verify(userRepository, times(1)).save(userEntityCaptor.capture());
    verify(currentUserSupplier, times(1)).get();
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
    UserEntity user = UserFactory.createUser(USERNAME, EMAIL);

    when(currentUserSupplier.get()).thenReturn(user);
    when(userRepository.findByPublicId(PUBLIC_ID)).thenReturn(Optional.of(user));
    // return the same user without changing the mail is ok here, we don't want to concentrate on the DTO conversion in this test
    when(userRepository.save(any())).thenReturn(user);

    // when
    UserDto userDto = userService.updateUser(PUBLIC_ID, userDtoForUpdate);

    // then
    verify(userRepository, times(1)).findByPublicId(PUBLIC_ID);
    verify(userRepository, times(1)).save(userEntityCaptor.capture());
    verify(currentUserSupplier, times(1)).get();
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
    Throwable throwable = catchThrowable(() -> userService.updateUser(PUBLIC_ID, userDtoForUpdate));

    // then
    verify(userRepository, times(1)).findByPublicId(PUBLIC_ID);
    assertThat(throwable).isInstanceOf(ResourceNotFoundException.class);
    assertThat(throwable).hasMessageContaining(UserErrorMessages.USER_WITH_ID_NOT_FOUND.toDisplayString());
  }

  @Test
  @DisplayName("Updating should fail if an admin tries to remove his own role")
  void update_should_fail_when_admin_removes_his_role() {
    // given
    UserDto userDtoForUpdate = UserDtoFactory.withUsernameAndEmail(USERNAME, EMAIL);
    userDtoForUpdate.setRole("User");
    UserEntity user = UserFactory.createUser(USERNAME, EMAIL);
    user.setPublicId(PUBLIC_ID);
    user.setUserRoles(UserRole.createAdministratorRole());

    when(currentUserSupplier.get()).thenReturn(user);
    when(userRepository.findByPublicId(PUBLIC_ID)).thenReturn(Optional.of(user));

    // when
    Throwable throwable = catchThrowable(() -> userService.updateUser(PUBLIC_ID, userDtoForUpdate));

    // then
    assertThat(throwable).isInstanceOf(IllegalArgumentException.class);
    assertThat(throwable).hasMessage(UserErrorMessages.ADMINISTRATOR_DISCARD_ROLE.toDisplayString());
  }

  @Test
  @DisplayName("Updating should fail if an admin tries to disable himself")
  void update_should_fail_when_admin_disabled() {
    // given
    UserDto userDtoForUpdate = UserDtoFactory.withUsernameAndEmail(USERNAME, EMAIL);
    userDtoForUpdate.setEnabled(false);
    UserEntity user = UserFactory.createUser(USERNAME, EMAIL);
    user.setPublicId(PUBLIC_ID);
    user.setUserRoles(UserRole.createAdministratorRole());

    when(currentUserSupplier.get()).thenReturn(user);
    when(userRepository.findByPublicId(PUBLIC_ID)).thenReturn(Optional.of(user));

    // when
    Throwable throwable = catchThrowable(() -> userService.updateUser(PUBLIC_ID, userDtoForUpdate));

    // then
    assertThat(throwable).isInstanceOf(IllegalArgumentException.class);
    assertThat(throwable).hasMessage(UserErrorMessages.ADMINISTRATOR_CANNOT_DISABLE_HIMSELF.toDisplayString());
  }

  @Test
  @DisplayName("Deleting an existing user should delete the user")
  void delete_user_for_existing_user() {
    // given
    UserEntity user = UserFactory.createUser(USERNAME, EMAIL);
    when(userRepository.findByPublicId(PUBLIC_ID)).thenReturn(Optional.of(user));

    // when
    userService.deleteUser(PUBLIC_ID);

    // then
    verify(userRepository, times(1)).findByPublicId(PUBLIC_ID);
    verify(userRepository, times(1)).delete(user);
  }

  @Test
  @DisplayName("Deleting a not existing user should throw exception")
  void delete_user_for_not_existing_user() {
    // given
    when(userRepository.findByPublicId(PUBLIC_ID)).thenReturn(Optional.empty());

    // when
    Throwable throwable = catchThrowable(() -> userService.deleteUser(PUBLIC_ID));

    // then
    verify(userRepository, times(1)).findByPublicId(PUBLIC_ID);
    assertThat(throwable).isInstanceOf(ResourceNotFoundException.class);
    assertThat(throwable).hasMessageContaining(UserErrorMessages.USER_WITH_ID_NOT_FOUND.toDisplayString());
  }

  @Test
  @DisplayName("Should return all users")
  void get_all_users() {
    // given
    UserEntity user1 = UserFactory.createUser("a", "a@example.com");
    UserEntity user2 = UserFactory.createUser("b", "b@example.com");
    when(userRepository.findAll()).thenReturn(List.of(user1, user2));

    // when
    List<UserDto> userDtoList = userService.getAllUsers();

    // then
    assertThat(userDtoList).hasSize(2);
    assertThat(userDtoList.get(0)).isEqualTo(userMapper.mapToDto(user1));
    assertThat(userDtoList.get(1)).isEqualTo(userMapper.mapToDto(user2));
  }

  @Test
  @DisplayName("Should use UserRepository to return a list of all users")
  void get_all_users_uses_user_repository() {
    // given
    when(userRepository.findAll()).thenReturn(Collections.emptyList());

    // when
    userService.getAllUsers();

    // then
    verify(userRepository, times(1)).findAll();
  }

  @Test
  @DisplayName("Should return all users in a certain order")
  void get_all_users_ordered() {
    // given
    UserEntity user1 = UserFactory.createUser("a1", ROLE_USER, false);
    UserEntity user2 = UserFactory.createUser("b1", ROLE_USER, true);
    UserEntity user3 = UserFactory.createUser("a2", ROLE_USER, true);
    UserEntity user4 = UserFactory.createUser("c1", ROLE_ADMINISTRATOR, false);
    UserEntity user5 = UserFactory.createUser("c2", ROLE_ADMINISTRATOR, true);
    UserEntity user6 = UserFactory.createUser("a3", ROLE_ADMINISTRATOR, true);
    when(userRepository.findAll()).thenReturn(List.of(user1, user2, user3, user4, user5, user6));

    // when
    List<UserDto> userDtoList = userService.getAllUsers();

    // then
    assertThat(userDtoList).hasSize(6);
    assertThat(userDtoList.get(0)).isEqualTo(userMapper.mapToDto(user6));
    assertThat(userDtoList.get(1)).isEqualTo(userMapper.mapToDto(user5));
    assertThat(userDtoList.get(2)).isEqualTo(userMapper.mapToDto(user3));
    assertThat(userDtoList.get(3)).isEqualTo(userMapper.mapToDto(user2));
    assertThat(userDtoList.get(4)).isEqualTo(userMapper.mapToDto(user4));
    assertThat(userDtoList.get(5)).isEqualTo(userMapper.mapToDto(user1));
  }

  @Test
  @DisplayName("Requesting all users with page and limit should return a sub list")
  void get_all_users_with_pagination() {
    // given
    int PAGE = 1;
    int LIMIT = 2;
    UserEntity user1 = UserFactory.createUser("a", "a@example.com");
    UserEntity user2 = UserFactory.createUser("b", "b@example.com");
    PageImpl<UserEntity> page = new PageImpl<>(List.of(user1, user2), PageRequest.of(PAGE, LIMIT), 4);
    when(userRepository.findAll(PageRequest.of(PAGE, LIMIT))).thenReturn(page);

    // when
    List<UserDto> userDtoList = userService.getAllUsers(PAGE, LIMIT);

    // then
    assertThat(userDtoList).hasSize(2);
    assertThat(userDtoList.get(0).getUsername()).isEqualTo(user1.getUsername());
    assertThat(userDtoList.get(0).getEmail()).isEqualTo(user1.getEmail());
    assertThat(userDtoList.get(1).getUsername()).isEqualTo(user2.getUsername());
    assertThat(userDtoList.get(1).getEmail()).isEqualTo(user2.getEmail());
    verify(userRepository, times(1)).findAll(PageRequest.of(PAGE, LIMIT));
  }

  @Test
  @DisplayName("Requesting an existing user by his username should return his user details")
  void load_user_by_username_for_existing_user() {
    // given
    UserEntity user = UserFactory.createUser(USERNAME, EMAIL);
    when(userRepository.findByEmail(USERNAME)).thenReturn(Optional.empty());
    when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(user));

    // when
    UserDetails userDetails = userService.loadUserByUsername(USERNAME);

    // then
    verify(userRepository, times(1)).findByEmail(USERNAME);
    verify(userRepository, times(1)).findByUsername(USERNAME);
    assertThat(userDetails).isNotNull();
    assertThat(userDetails.getUsername()).isEqualTo(user.getUsername());
    assertThat(userDetails.isEnabled()).isEqualTo(user.isEnabled());
  }

  @Test
  @DisplayName("Requesting a not existing user by username should throw an exception")
  void load_user_by_username_for_not_existing_user() {
    // given
    when(userRepository.findByEmail(USERNAME)).thenReturn(Optional.empty());
    when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.empty());

    // when
    Throwable throwable = catchThrowable(() -> userService.loadUserByUsername(USERNAME));

    // then
    verify(userRepository, times(1)).findByEmail(USERNAME);
    verify(userRepository, times(1)).findByUsername(USERNAME);
    assertThat(throwable).isInstanceOf(UsernameNotFoundException.class);
    assertThat(throwable).hasMessageContaining(UserErrorMessages.USER_NOT_FOUND.toDisplayString());
  }

  @Test
  @DisplayName("Verifying the registration with an existing and not expired token should work")
  void verify_registration_with_valid_token() {
    // given
    ArgumentCaptor<UserEntity> userEntityCaptor = ArgumentCaptor.forClass(UserEntity.class);
    String TOKEN_STRING = "token";
    TokenEntity tokenEntity = TokenFactory.createToken(TokenType.EMAIL_VERIFICATION, Duration.ofHours(1).toMillis());
    tokenEntity.getUser().setEnabled(false); // should be set to true within verification process
    when(tokenRepository.findEmailVerificationToken(TOKEN_STRING)).thenReturn(Optional.of(tokenEntity));

    // when
    userService.verifyEmailToken(TOKEN_STRING);

    // then
    verify(userRepository, times(1)).save(userEntityCaptor.capture());
    verify(tokenRepository, times(1)).delete(tokenEntity);
    assertThat(userEntityCaptor.getValue().isEnabled()).isTrue();
  }

  @Test
  @DisplayName("Verifying the registration with a not existing token should throw exception")
  void verify_registration_with_not_existing_token() {
    // given
    String TOKEN_STRING = "token";
    when(tokenRepository.findEmailVerificationToken(TOKEN_STRING)).thenReturn(Optional.empty());

    // when
    Throwable throwable = catchThrowable(() -> userService.verifyEmailToken(TOKEN_STRING));

    // then
    assertThat(throwable).isInstanceOf(ResourceNotFoundException.class);
    assertThat(throwable).hasMessageContaining(UserErrorMessages.TOKEN_NOT_FOUND.toDisplayString());
  }

  @Test
  @DisplayName("Verifying the registration with an expired token should throw exception")
  void verify_registration_with_expired_token() throws InterruptedException {
    // given
    TokenEntity tokenEntity = TokenFactory.createToken(TokenType.EMAIL_VERIFICATION, 1);
    when(tokenRepository.findEmailVerificationToken(TOKEN)).thenReturn(Optional.of(tokenEntity));

    // when
    Thread.sleep(1); // wait 1ms so that the token can expire
    Throwable throwable = catchThrowable(() -> userService.verifyEmailToken(TOKEN));

    // then
    assertThat(throwable).isInstanceOf(TokenExpiredException.class);
    assertThat(throwable).hasMessageContaining(UserErrorMessages.TOKEN_EXPIRED.toDisplayString());
  }

  @Test
  @DisplayName("Changing the password of a user should work")
  void change_password() {
    // given
    ArgumentCaptor<UserEntity> userEntityCaptor = ArgumentCaptor.forClass(UserEntity.class);
    UserEntity userEntity = UserFactory.createUser(USERNAME, EMAIL);
    TokenEntity tokenEntity = TokenFactory.createToken(TokenType.PASSWORD_RESET, userEntity);
    when(tokenService.getResetPasswordTokenByTokenString(TOKEN)).thenReturn(Optional.of(tokenEntity));
    when(passwordEncoder.encode(NEW_PLAIN_PASSWORD)).thenReturn(NEW_ENCRYPTED_PASSWORD);

    // when
    userService.changePassword(TOKEN, NEW_PLAIN_PASSWORD);

    // then
    verify(userRepository, times(1)).save(userEntityCaptor.capture());
    verify(tokenService, times(1)).getResetPasswordTokenByTokenString(TOKEN);
    verify(tokenService, times(1)).deleteToken(tokenEntity);
    verify(jwtsSupport, times(1)).getClaims(TOKEN);
    assertThat(userEntityCaptor.getValue().getPassword()).isEqualTo(NEW_ENCRYPTED_PASSWORD);
  }

  @Test
  @DisplayName("Changing the password should throw exception if token does not exist")
  void change_password_should_throw_resource_not_found() {
    // given
    when(tokenService.getResetPasswordTokenByTokenString(TOKEN)).thenReturn(Optional.empty());

    // when
    Throwable throwable = catchThrowable(() -> userService.changePassword(TOKEN, NEW_PLAIN_PASSWORD));

    // then
    assertThat(throwable).isInstanceOf(ResourceNotFoundException.class);
    assertThat(throwable).hasMessageContaining(UserErrorMessages.TOKEN_NOT_FOUND.toDisplayString());
  }

  @Test
  @DisplayName("Changing the password with an expired token should throw exception")
  void change_password_should_throw_token_expired() throws InterruptedException {
    // given
    TokenEntity tokenEntity = TokenFactory.createToken(TokenType.PASSWORD_RESET, 1);
    when(tokenService.getResetPasswordTokenByTokenString(TOKEN)).thenReturn(Optional.of(tokenEntity));

    // when
    Thread.sleep(1); // wait 1ms so that the token can expire
    Throwable throwable = catchThrowable(() -> userService.changePassword(TOKEN, NEW_PLAIN_PASSWORD));

    // then
    assertThat(throwable).isInstanceOf(TokenExpiredException.class);
    assertThat(throwable).hasMessageContaining(UserErrorMessages.TOKEN_EXPIRED.toDisplayString());
  }
}