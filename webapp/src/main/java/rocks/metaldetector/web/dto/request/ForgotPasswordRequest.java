package rocks.metaldetector.web.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ForgotPasswordRequest {

  @NotBlank
  private String emailOrUsername;

}
