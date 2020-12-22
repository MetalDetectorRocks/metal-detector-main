package rocks.metaldetector.web.api.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import rocks.metaldetector.service.artist.ArtistDto;
import rocks.metaldetector.support.Pagination;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MyArtistsResponse {

  private List<ArtistDto> myArtists;
  private Pagination pagination;

}
