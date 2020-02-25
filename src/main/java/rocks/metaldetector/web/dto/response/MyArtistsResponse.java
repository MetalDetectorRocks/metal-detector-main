package rocks.metaldetector.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import rocks.metaldetector.web.dto.ArtistDto;

import java.util.List;

@Data
@AllArgsConstructor
public class MyArtistsResponse {

  private List<ArtistDto> myArtists;
  private Pagination pagination;

}
