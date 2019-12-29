package com.metalr2.service.user;

import com.metalr2.model.exceptions.EmailVerificationTokenExpiredException;
import com.metalr2.model.exceptions.ErrorMessages;
import com.metalr2.model.exceptions.ResourceNotFoundException;
import com.metalr2.model.exceptions.UserAlreadyExistsException;
import com.metalr2.model.token.*;
import com.metalr2.model.user.UserEntity;
import com.metalr2.model.user.UserFactory;
import com.metalr2.model.user.UserRepository;
import com.metalr2.model.user.UserRole;
import com.metalr2.service.mapper.UserMapper;
import com.metalr2.web.DtoFactory;
import com.metalr2.web.DtoFactory.UserDtoFactory;
import com.metalr2.web.dto.UserDto;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest implements WithAssertions {

  private static final String USERNAME           = "JohnD";
  private static final String EMAIL              = "john.doe@example.com";
  private static final String DUPLICATE_USERNAME = "Duplicate";
  private static final String DUPLICATE_EMAIL    = "duplicate@example.com";

  @Mock
  private TokenRepository tokenRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private BCryptPasswordEncoder passwordEncoder;

  @Mock
  private JwtsSupport jwtsSupport;

  @Spy
  private UserMapper userMapper;

  @InjectMocks
  private UserServiceImpl userService;

  @BeforeEach
  void setUp() {
  }

  @AfterEach
  void tearDown() {
    reset(tokenRepository, userRepository, passwordEncoder, jwtsSupport, userMapper);
  }

  @Test
  @DisplayName("Creating a new user with a username and email that does not already exist should work")
  void create_user_with_unique_username_and_email_should_be_ok() {
    // given
    ArgumentCaptor<UserEntity> userEntityCaptor = ArgumentCaptor.forClass(UserEntity.class);
    UserDto userDto = UserDtoFactory.withUsernameAndEmail(USERNAME, EMAIL);
    when(userRepository.existsByEmail(anyString())).thenReturn(false);
    when(userRepository.existsByUsername(anyString())).thenReturn(false);
    when(passwordEncoder.encode(anyString())).thenReturn("encrypted-password");
    when(userRepository.save(any(UserEntity.class))).thenReturn(UserFactory.createUser(USERNAME, EMAIL));

    // when
    UserDto createdUserDto = userService.createUser(userDto);

    // then
    verify(userRepository, times(1)).save(userEntityCaptor.capture());
    verify(userRepository, times(2)).existsByEmail(anyString());
    verify(userRepository, times(2)).existsByUsername(anyString());

    assertThat(createdUserDto).isNotNull();
    assertThat(createdUserDto.getUsername()).isEqualTo(USERNAME);
    assertThat(createdUserDto.getEmail()).isEqualTo(EMAIL);

    assertThat(userEntityCaptor.getValue().getUsername()).isEqualTo(USERNAME);
    assertThat(userEntityCaptor.getValue().getEmail()).isEqualTo(EMAIL);
    assertThat(userEntityCaptor.getValue().getPassword()).isEqualTo("encrypted-password");
    assertThat(userEntityCaptor.getValue().getUserRoles()).containsExactly(UserRole.ROLE_USER);
    assertThat(userEntityCaptor.getValue().isEnabled()).isFalse();
  }

  @ParameterizedTest(name = "[{index}] => Username <{0}> | Email <{1}>")
  @MethodSource("userDtoProvider")
  @DisplayName("Creating a new user with a username and email that already exists should throw exception")
  void create_user_with_username_or_email_that_already_exists_should_throw_exception(String username, String email, UserAlreadyExistsException.Reason reason) {
    // given
    UserDto userDto = DtoFactory.UserDtoFactory.withUsernameAndEmail(username, email);

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

  private static Stream<Arguments> userDtoProvider() {
    return Stream.of(
            Arguments.of(DUPLICATE_USERNAME, EMAIL, UserAlreadyExistsException.Reason.USERNAME_ALREADY_EXISTS),
            Arguments.of(DUPLICATE_EMAIL, EMAIL, UserAlreadyExistsException.Reason.USERNAME_ALREADY_EXISTS),
            Arguments.of(USERNAME, DUPLICATE_EMAIL, UserAlreadyExistsException.Reason.EMAIL_ALREADY_EXISTS),
            Arguments.of(USERNAME, DUPLICATE_USERNAME, UserAlreadyExistsException.Reason.EMAIL_ALREADY_EXISTS)
    );
  }

  @Test
  @DisplayName("Requesting an existing user by his public id should work")
  void get_user_by_public_id_for_existing_user() {
    // given
    String PUBLIC_ID = "public-id";
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
    String PUBLIC_ID = "public-id";
    when(userRepository.findByPublicId(PUBLIC_ID)).thenReturn(Optional.empty());

    // when
    Throwable throwable = catchThrowable(() -> userService.getUserByPublicId(PUBLIC_ID));

    // then
    verify(userRepository, times(1)).findByPublicId(PUBLIC_ID);
    assertThat(throwable).isInstanceOf(ResourceNotFoundException.class);
    assertThat(throwable).hasMessageContaining(ErrorMessages.USER_WITH_ID_NOT_FOUND.toDisplayString());
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
  @DisplayName("Updating an existing user should update the email address of the user")
  void update_user_for_existing_user() {
    // given
    String PUBLIC_ID = "public-id";
    String NEW_EMAIL = "updatedEmail@example.com";

    ArgumentCaptor<UserEntity> userEntityCaptor = ArgumentCaptor.forClass(UserEntity.class);
    UserDto                    userDtoForUpdate = UserDtoFactory.withUsernameAndEmail(USERNAME, NEW_EMAIL);
    UserEntity                 user             = UserFactory.createUser(USERNAME, EMAIL);

    when(userRepository.findByPublicId(PUBLIC_ID)).thenReturn(Optional.of(user));
    // return the same user without changing the mail is ok here, we don't want to concentrate on the DTO conversion in this test
    when(userRepository.save(any())).thenReturn(user);

    // when
    UserDto userDto = userService.updateUser(PUBLIC_ID, userDtoForUpdate);

    // then
    verify(userRepository, times(1)).findByPublicId(PUBLIC_ID);
    verify(userRepository, times(1)).save(userEntityCaptor.capture());
    assertThat(userDto).isNotNull();
    assertThat(userEntityCaptor.getValue().getUsername()).isEqualTo(userDtoForUpdate.getUsername());
    assertThat(userEntityCaptor.getValue().getEmail()).isEqualTo(NEW_EMAIL);
  }

  @Test
  @DisplayName("Updating a not existing user should throw exception")
  void update_user_for_not_existing_user() {
    // given
    String PUBLIC_ID = "public-id";
    String NEW_EMAIL = "updatedEmail@example.com";
    UserDto userDtoForUpdate = UserDtoFactory.withUsernameAndEmail(USERNAME, NEW_EMAIL);
    when(userRepository.findByPublicId(PUBLIC_ID)).thenReturn(Optional.empty());

    // when
    Throwable throwable = catchThrowable(() -> userService.updateUser(PUBLIC_ID, userDtoForUpdate));

    // then
    verify(userRepository, times(1)).findByPublicId(PUBLIC_ID);
    assertThat(throwable).isInstanceOf(ResourceNotFoundException.class);
    assertThat(throwable).hasMessageContaining(ErrorMessages.USER_WITH_ID_NOT_FOUND.toDisplayString());
  }

  @Test
  @DisplayName("Deleting an existing user should delete the user")
  void delete_user_for_existing_user() {
    // given
    String PUBLIC_ID = "public-id";
    UserEntity user  = UserFactory.createUser(USERNAME, EMAIL);
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
    String PUBLIC_ID = "public-id";
    when(userRepository.findByPublicId(PUBLIC_ID)).thenReturn(Optional.empty());

    // when
    Throwable throwable = catchThrowable(() -> userService.deleteUser(PUBLIC_ID));

    // then
    verify(userRepository, times(1)).findByPublicId(PUBLIC_ID);
    assertThat(throwable).isInstanceOf(ResourceNotFoundException.class);
    assertThat(throwable).hasMessageContaining(ErrorMessages.USER_WITH_ID_NOT_FOUND.toDisplayString());
  }

  @Test
  @DisplayName("Requesting all users should return a list of all users")
  void get_all_users() {
    // given
    UserEntity user1 = UserFactory.createUser("a", "a@example.com");
    UserEntity user2 = UserFactory.createUser("b", "b@example.com");
    when(userRepository.findAll()).thenReturn(List.of(user1, user2));

    // when
    List<UserDto> userDtoList = userService.getAllUsers();

    // then
    assertThat(userDtoList).hasSize(2);
    assertThat(userDtoList.get(0).getUsername()).isEqualTo(user1.getUsername());
    assertThat(userDtoList.get(0).getEmail()).isEqualTo(user1.getEmail());
    assertThat(userDtoList.get(1).getUsername()).isEqualTo(user2.getUsername());
    assertThat(userDtoList.get(1).getEmail()).isEqualTo(user2.getEmail());
    verify(userRepository, times(1)).findAll();
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
    assertThat(throwable).hasMessageContaining(ErrorMessages.USER_NOT_FOUND.toDisplayString());
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
    assertThat(throwable).hasMessageContaining(ErrorMessages.TOKEN_NOT_FOUND.toDisplayString());
  }

  @Test
  @DisplayName("Verifying the registration with an expired token should throw exception")
  void verify_registration_with_expired_token() throws InterruptedException {
    // given
    String TOKEN_STRING = "token";
    TokenEntity tokenEntity = TokenFactory.createToken(TokenType.EMAIL_VERIFICATION, 1);
    when(tokenRepository.findEmailVerificationToken(TOKEN_STRING)).thenReturn(Optional.of(tokenEntity));

    // when
    Thread.sleep(1); // wait 1ms so that the token can expire
    Throwable throwable = catchThrowable(() -> userService.verifyEmailToken(TOKEN_STRING));

    // then
    assertThat(throwable).isInstanceOf(EmailVerificationTokenExpiredException.class);
    assertThat(throwable).hasMessageContaining(ErrorMessages.EMAIL_VERIFICATION_TOKEN_EXPIRED.toDisplayString());
  }

  @Test
  @DisplayName("Changing the password of an user should work")
  void change_password() {
    // given
    final String NEW_PLAIN_PASSWORD     = "new-plain-password";
    final String NEW_ENCRYPTED_PASSWORD = "encryption".repeat(6); // an encrypted password must be 60 characters long

    ArgumentCaptor<UserEntity> userEntityCaptor = ArgumentCaptor.forClass(UserEntity.class);
    UserEntity userEntity = UserFactory.createUser(USERNAME, EMAIL);
    when(passwordEncoder.encode(NEW_PLAIN_PASSWORD)).thenReturn(NEW_ENCRYPTED_PASSWORD);

    // when
    userService.changePassword(userEntity, NEW_PLAIN_PASSWORD);

    // then
    verify(userRepository, times(1)).save(userEntityCaptor.capture());
    assertThat(userEntityCaptor.getValue().getPassword()).isEqualTo(NEW_ENCRYPTED_PASSWORD);
  }

}