package com.metalr2.service.mapper;

import com.metalr2.model.user.UserEntity;
import com.metalr2.web.dto.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    userDto.setRole(determineRoleName(entity));

    return userDto;
  }

  private String determineRoleName(UserEntity userEntity) {
    if (userEntity.isAdministrator()) {
      return "Administrator";
    }
    else if (userEntity.isUser()) {
      return "User";
    }
    else {
      log.warn("Unknown user role found for user with id {}", userEntity.getPublicId());
      return "Unknown";
    }
  }
}
