package com.metalr2.web;

import com.metalr2.web.dto.UserDto;

import java.util.UUID;

public class DtoFactory {

  public static UserDto createUser(String username, String email) {
    return UserDto.builder()
                  .id(1)
                  .publicId(UUID.randomUUID().toString())
                  .username(username)
                  .email(email)
                  .plainPassword("xxx")
                  .enabled(true)
                  .build();
  }

}
