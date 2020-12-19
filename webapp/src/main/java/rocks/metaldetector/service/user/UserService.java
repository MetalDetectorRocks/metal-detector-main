package rocks.metaldetector.service.user;

import org.springframework.security.core.userdetails.UserDetailsService;
import rocks.metaldetector.service.notification.NotificationConfigDto;

import java.util.List;
import java.util.Optional;

public interface UserService extends UserDetailsService {

  UserDto createUser(UserDto userDto);

  UserDto createAdministrator(UserDto userDto);

  UserDto getUserByPublicId(String publicId);

  Optional<UserDto> getUserByEmailOrUsername(String emailOrUsername);

  UserDto updateUser(String publicId, UserDto userDto);

  UserDto updateCurrentUserNotificationConfig(NotificationConfigDto notificationConfig);

  void deleteUser(String publicId);

  List<UserDto> getAllUsers();

  List<UserDto> getAllActiveUsers();

  List<UserDto> getAllUsers(int page, int limit);

  void verifyEmailToken(String tokenString);

  void changePassword(String tokenString, String newPassword);

  void persistSuccessfulLogin(String publicUserId);

}
