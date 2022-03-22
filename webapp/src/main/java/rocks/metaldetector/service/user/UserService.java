package rocks.metaldetector.service.user;

import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Optional;

public interface UserService extends UserDetailsService {

  UserDto createUser(UserDto userDto);

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

  void verifyEmailToken(String tokenString);
}
