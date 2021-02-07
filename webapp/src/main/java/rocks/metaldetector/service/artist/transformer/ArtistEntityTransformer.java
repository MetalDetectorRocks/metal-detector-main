package rocks.metaldetector.service.artist.transformer;

import org.springframework.stereotype.Component;
import rocks.metaldetector.discogs.facade.dto.DiscogsArtistDto;
import rocks.metaldetector.persistence.domain.artist.ArtistEntity;
import rocks.metaldetector.spotify.facade.dto.SpotifyArtistDto;

import static rocks.metaldetector.persistence.domain.artist.ArtistSource.DISCOGS;
import static rocks.metaldetector.persistence.domain.artist.ArtistSource.SPOTIFY;
import static rocks.metaldetector.support.ImageSize.L;
import static rocks.metaldetector.support.ImageSize.M;
import static rocks.metaldetector.support.ImageSize.S;
import static rocks.metaldetector.support.ImageSize.XS;

@Component
public class ArtistEntityTransformer {

  public ArtistEntity transformSpotifyArtistDto(SpotifyArtistDto spotifyArtist) {
    return ArtistEntity.builder()
            .externalId(spotifyArtist.getId())
            .externalUrl(spotifyArtist.getUrl())
            .artistName(spotifyArtist.getName())
            .genres(String.join(", ", spotifyArtist.getGenres()))
            .source(SPOTIFY)
            .spotifyPopularity(spotifyArtist.getPopularity())
            .spotifyFollower(spotifyArtist.getFollower())
            .imageXs(spotifyArtist.getImages().get(XS))
            .imageS(spotifyArtist.getImages().get(S))
            .imageM(spotifyArtist.getImages().get(M))
            .imageL(spotifyArtist.getImages().get(L))
            .build();
  }

  // ToDo DanielW: Welche Infos können wir noch von Discogs bekommen?
  public ArtistEntity transformDiscogsArtistDto(DiscogsArtistDto discogsArtist) {
    return ArtistEntity.builder()
            .externalId(discogsArtist.getId())
            .artistName(discogsArtist.getName())
            .source(DISCOGS)
            .imageL(discogsArtist.getImageUrl())
            .build();
  }
}
