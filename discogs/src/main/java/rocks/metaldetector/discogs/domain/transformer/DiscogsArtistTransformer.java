package rocks.metaldetector.discogs.domain.transformer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import rocks.metaldetector.discogs.api.DiscogsArtist;
import rocks.metaldetector.discogs.fascade.dto.DiscogsArtistDto;

@Service
@Slf4j
public class DiscogsArtistTransformer {

  // ToDo DanielW: Tests
  public DiscogsArtistDto transform(DiscogsArtist artist) {
    log.warn("Hello World"); // ToDo DanielW: Gelangt das auch in das File Log?
    String imageUrl = artist.getImages() != null && artist.getImages().size() > 0 ? artist.getImages().get(0).getResourceUrl() : null;
    return DiscogsArtistDto.builder()
            .id(artist.getId())
            .name(artist.getName())
            .imageUrl(imageUrl)
            .build();
  }
}
