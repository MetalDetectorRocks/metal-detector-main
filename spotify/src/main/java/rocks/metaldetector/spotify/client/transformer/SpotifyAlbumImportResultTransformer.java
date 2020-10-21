package rocks.metaldetector.spotify.client.transformer;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import rocks.metaldetector.spotify.api.imports.SpotifyAlbumImportResultItem;
import rocks.metaldetector.spotify.facade.dto.SpotifyAlbumDto;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class SpotifyAlbumImportResultTransformer {

  private final SpotifyAlbumTransformer albumTransformer;

  public List<SpotifyAlbumDto> transform(List<SpotifyAlbumImportResultItem> items) {
    return items.stream()
        .map(SpotifyAlbumImportResultItem::getAlbum)
        .map(albumTransformer::transform)
        .collect(Collectors.toList());
  }
}
