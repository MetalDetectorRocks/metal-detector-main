package rocks.metaldetector.spotify.api.search;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonPropertyOrder({
    "artists"
})
public class SpotifyArtistSearchResultContainer {

  private SpotifyArtistSearchResult artists;

}
