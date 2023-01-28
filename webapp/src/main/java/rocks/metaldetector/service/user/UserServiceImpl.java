package rocks.metaldetector.service.user;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.hibernate.Hibernate;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.comparator.BooleanComparator;
import rocks.metaldetector.persistence.domain.user.AbstractUserEntity;
import rocks.metaldetector.persistence.domain.user.OAuthUserEntity;
import rocks.metaldetector.persistence.domain.user.UserEntity;
import rocks.metaldetector.persistence.domain.user.UserRepository;
import rocks.metaldetector.persistence.domain.user.UserRole;
import rocks.metaldetector.security.AuthenticationFacade;
import rocks.metaldetector.security.LoginAttemptService;
import rocks.metaldetector.service.exceptions.IllegalUserActionException;
import rocks.metaldetector.service.exceptions.UserAlreadyExistsException;
import rocks.metaldetector.service.user.events.OnRegistrationCompleteEvent;
import rocks.metaldetector.service.user.events.UserCreationEvent;
import rocks.metaldetector.service.user.events.UserDeletionEvent;
import rocks.metaldetector.support.JwtsSupport;
import rocks.metaldetector.support.exceptions.ResourceNotFoundException;
import rocks.metaldetector.web.api.auth.RegisterUserRequest;
import rocks.metaldetector.web.api.auth.RegistrationVerificationResponse;
import rocks.metaldetector.web.transformer.UserDtoTransformer;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static rocks.metaldetector.persistence.domain.user.UserRole.ROLE_ADMINISTRATOR;
import static rocks.metaldetector.service.user.UserErrorMessages.USER_NOT_FOUND;
import static rocks.metaldetector.service.user.UserErrorMessages.USER_WITH_ID_NOT_FOUND;

@Service
@Slf4j
@AllArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtsSupport jwtsSupport;
  private final UserTransformer userTransformer;
  private final UserDtoTransformer userDtoTransformer;
  private final AuthenticationFacade authenticationFacade;
  private final LoginAttemptService loginAttemptService;
  private final ApplicationEventPublisher eventPublisher;
  private final HttpServletRequest request;

  @Override
  @Transactional
  public UserDto createUser(RegisterUserRequest request) {
    UserDto userDto = userDtoTransformer.transformUserDto(request);
    UserDto createdUserDto = createUserEntity(userDto, UserRole.createUserRole(), false);
    eventPublisher.publishEvent(new OnRegistrationCompleteEvent(this, createdUserDto));
    return createdUserDto;
  }

  @Override
  @Transactional
  public UserDto createAdministrator(UserDto userDto) {
    return createUserEntity(userDto, UserRole.createAdministratorRole(), true);
  }

  private UserDto createUserEntity(UserDto userDto, Set<UserRole> roles, boolean enabled) {
    checkIfUserAlreadyExistsByUsername(userDto.getUsername(), userDto.getUsername());
    checkIfUserAlreadyExistsByEmail(userDto.getUsername(), userDto.getEmail());

    UserEntity userEntity = UserEntity.builder()
        .username(userDto.getUsername())
        .email(userDto.getEmail())
        .password(passwordEncoder.encode(userDto.getPlainPassword()))
        .userRoles(roles)
        .enabled(enabled)
        .build();

    UserEntity savedUserEntity = userRepository.save(userEntity);

    eventPublisher.publishEvent(new UserCreationEvent(this, savedUserEntity));

    return userTransformer.transform(savedUserEntity);
  }

  @Override
  public UserDto getUserByPublicId(String publicId) {
    AbstractUserEntity userEntity = userRepository.findByPublicId(publicId)
        .orElseThrow(() -> new ResourceNotFoundException(USER_WITH_ID_NOT_FOUND.toDisplayString()));

    return userTransformer.transform(userEntity);
  }

  @Override
  public Optional<UserDto> getUserByEmailOrUsername(String emailOrUsername) {
    Optional<AbstractUserEntity> userEntity = findByEmailOrUsername(emailOrUsername);
    return userEntity.map(userTransformer::transform);
  }

  @Override
  @Transactional
  public UserDto updateUser(String publicId, UserDto userDto) {
    AbstractUserEntity userEntity = userRepository.findByPublicId(publicId)
        .orElseThrow(() -> new ResourceNotFoundException(USER_WITH_ID_NOT_FOUND.toDisplayString()));
    AbstractUserEntity currentUser = authenticationFacade.getCurrentUser();

    if (publicId.equals(currentUser.getPublicId())) {
      if (!userDto.isEnabled()) {throw IllegalUserActionException.createAdminCannotDisableHimselfException();}
      if (userEntity.isAdministrator() && !UserRole.getRoleFromString(userDto.getRole()).contains(ROLE_ADMINISTRATOR)) {throw IllegalUserActionException.createAdminCannotDiscardHisRoleException();}
    }

    userEntity.setUserRoles(UserRole.getRoleFromString(userDto.getRole()));
    userEntity.setEnabled(userDto.isEnabled());

    AbstractUserEntity updatedUserEntity = userRepository.save((UserEntity) userEntity);

    return userTransformer.transform(updatedUserEntity);
  }

  @Override
  @Transactional
  public UserDto updateCurrentEmail(String emailAddress) {
    AbstractUserEntity currentUser = authenticationFacade.getCurrentUser();

    if (currentUser instanceof OAuthUserEntity) {
      throw IllegalUserActionException.createOAuthUserCannotChangeEMailException();
    }
    if (!currentUser.getEmail().equalsIgnoreCase(emailAddress) && userRepository.existsByEmail(emailAddress)) {
      throw new IllegalArgumentException("The email address is already in use!");
    }

    currentUser.setEmail(emailAddress);
    UserEntity updatedUser = userRepository.save((UserEntity) currentUser);
    return userTransformer.transform(updatedUser);
  }

  @Override
  public List<UserDto> getAllUsers() {
    return userRepository.findAll()
        .stream()
        .map(userTransformer::transform)
        .sorted(Comparator.comparing(UserDto::isEnabled, BooleanComparator.TRUE_LOW).thenComparing(UserDto::getRole).thenComparing(UserDto::getUsername))
        .collect(Collectors.toList());
  }

  @Override
  public List<UserDto> getAllActiveUsers() {
    return userRepository.findAll()
        .stream()
        .map(userTransformer::transform)
        .filter(UserDto::isEnabled)
        .collect(Collectors.toList());
  }

  @Override
  public UserDto getCurrentUser() {
    AbstractUserEntity currentUser = authenticationFacade.getCurrentUser();
    return userTransformer.transform(currentUser);
  }

  @Override
  @Transactional
  public UserDetails loadUserByUsername(String emailOrUsername) throws UsernameNotFoundException {
    return findByEmailOrUsername(emailOrUsername).orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND.toDisplayString()));
  }

  @Override
  @Transactional
  public void resetPasswordWithToken(String tokenString, String newPassword) {
    UserEntity userEntity = extractUserFromToken(tokenString);

    // set new password
    userEntity.setPassword(passwordEncoder.encode(newPassword));
    userRepository.save(userEntity);
  }

  @Override
  @Transactional
  public void persistSuccessfulLogin(String publicUserId) {
    Optional<AbstractUserEntity> userEntityOptional = userRepository.findByPublicId(publicUserId);

    if (userEntityOptional.isPresent()) {
      AbstractUserEntity userEntity = userEntityOptional.get();

      userEntity.setLastLogin(LocalDateTime.now());

      userRepository.save(userEntity);
    }
  }

  @Override
  @Transactional
  public void deleteCurrentUser() {
    AbstractUserEntity currentUser = authenticationFacade.getCurrentUser();
    eventPublisher.publishEvent(new UserDeletionEvent(this, currentUser));

    HttpSession session = request.getSession(false);
    SecurityContextHolder.clearContext();
    if (session != null) {
      session.invalidate();
    }
  }

  @Override
  @Transactional
  public void updateCurrentPassword(String oldPlainPassword, String newPlainPassword) {
    AbstractUserEntity currentUser = authenticationFacade.getCurrentUser();

    if (currentUser instanceof OAuthUserEntity) {
      throw IllegalUserActionException.createOAuthUserCannotChangePasswordException();
    }
    if (passwordEncoder.matches(oldPlainPassword, currentUser.getPassword())) {
      ((UserEntity) currentUser).setPassword(passwordEncoder.encode(newPlainPassword));
      userRepository.save(currentUser);
    }
    else {
      throw new IllegalArgumentException("Old password does not match");
    }
  }

  @Override
  @Transactional
  public RegistrationVerificationResponse verifyEmailToken(String tokenString) {
    AbstractUserEntity userEntity = extractUserFromToken(tokenString);
    userEntity.setEnabled(true);
    userRepository.save(userEntity);

    return new RegistrationVerificationResponse(userEntity.getUsername());
  }

  private Optional<AbstractUserEntity> findByEmailOrUsername(String emailOrUsername) {
    if (loginAttemptService.isBlocked(getClientIPHash())) {
      throw new LockedException("User " + emailOrUsername + " is blocked");
    }

    // try to find user by email
    Optional<AbstractUserEntity> userEntity = userRepository.findByEmail(emailOrUsername);

    // If no user was found by email, a user will be searched by username
    if (userEntity.isEmpty()) {
      userEntity = userRepository.findByUsername(emailOrUsername);
    }

    // make authorities available outside of transaction
    userEntity.ifPresent(abstractUserEntity -> Hibernate.initialize(abstractUserEntity.getAuthorities()));
    return userEntity;
  }

  private void checkIfUserAlreadyExistsByEmail(String username, String email) {
    if (userRepository.existsByEmail(email) || userRepository.existsByEmail(username)) {
      throw UserAlreadyExistsException.createUserWithEmailAlreadyExistsException();
    }
  }

  private void checkIfUserAlreadyExistsByUsername(String username, String email) {
    if (userRepository.existsByUsername(username) || userRepository.existsByUsername(email)) {
      throw UserAlreadyExistsException.createUserWithUsernameAlreadyExistsException();
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

  private UserEntity extractUserFromToken(String token) {
    var claims = jwtsSupport.getClaims(token);
    return (UserEntity) userRepository.findByPublicId(claims.getSubject())
        .orElseThrow(() -> new ResourceNotFoundException(USER_WITH_ID_NOT_FOUND.toDisplayString()));
  }
}
