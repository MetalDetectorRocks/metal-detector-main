package rocks.metaldetector.discogs.client.transformer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import rocks.metaldetector.discogs.api.DiscogsArtist;
import rocks.metaldetector.discogs.facade.dto.DiscogsArtistDto;

@Service
@Slf4j
public class DiscogsArtistTransformer {

  public DiscogsArtistDto transform(DiscogsArtist artist) {
    String imageUrl = artist.getImages() != null && artist.getImages().size() > 0 ? artist.getImages().get(0).getResourceUrl() : "";
    return DiscogsArtistDto.builder()
            .id(String.valueOf(artist.getId()))
            .name(artist.getName())
            .imageUrl(imageUrl)
            .build();
  }
}
