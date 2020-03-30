package rocks.metaldetector.butler.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@JsonPropertyOrder({
    "artist",
    "additionalArtists",
    "albumTitle",
    "releaseDate",
    "estimatedReleaseDate",
    "genre",
    "type",
    "metalArchivesArtistUrl",
    "metalArchivesAlbumUrl",
    "source",
    "state"
})
@NoArgsConstructor
public class ButlerRelease {

  @JsonProperty("artist")
  private String artist;

  @JsonProperty("additionalArtists")
  private List<String> additionalArtists;

  @JsonProperty("albumTitle")
  private String albumTitle;

  @JsonProperty("releaseDate")
  private LocalDate releaseDate;

  @JsonProperty("estimatedReleaseDate")
  private String estimatedReleaseDate;

  @JsonProperty("genre")
  private String genre;

  @JsonProperty("type")
  private ButlerReleaseType type;

  @JsonProperty("metalArchivesArtistUrl")
  private String metalArchivesArtistUrl;

  @JsonProperty("metalArchivesAlbumUrl")
  private String metalArchivesAlbumUrl;

  @JsonProperty("source")
  private ButlerReleaseSource source;

  @JsonProperty("state")
  private ButlerReleaseEntityRecordState state;

}
