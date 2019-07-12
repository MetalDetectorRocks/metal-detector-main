package com.metalr2.web.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@Getter
@Setter
public class UserLoginRequest {

  @Email
  @NotBlank
  private String email;

  @NotBlank
  private String password;

}
