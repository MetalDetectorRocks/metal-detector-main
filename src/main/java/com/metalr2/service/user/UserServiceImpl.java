package com.metalr2.service.user;

import com.metalr2.model.exceptions.EmailVerificationTokenExpiredException;
import com.metalr2.model.exceptions.ErrorMessages;
import com.metalr2.model.exceptions.ResourceNotFoundException;
import com.metalr2.model.exceptions.UserAlreadyExistsException;
import com.metalr2.model.token.TokenEntity;
import com.metalr2.model.token.TokenRepository;
import com.metalr2.model.user.UserEntity;
import com.metalr2.model.user.UserRepository;
import com.metalr2.model.user.UserRole;
import com.metalr2.web.dto.UserDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

  private final UserRepository  userRepository;
  private final PasswordEncoder passwordEncoder;
  private final TokenRepository tokenRepository;
  private final ModelMapper     mapper;

  @Autowired
  public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, TokenRepository tokenRepository) {
    this.userRepository  = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.tokenRepository = tokenRepository;
    this.mapper          = new ModelMapper();
  }

  @Override
  @Transactional
  public UserDto createUser(UserDto userDto) {
    checkIfUserAlreadyExists(userDto.getUsername(), userDto.getEmail());

    // enhance UserEntity
    UserEntity userEntity = mapper.map(userDto, UserEntity.class);
    userEntity.setEncryptedPassword(passwordEncoder.encode(userDto.getPassword()));
    userEntity.setUserRoles(UserRole.createUserRole());

    // create user
    UserEntity savedUserEntity = userRepository.save(userEntity);

    return mapper.map(savedUserEntity, UserDto.class);
  }

  @Override
  @Transactional(readOnly = true)
  public UserDto getUserByPublicId(String publicId) {
    UserEntity userEntity = userRepository.findByPublicId(publicId)
                                          .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.USER_WITH_ID_NOT_FOUND.toDisplayString()));

    return mapper.map(userEntity, UserDto.class);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<UserDto> getUserByEmailOrUsername(String emailOrUsername) {
    Optional<UserEntity> userEntity = findByEmailOrUsername(emailOrUsername);

    if (userEntity.isEmpty()) {
      return Optional.empty();
    }

    UserDto userDto = mapper.map(userEntity.get(), UserDto.class);
    return Optional.of(userDto);
  }

  @Override
  @Transactional
  public UserDto updateUser(String publicId, UserDto userDto) {
    UserEntity userEntity = userRepository.findByPublicId(publicId)
                                          .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.USER_WITH_ID_NOT_FOUND.toDisplayString()));

    userEntity.setEmail(userDto.getEmail());
    UserEntity updatedUserEntity = userRepository.save(userEntity);

    return mapper.map(updatedUserEntity, UserDto.class);
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
            .map(userEntity -> mapper.map(userEntity, UserDto.class))
            .collect(Collectors.toList());
  }

  @Override
  @Transactional(readOnly = true)
  public List<UserDto> getAllUsers(int page, int limit) {
    Pageable pageable = PageRequest.of(page, limit);

    return userRepository.findAll(pageable)
            .stream()
            .map(userEntity -> mapper.map(userEntity, UserDto.class))
            .collect(Collectors.toList());
  }

  @Override
  @Transactional(readOnly = true)
  public UserDetails loadUserByUsername(String emailOrUsername) throws UsernameNotFoundException {
    UserEntity userEntity = findByEmailOrUsername(emailOrUsername)
                            .orElseThrow(() -> new UsernameNotFoundException(ErrorMessages.USER_NOT_FOUND.toDisplayString()));

    return new User(userEntity.getUsername(), userEntity.getEncryptedPassword(), userEntity.isEnabled(), true, true, true, Collections.emptyList());
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

    // verify registration
    UserEntity userEntity = tokenEntity.getUser();
    userEntity.setEnabled(true);
    userRepository.save(userEntity);

    tokenRepository.delete(tokenEntity);
  }

  @Override
  @Transactional
  public void changePassword(UserEntity userEntity, String newPassword) {
    userEntity.setEncryptedPassword(passwordEncoder.encode(newPassword));
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
