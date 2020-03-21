package rocks.metaldetector.butler.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@NoArgsConstructor
public class ButlerReleasesResponse {

  @JsonProperty("releases")
  private List<ReleaseDto> releases;

}
