package rocks.metaldetector.discogs.client.transformer;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import rocks.metaldetector.discogs.api.DiscogsArtist;
import rocks.metaldetector.discogs.api.DiscogsImage;
import rocks.metaldetector.discogs.facade.dto.DiscogsArtistDto;
import rocks.metaldetector.support.ImageSize;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static rocks.metaldetector.support.ImageSize.XS;
import static rocks.metaldetector.support.ImageSize.ofHeight;

@Service
@AllArgsConstructor
public class DiscogsArtistTransformer {

  private final DiscogsArtistNameTransformer artistNameTransformer;

  public DiscogsArtistDto transform(DiscogsArtist artist) {
    return DiscogsArtistDto.builder()
            .id(String.valueOf(artist.getId()))
            .url(artist.getUri())
            .uri(artist.getResourceUrl())
            .name(artistNameTransformer.transformArtistName(artist.getName()))
            .images(transformImages(artist.getImages()))
            .build();
  }

  private Map<ImageSize, String> transformImages(List<DiscogsImage> discogsImages) {
    Map<ImageSize, String> images = new HashMap<>();
    if (discogsImages != null && discogsImages.size() > 0) {
      DiscogsImage primaryImage = discogsImages.get(0);
      images.put(ofHeight(primaryImage.getHeight()), primaryImage.getResourceUrl());
      images.putIfAbsent(XS, primaryImage.getUri150());
    }

    return images;
  }
}
