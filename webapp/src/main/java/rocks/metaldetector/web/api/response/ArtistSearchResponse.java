package rocks.metaldetector.web.api.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import rocks.metaldetector.support.Pagination;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class ArtistSearchResponse {

  private Pagination pagination;
  private List<ArtistSearchResponseEntryDto> searchResults;

}
