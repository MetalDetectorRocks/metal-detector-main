package com.metalr2.web.dto.request;

import com.metalr2.web.validation.FieldsValueMatch;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@FieldsValueMatch.List({
  @FieldsValueMatch(field = "plainPassword", fieldMatch = "verifyPlainPassword", message = "The Passwords must match")
})
@NoArgsConstructor
@Getter
@Setter
public class RegisterUserRequest {

  @NotBlank
  private String username;

  @NotBlank
  @Email
  private String email;

  @NotBlank
  @Size(min=8, message="Password length must be at least 8 characters")
  private String plainPassword;

  @NotBlank
  @Size(min=8, message="Password length must be at least 8 characters")
  private String verifyPlainPassword;

}
