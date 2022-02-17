package rocks.metaldetector.web.transformer;

import org.springframework.stereotype.Component;
import rocks.metaldetector.service.user.UserDto;
import rocks.metaldetector.web.api.request.RegisterUserRequest;
import rocks.metaldetector.web.api.request.UpdateUserRequest;
import rocks.metaldetector.web.api.response.UserResponse;

@Component
public class UserDtoTransformer {

  public UserDto transformUserDto(RegisterUserRequest request) {
    return UserDto.builder()
        .username(request.getUsername())
        .email(request.getEmail())
        .plainPassword(request.getPlainPassword())
        .build();
  }

  public UserDto transformUserDto(UpdateUserRequest request) {
    return UserDto.builder()
        .publicId(request.getPublicUserId())
        .role(request.getRole())
        .enabled(request.isEnabled())
        .build();
  }

  public UserResponse transformUserResponse(UserDto dto) {
    return UserResponse.builder()
        .publicId(dto.getPublicId())
        .username(dto.getUsername())
        .email(dto.getEmail())
        .enabled(dto.isEnabled())
        .role(dto.getRole())
        .lastLogin(dto.getLastLogin())
        .createdDateTime(dto.getCreatedDateTime())
        .lastModifiedDateTime(dto.getLastModifiedDateTime())
        .lastModifiedBy(dto.getLastModifiedBy())
        .nativeUser(dto.isNativeUser())
        .build();
  }
}
