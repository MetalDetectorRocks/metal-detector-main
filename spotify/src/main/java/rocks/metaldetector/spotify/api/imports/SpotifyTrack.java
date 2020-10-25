package rocks.metaldetector.spotify.api.imports;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import rocks.metaldetector.spotify.api.SpotifyArtist;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonPropertyOrder({
    "artists",
    "available_markets",
    "disc_number",
    "duration_ms",
    "explicit",
    "external_urls",
    "href",
    "id",
    "is_playable",
    "linked_from",
    "restrictions",
    "name",
    "preview_url",
    "track_number",
    "type",
    "uri",
    "is_local"
})
public class SpotifyTrack {

  @JsonProperty("artists")
  private List<SpotifyArtist> artists;

  @JsonProperty("available_markets")
  private List<String> availableMarkets;

  @JsonProperty("disc_number")
  private int discNumber;

  @JsonProperty("duration_ms")
  private int durationMs;

  @JsonProperty("explicit")
  private boolean explicit;

  @JsonProperty("external_urls")
  private Map<String, String> externalUrls;

  @JsonProperty("href")
  private String href;

  @JsonProperty("id")
  private String id;

  @JsonProperty("is_playable")
  private boolean isPlayable;

  @JsonProperty("restrictions")
  private SpotifyRestriction restrictions;

  @JsonProperty("name")
  private String name;

  @JsonProperty("preview_url")
  private String previewUrl;

  @JsonProperty("track_number")
  private int trackNumber;

  @JsonProperty("type")
  private String type;

  @JsonProperty("uri")
  private String uri;

  @JsonProperty("is_local")
  private boolean isLocal;
}
