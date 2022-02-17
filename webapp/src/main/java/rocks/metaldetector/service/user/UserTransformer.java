package rocks.metaldetector.service.user;

import org.springframework.stereotype.Component;
import rocks.metaldetector.persistence.domain.user.AbstractUserEntity;
import rocks.metaldetector.persistence.domain.user.UserEntity;

@Component
public class UserTransformer {

  public UserDto transform(AbstractUserEntity entity) {
    return UserDto.builder()
        .publicId(entity.getPublicId())
        .username(entity.getUsername())
        .email(entity.getEmail())
        .avatar(entity.getAvatar())
        .enabled(entity.isEnabled())
        .role(entity.getHighestRole().getDisplayName())
        .lastLogin(entity.getLastLogin())
        .createdBy(entity.getCreatedBy())
        .createdDateTime(entity.getCreatedDateTime())
        .lastModifiedDateTime(entity.getLastModifiedDateTime())
        .lastModifiedBy(entity.getLastModifiedBy())
        .nativeUser(entity instanceof UserEntity)
        .build();
  }
}
