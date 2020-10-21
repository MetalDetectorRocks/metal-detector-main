package rocks.metaldetector.spotify.api.imports;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonPropertyOrder({
    "added_at",
    "album"
})
public class SpotifyAlbumImportResultItem {

  @JsonProperty("added_at")
  private LocalDateTime addedAt;

  @JsonProperty("album")
  private SpotifyAlbum album;
}
