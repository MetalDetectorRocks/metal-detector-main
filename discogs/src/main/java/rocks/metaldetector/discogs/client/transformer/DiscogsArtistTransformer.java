package rocks.metaldetector.discogs.client.transformer;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import rocks.metaldetector.discogs.api.DiscogsArtist;
import rocks.metaldetector.discogs.facade.dto.DiscogsArtistDto;

@Service
@AllArgsConstructor
public class DiscogsArtistTransformer {

  private final DiscogsArtistNameTransformer artistNameTransformer;

  public DiscogsArtistDto transform(DiscogsArtist artist) {
    String imageUrl = artist.getImages() != null && artist.getImages().size() > 0 ? artist.getImages().get(0).getResourceUrl() : "";
    return DiscogsArtistDto.builder()
            .id(String.valueOf(artist.getId()))
            .name(artistNameTransformer.transformArtistName(artist.getName()))
            .imageUrl(imageUrl)
            .build();
  }
}
