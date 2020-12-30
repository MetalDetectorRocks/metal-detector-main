package rocks.metaldetector.spotify.api.imports;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonPropertyOrder({
    "artists"
})
public class SpotifyFollowedArtistsPageContainer {

  @JsonProperty("artists")
  private SpotifyFollowedArtistsPage artistsPage;
}
