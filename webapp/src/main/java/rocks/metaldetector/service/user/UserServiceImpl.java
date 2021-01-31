package rocks.metaldetector.service.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.comparator.BooleanComparator;
import rocks.metaldetector.persistence.domain.notification.NotificationConfigEntity;
import rocks.metaldetector.persistence.domain.notification.NotificationConfigRepository;
import rocks.metaldetector.persistence.domain.token.TokenEntity;
import rocks.metaldetector.persistence.domain.token.TokenRepository;
import rocks.metaldetector.persistence.domain.user.UserEntity;
import rocks.metaldetector.persistence.domain.user.UserRepository;
import rocks.metaldetector.persistence.domain.user.UserRole;
import rocks.metaldetector.security.CurrentUserSupplier;
import rocks.metaldetector.security.LoginAttemptService;
import rocks.metaldetector.service.exceptions.IllegalUserActionException;
import rocks.metaldetector.service.exceptions.TokenExpiredException;
import rocks.metaldetector.service.exceptions.UserAlreadyExistsException;
import rocks.metaldetector.service.token.TokenService;
import rocks.metaldetector.service.user.events.UserDeletionEvent;
import rocks.metaldetector.support.JwtsSupport;
import rocks.metaldetector.support.exceptions.ResourceNotFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static rocks.metaldetector.persistence.domain.user.UserRole.ROLE_ADMINISTRATOR;

@Service
@Slf4j
@AllArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final TokenRepository tokenRepository;
  private final JwtsSupport jwtsSupport;
  private final UserTransformer userTransformer;
  private final NotificationConfigRepository notificationConfigRepository;
  private final TokenService tokenService;
  private final CurrentUserSupplier currentUserSupplier;
  private final LoginAttemptService loginAttemptService;
  private final ApplicationEventPublisher applicationEventPublisher;
  private final HttpServletRequest request;

  @Override
  @Transactional
  public UserDto createUser(UserDto userDto) {
    return createUserEntity(userDto, UserRole.createUserRole(), false);
  }

  @Override
  @Transactional
  public UserDto createAdministrator(UserDto userDto) {
    return createUserEntity(userDto, UserRole.createAdministratorRole(), true);
  }

  private UserDto createUserEntity(UserDto userDto, Set<UserRole> roles, boolean enabled) {
    checkIfUserAlreadyExists(userDto.getUsername(), userDto.getEmail());

    // create user
    UserEntity userEntity = UserEntity.builder()
        .username(userDto.getUsername())
        .email(userDto.getEmail())
        .password(passwordEncoder.encode(userDto.getPlainPassword()))
        .userRoles(roles)
        .enabled(enabled)
        .build();

    UserEntity savedUserEntity = userRepository.save(userEntity);

    // create user's notification config
    NotificationConfigEntity notificationConfigEntity = NotificationConfigEntity.builder()
        .user(savedUserEntity)
        .build();
    notificationConfigRepository.save(notificationConfigEntity);

    return userTransformer.transform(savedUserEntity);
  }

  @Override
  @Transactional(readOnly = true)
  public UserDto getUserByPublicId(String publicId) {
    UserEntity userEntity = userRepository.findByPublicId(publicId)
        .orElseThrow(() -> new ResourceNotFoundException(UserErrorMessages.USER_WITH_ID_NOT_FOUND.toDisplayString()));

    return userTransformer.transform(userEntity);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<UserDto> getUserByEmailOrUsername(String emailOrUsername) {
    Optional<UserEntity> userEntity = findByEmailOrUsername(emailOrUsername);
    return userEntity.map(userTransformer::transform);
  }

  @Override
  @Transactional
  public UserDto updateUser(String publicId, UserDto userDto) {
    UserEntity userEntity = userRepository.findByPublicId(publicId)
        .orElseThrow(() -> new ResourceNotFoundException(UserErrorMessages.USER_WITH_ID_NOT_FOUND.toDisplayString()));
    UserEntity currentUser = currentUserSupplier.get();

    if (publicId.equals(currentUser.getPublicId())) {
      if (!userDto.isEnabled()) { throw IllegalUserActionException.createAdminCannotDisableHimselfException(); }
      if (userEntity.isAdministrator() && !UserRole.getRoleFromString(userDto.getRole()).contains(ROLE_ADMINISTRATOR)) { throw IllegalUserActionException.createAdminCannotDiscardHisRoleException(); }
    }

    userEntity.setUserRoles(UserRole.getRoleFromString(userDto.getRole()));
    userEntity.setEnabled(userDto.isEnabled());

    UserEntity updatedUserEntity = userRepository.save(userEntity);

    return userTransformer.transform(updatedUserEntity);
  }

  @Override
  @Transactional
  public UserDto updateCurrentEmail(String emailAddress) {
    if (userRepository.existsByEmail(emailAddress)) {
      throw new IllegalArgumentException("emailAddress already in use");
    }

    UserEntity currentUser = currentUserSupplier.get();
    currentUser.setEmail(emailAddress);
    UserEntity updatedUser = userRepository.save(currentUser);
    return userTransformer.transform(updatedUser);
  }

  @Override
  @Transactional(readOnly = true)
  public List<UserDto> getAllUsers() {
    return userRepository.findAll()
        .stream()
        .map(userTransformer::transform)
        .sorted(Comparator.comparing(UserDto::isEnabled, BooleanComparator.TRUE_LOW).thenComparing(UserDto::getRole).thenComparing(UserDto::getUsername))
        .collect(Collectors.toList());
  }

  @Override
  @Transactional(readOnly = true)
  public List<UserDto> getAllActiveUsers() {
    return userRepository.findAll()
        .stream()
        .map(userTransformer::transform)
        .filter(UserDto::isEnabled)
        .collect(Collectors.toList());
  }

  @Override
  @Transactional(readOnly = true)
  public UserDto getCurrentUser() {
    UserEntity currentUser = currentUserSupplier.get();
    return userTransformer.transform(currentUser);
  }

  @Override
  @Transactional(readOnly = true)
  public UserDetails loadUserByUsername(String emailOrUsername) throws UsernameNotFoundException {
    return findByEmailOrUsername(emailOrUsername).orElseThrow(() -> new UsernameNotFoundException(UserErrorMessages.USER_NOT_FOUND.toDisplayString()));
  }

  @Override
  @Transactional
  public void verifyEmailToken(String tokenString) {
    // check if token exists
    TokenEntity tokenEntity = tokenRepository.findEmailVerificationToken(tokenString)
        .orElseThrow(() -> new ResourceNotFoundException(UserErrorMessages.TOKEN_NOT_FOUND.toDisplayString()));

    // check if token is expired
    if (tokenEntity.isExpired()) {
      throw new TokenExpiredException();
    }

    // get claims to check signature of token
    jwtsSupport.getClaims(tokenString);

    // verify registration
    UserEntity userEntity = tokenEntity.getUser();
    userEntity.setEnabled(true);
    userRepository.save(userEntity);

    tokenRepository.delete(tokenEntity);
  }

  @Override
  @Transactional
  public void changePassword(String tokenString, String newPassword) {
    // 1. get claims to check signature of token
    jwtsSupport.getClaims(tokenString);

    // 2. get user from token if it exists
    TokenEntity tokenEntity = tokenService.getResetPasswordTokenByTokenString(tokenString)
        .orElseThrow(() -> new ResourceNotFoundException(UserErrorMessages.TOKEN_NOT_FOUND.toDisplayString()));

    // 3. check if token is expired
    if (tokenEntity.isExpired()) {
      throw new TokenExpiredException();
    }

    UserEntity userEntity = tokenEntity.getUser();

    // 4. set new password
    userEntity.setPassword(passwordEncoder.encode(newPassword));
    userRepository.save(userEntity);

    // 5. remove token from database
    tokenService.deleteToken(tokenEntity);
  }

  @Override
  @Transactional
  public void persistSuccessfulLogin(String publicUserId) {
    Optional<UserEntity> userEntityOptional = userRepository.findByPublicId(publicUserId);

    if (userEntityOptional.isPresent()) {
      UserEntity userEntity = userEntityOptional.get();

      userEntity.setLastLogin(LocalDateTime.now());

      userRepository.save(userEntity);
    }
  }

  @Override
  @Transactional
  public void deleteCurrentUser() {
    UserEntity currentUser = currentUserSupplier.get();
    applicationEventPublisher.publishEvent(new UserDeletionEvent(this, currentUser));

    HttpSession session = request.getSession(false);
    SecurityContextHolder.clearContext();
    if (session != null) {
      session.invalidate();
    }
  }

  private Optional<UserEntity> findByEmailOrUsername(String emailOrUsername) {
    if (loginAttemptService.isBlocked(getClientIPHash())) {
      throw new LockedException("User " + emailOrUsername + " is blocked");
    }

    // try to find user by email
    Optional<UserEntity> userEntity = userRepository.findByEmail(emailOrUsername);

    // If no user was found by email, a user will be searched by username
    if (userEntity.isEmpty()) {
      userEntity = userRepository.findByUsername(emailOrUsername);
    }

    return userEntity;
  }

  /*
   * It's not allowed to use someones username or email.
   * It's also not allowed to use someones email as username and vice versa.
   */
  private void checkIfUserAlreadyExists(String username, String email) {
    if (userRepository.existsByUsername(username) || userRepository.existsByEmail(username)) {
      throw UserAlreadyExistsException.createUserWithUsernameAlreadyExistsException();
    }
    else if (userRepository.existsByEmail(email) || userRepository.existsByUsername(email)) {
      throw UserAlreadyExistsException.createUserWithEmailAlreadyExistsException();
    }
  }

  private String getClientIPHash() {
    String xfHeader = request.getHeader("X-Forwarded-For");
    String clientIp;

    if (xfHeader == null) {
      clientIp = request.getRemoteAddr();
    }
    else {
      clientIp = xfHeader.split(",")[0];
    }

    return DigestUtils.md5Hex(clientIp);
  }
}
