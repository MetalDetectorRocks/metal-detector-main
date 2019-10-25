package com.metalr2.web.dto.request;

import com.metalr2.web.validation.FieldsValueMatch;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@FieldsValueMatch.List({
  @FieldsValueMatch(field = "newPlainPassword", fieldMatch = "verifyNewPlainPassword", message = "The Passwords must match")
})
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ChangePasswordRequest {

  @NotBlank
  private String tokenString;

  @NotBlank
  @Size(min=8, message="Password length must be at least 8 characters")
  private String newPlainPassword;

  @NotBlank
  @Size(min=8, message="Password length must be at least 8 characters")
  private String verifyNewPlainPassword;

}
