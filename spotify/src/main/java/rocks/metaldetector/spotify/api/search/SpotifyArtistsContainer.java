package rocks.metaldetector.spotify.api.search;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import rocks.metaldetector.spotify.api.SpotifyArtist;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonPropertyOrder({
    "artists"
})
public class SpotifyArtistsContainer {

  @JsonProperty("artists")
  private List<SpotifyArtist> artists;
}
