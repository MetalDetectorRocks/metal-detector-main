package rocks.metaldetector.spotify.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import rocks.metaldetector.spotify.api.search.SpotifyFollowers;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonPropertyOrder({
    "id",
    "name",
    "href",
    "popularity",
    "uri",
    "images",
    "genres",
    "external_urls",
    "followers"
})
@ToString(of = {"name"})
public class SpotifyArtist {

  @JsonProperty("id")
  private String id;

  @JsonProperty("name")
  private String name;

  @JsonProperty("href")
  private String href;

  @JsonProperty("popularity")
  private int popularity;

  @JsonProperty("uri")
  private String uri;

  @JsonProperty("images")
  private List<SpotifyImage> images;

  @JsonProperty("genres")
  private List<String> genres;

  @JsonProperty("external_urls")
  private Map<String, String> externalUrls;

  @JsonProperty("followers")
  private SpotifyFollowers followers;
}
