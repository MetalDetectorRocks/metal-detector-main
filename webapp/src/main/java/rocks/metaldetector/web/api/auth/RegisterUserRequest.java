package rocks.metaldetector.web.api.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

}
