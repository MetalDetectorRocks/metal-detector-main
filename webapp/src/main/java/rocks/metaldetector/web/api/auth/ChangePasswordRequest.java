package rocks.metaldetector.web.api.auth;

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
public class ChangePasswordRequest {

  @NotBlank
  private String tokenString;

  @NotBlank
  @Size(min=8, message="Password length must be at least 8 characters")
  private String newPlainPassword;

}
