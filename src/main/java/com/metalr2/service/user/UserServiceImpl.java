package com.metalr2.service.user;

import com.metalr2.model.exceptions.EmailVerificationTokenExpiredException;
import com.metalr2.model.exceptions.ErrorMessages;
import com.metalr2.model.exceptions.ResourceNotFoundException;
import com.metalr2.model.exceptions.UserAlreadyExistsException;
import com.metalr2.model.token.JwtsSupport;
import com.metalr2.model.token.TokenEntity;
import com.metalr2.model.token.TokenRepository;
import com.metalr2.model.user.UserEntity;
import com.metalr2.model.user.UserRepository;
import com.metalr2.model.user.UserRole;
import com.metalr2.service.mapper.UserMapper;
import com.metalr2.web.dto.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

  private final UserRepository  userRepository;
  private final PasswordEncoder passwordEncoder;
  private final TokenRepository tokenRepository;
  private final JwtsSupport     jwtsSupport;
  private final UserMapper      userMapper;

  @Autowired
  public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder,
                         TokenRepository tokenRepository, JwtsSupport jwtsSupport, UserMapper userMapper) {
    this.userRepository  = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.tokenRepository = tokenRepository;
    this.jwtsSupport     = jwtsSupport;
    this.userMapper      = userMapper;
  }

  @Override
  @Transactional
  public UserDto createUser(UserDto userDto) {
    checkIfUserAlreadyExists(userDto.getUsername(), userDto.getEmail());

    // create user
    UserEntity userEntity = UserEntity.builder()
            .username(userDto.getUsername())
            .email(userDto.getEmail())
            .password(passwordEncoder.encode(userDto.getPlainPassword()))
            .userRoles(UserRole.createUserRole())
            .build();

    // persist user
    UserEntity savedUserEntity = userRepository.save(userEntity);

    return userMapper.mapToDto(savedUserEntity);
  }

  @Override
  @Transactional(readOnly = true)
  public UserDto getUserByPublicId(String publicId) {
    UserEntity userEntity = userRepository.findByPublicId(publicId)
                                          .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.USER_WITH_ID_NOT_FOUND.toDisplayString()));

    return userMapper.mapToDto(userEntity);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<UserDto> getUserByEmailOrUsername(String emailOrUsername) {
    Optional<UserEntity> userEntity = findByEmailOrUsername(emailOrUsername);
    return userEntity.map(userMapper::mapToDto);
  }

  @Override
  @Transactional
  public UserDto updateUser(String publicId, UserDto userDto) {
    UserEntity userEntity = userRepository.findByPublicId(publicId)
                                          .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.USER_WITH_ID_NOT_FOUND.toDisplayString()));

    userEntity.setEmail(userDto.getEmail());
    UserEntity updatedUserEntity = userRepository.save(userEntity);

    return userMapper.mapToDto(updatedUserEntity);
  }

  @Override
  @Transactional
  public void deleteUser(String publicId) {
    UserEntity userEntity = userRepository.findByPublicId(publicId)
                                          .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.USER_WITH_ID_NOT_FOUND.toDisplayString()));
    userRepository.delete(userEntity);
  }

  @Override
  @Transactional(readOnly = true)
  public List<UserDto> getAllUsers() {
    return userRepository.findAll()
            .stream()
            .map(userMapper::mapToDto)
            .collect(Collectors.toList());
  }

  @Override
  @Transactional(readOnly = true)
  public List<UserDto> getAllUsers(int page, int limit) {
    Pageable pageable = PageRequest.of(page, limit);

    return userRepository.findAll(pageable)
            .stream()
            .map(userMapper::mapToDto)
            .collect(Collectors.toList());
  }

  @Override
  @Transactional(readOnly = true)
  public UserDetails loadUserByUsername(String emailOrUsername) throws UsernameNotFoundException {
    return findByEmailOrUsername(emailOrUsername)
           .orElseThrow(() -> new UsernameNotFoundException(ErrorMessages.USER_NOT_FOUND.toDisplayString()));
  }

  @Override
  @Transactional
  public void verifyEmailToken(String tokenString) {
    // check if token exists
    TokenEntity tokenEntity = tokenRepository.findEmailVerificationToken(tokenString)
                                             .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.TOKEN_NOT_FOUND.toDisplayString()));

    // check if token is expired
    if (tokenEntity.isExpired()) {
      throw new EmailVerificationTokenExpiredException();
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
  public void changePassword(UserEntity userEntity, String newPassword) {
    userEntity.setPassword(passwordEncoder.encode(newPassword));
    userRepository.save(userEntity);
  }

  private Optional<UserEntity> findByEmailOrUsername(String emailOrUsername) {
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

}
