package rocks.metaldetector.spotify.api.imports;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import rocks.metaldetector.spotify.api.SpotifyArtist;
import rocks.metaldetector.spotify.api.SpotifyImage;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonPropertyOrder({
    "album_type",
    "artists",
    "available_markets",
    "copyrights",
    "external_ids",
    "external_urls",
    "genres",
    "href",
    "id",
    "images",
    "label",
    "name",
    "release_date",
    "release_date_precision",
    "restrictions",
    "tracks",
    "type",
    "uri"
})
public class SpotifyAlbum {

  @JsonProperty("album_type")
  private String albumType;

  @JsonProperty("artists")
  private List<SpotifyArtist> artists;

  @JsonProperty("available_markets")
  private List<String> availableMarkets;

  @JsonProperty("copyrights")
  private List<SpotifyCopyright> copyrights;

  @JsonProperty("external_ids")
  private Map<String, String> externalIds;

  @JsonProperty("external_urls")
  private Map<String, String> externalUrls;

  @JsonProperty("genres")
  private List<String> genres;

  @JsonProperty("href")
  private String href;

  @JsonProperty("id")
  private String id;

  @JsonProperty("images")
  private List<SpotifyImage> images;

  @JsonProperty("label")
  private String label;

  @JsonProperty("name")
  private String name;

  @JsonProperty("popularity")
  private int popularity;

  @JsonProperty("release_date")
  private String releaseDate;

  @JsonProperty("release_date_precision")
  private String releaseDatePrecision;

  @JsonProperty("restrictions")
  private SpotifyRestriction restrictions;

  @JsonProperty("total_tracks")
  private int totalTracks;

  @JsonProperty("tracks")
  private SpotfiyTrackImportResult tracks;

  @JsonProperty("type")
  private String type;

  @JsonProperty("uri")
  private String uri;
}
