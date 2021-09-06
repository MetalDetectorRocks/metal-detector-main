package rocks.metaldetector.web.api.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import rocks.metaldetector.web.validation.FieldsValueMatch;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@FieldsValueMatch.List({
  @FieldsValueMatch(field = "plainPassword", fieldMatch = "verifyPlainPassword", message = "The passwords must match")
})
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class RegisterUserRequest {

  @NotBlank
  @Size(max=50, message="Username length must be at most 50 characters")
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
