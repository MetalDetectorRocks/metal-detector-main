package com.metalr2.web;

import com.metalr2.web.dto.UserDto;
import com.metalr2.web.dto.request.RegisterUserRequest;

import java.util.UUID;

public class DtoFactory {

  public static class UserDtoFactory {

    public static UserDto withUsernameAndEmail(String username, String email) {
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

  public static class RegisterUserRequestFactory {

    public static RegisterUserRequest withUsername(String username) {
      return create(username, "john.d@example.com", "valid-password", "valid-password");
    }

    public static RegisterUserRequest withEmail(String email) {
      return create("JohnD", email, "valid-password", "valid-password");
    }

    public static RegisterUserRequest withPassword(String plainPassword, String verifyPlainPassword) {
      return create("JohnD", "john.d@example.com", plainPassword, verifyPlainPassword);
    }

    private static RegisterUserRequest create(String username, String email, String plainPassword, String verifyPlainPassword) {
      return RegisterUserRequest.builder()
              .username(username)
              .email(email)
              .plainPassword(plainPassword)
              .verifyPlainPassword(verifyPlainPassword)
              .build();
    }

  }

}
