package rocks.metaldetector.service.user;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import rocks.metaldetector.persistence.domain.user.UserEntity;

@Component
public class UserTransformer {

  private final ModelMapper mapper;

  public UserTransformer() {
    this.mapper = new ModelMapper();
  }

  public UserDto transform(UserEntity entity) {
    UserDto userDto = mapper.map(entity, UserDto.class);
    userDto.setRole(entity.getHighestRole().getDisplayName());
    return userDto;
  }
}
