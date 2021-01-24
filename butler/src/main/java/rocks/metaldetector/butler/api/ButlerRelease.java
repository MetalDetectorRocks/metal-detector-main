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
    "id",
    "artist",
    "additionalArtists",
    "albumTitle",
    "releaseDate",
    "announcementDate",
    "estimatedReleaseDate",
    "genre",
    "type",
    "metalArchivesArtistUrl",
    "metalArchivesAlbumUrl",
    "source",
    "state",
    "coverUrl"
})
@NoArgsConstructor
public class ButlerRelease {

  @JsonProperty("id")
  private long id;

  @JsonProperty("artist")
  private String artist;

  @JsonProperty("additionalArtists")
  private List<String> additionalArtists;

  @JsonProperty("albumTitle")
  private String albumTitle;

  @JsonProperty("releaseDate")
  private LocalDate releaseDate;

  @JsonProperty("announcementDate")
  private LocalDate announcementDate;

  @JsonProperty("estimatedReleaseDate")
  private String estimatedReleaseDate;

  @JsonProperty("genre")
  private String genre;

  @JsonProperty("type")
  private String type;

  @JsonProperty("artistDetailsUrl")
  private String artistDetailsUrl;

  @JsonProperty("releaseDetailsUrl")
  private String releaseDetailsUrl;

  @JsonProperty("source")
  private String source;

  @JsonProperty("state")
  private String state;

  @JsonProperty("coverUrl")
  private String coverUrl;

}
