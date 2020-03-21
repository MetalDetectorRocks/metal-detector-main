package rocks.metaldetector.discogs.facade.dto;

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
public class DiscogsArtistSearchResultDto {

  private Pagination pagination;
  private List<DiscogsArtistSearchResultEntryDto> searchResults;
}
