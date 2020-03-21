package rocks.metaldetector.service.user;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rocks.metaldetector.persistence.domain.user.UserEntity;
import rocks.metaldetector.web.dto.UserDto;

@Service
@Slf4j
public class UserMapper implements Mappable<UserEntity, UserDto> {

  private final ModelMapper mapper;

  @Autowired
  public UserMapper() {
    mapper = new ModelMapper();
  }

  @Override
  public UserEntity mapToEntity(UserDto dto) {
    throw new UnsupportedOperationException("Because of the encryption of the password, you should manually map the UserDto into a UserEntity");
  }

  @Override
  public UserDto mapToDto(UserEntity entity) {
    UserDto userDto = mapper.map(entity, UserDto.class);
    userDto.setRole(entity.getHighestRole().getDisplayName());
    return userDto;
  }
}
