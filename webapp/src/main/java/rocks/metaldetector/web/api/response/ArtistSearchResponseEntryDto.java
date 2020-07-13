package rocks.metaldetector.web.api.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class ArtistSearchResponseEntryDto {

  private String id;
  private String name;
  private String imageUrl;
  private String uri;
  private boolean followed;
  private List<String> genres;
  private int popularity;

}
