package rocks.metaldetector.web.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import rocks.metaldetector.support.infrastructure.ArtifactForFramework;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class UpdateEmailRequest {

  @NotBlank
  @Email
  private String emailAddress;

  @JsonProperty("data")
  @ArtifactForFramework
  private void unpackNested(Map<String, String> data) {
    this.emailAddress = data.get("emailAddress");
  }
}
