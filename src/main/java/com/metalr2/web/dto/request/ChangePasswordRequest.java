package com.metalr2.web.dto.request;

import com.metalr2.web.validation.FieldsValueMatch;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@FieldsValueMatch.List({
  @FieldsValueMatch(field = "newPlainPassword", fieldMatch = "verifyNewPlainPassword", message = "The Passwords must match")
})
@Getter
@Setter
@NoArgsConstructor
public class ChangePasswordRequest {

  private String tokenString;

  @NotBlank
  @Size(min=8, message="Password length must be at least 8 characters")
  private String newPlainPassword;

  @NotBlank
  @Size(min=8, message="Password length must be at least 8 characters")
  private String verifyNewPlainPassword;

}
