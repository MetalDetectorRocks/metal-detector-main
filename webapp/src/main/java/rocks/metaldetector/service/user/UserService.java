package rocks.metaldetector.service.user;

import org.springframework.security.core.userdetails.UserDetailsService;
import rocks.metaldetector.web.api.auth.RegisterUserRequest;
import rocks.metaldetector.web.api.auth.RegistrationVerificationResponse;

import java.util.List;
import java.util.Optional;

public interface UserService extends UserDetailsService {

  UserDto createUser(RegisterUserRequest request);

  UserDto createAdministrator(UserDto userDto);

  UserDto getUserByPublicId(String publicId);

  Optional<UserDto> getUserByEmailOrUsername(String emailOrUsername);

  UserDto updateUser(String publicId, UserDto userDto);

  UserDto updateCurrentEmail(String emailAddress);

  List<UserDto> getAllUsers();

  List<UserDto> getAllActiveUsers();

  UserDto getCurrentUser();

  void resetPasswordWithToken(String tokenString, String newPassword);

  void persistSuccessfulLogin(String publicUserId);

  void deleteCurrentUser();

  void updateCurrentPassword(String oldPlainPassword, String newPlainPassword);

  RegistrationVerificationResponse verifyEmailToken(String tokenString);
}
