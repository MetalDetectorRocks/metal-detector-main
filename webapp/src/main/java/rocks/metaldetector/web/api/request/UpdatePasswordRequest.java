package rocks.metaldetector.web.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import rocks.metaldetector.support.infrastructure.ArtifactForFramework;
import rocks.metaldetector.web.validation.FieldsValueMatch;

import java.util.Map;

@FieldsValueMatch.List({
    @FieldsValueMatch(field = "newPlainPassword", fieldMatch = "verifyNewPlainPassword", message = "The passwords must match")
})
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class UpdatePasswordRequest {

  @NotBlank
  private String oldPlainPassword;

  @NotBlank
  @Size(min=8, message="Password length must be at least 8 characters")
  private String newPlainPassword;

  @NotBlank
  @Size(min=8, message="Password length must be at least 8 characters")
  private String verifyNewPlainPassword;

  @JsonProperty("data")
  @ArtifactForFramework
  private void unpackNested(Map<String, String> data) {
    this.oldPlainPassword = data.get("oldPlainPassword");
    this.newPlainPassword = data.get("newPlainPassword");
    this.verifyNewPlainPassword = data.get("verifyNewPlainPassword");
  }
}
