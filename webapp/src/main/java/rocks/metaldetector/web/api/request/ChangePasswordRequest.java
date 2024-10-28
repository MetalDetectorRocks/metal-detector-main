package rocks.metaldetector.web.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import rocks.metaldetector.web.validation.FieldsValueMatch;

@FieldsValueMatch.List({
  @FieldsValueMatch(field = "newPlainPassword", fieldMatch = "verifyNewPlainPassword", message = "The passwords must match")
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
