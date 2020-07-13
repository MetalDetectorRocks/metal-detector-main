package rocks.metaldetector.spotify.facade.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import rocks.metaldetector.support.Pagination;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class SpotifyArtistSearchResultDto {

  private Pagination pagination;
  private List<SpotifyArtistSearchResultEntryDto> searchResults;
}
