package rocks.metaldetector.web.api.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class UpdateUserRequest {

  @NotBlank
  private String publicUserId;

  @NotBlank
  private String role;

  private boolean enabled;

}
