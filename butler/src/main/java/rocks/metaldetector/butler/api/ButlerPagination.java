package rocks.metaldetector.butler.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ButlerPagination {

  @JsonProperty("currentPage")
  private int currentPage;

  @JsonProperty("size")
  private int size;

  @JsonProperty("totalPages")
  private int totalPages;

  @JsonProperty("totalReleases")
  private long totalReleases;

}
