package rocks.metaldetector.web.api.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReleasesResponse {

  private String artist;
  private List<String> additionalArtists;
  private String albumTitle;
  private LocalDate releaseDate;
  private String estimatedReleaseDate;
  private String genre;
  private String type;
  private String metalArchivesArtistUrl;
  private String metalArchivesAlbumUrl;
  private String source;
  private String state;

}
