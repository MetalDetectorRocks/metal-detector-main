package rocks.metaldetector.web.api.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import rocks.metaldetector.support.Pagination;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArtistSearchResponse {

  private String query;
  private Pagination pagination;
  private List<ArtistSearchResponseEntryDto> searchResults;

}
