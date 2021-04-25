package rocks.metaldetector.service.artist.transformer;

import org.springframework.stereotype.Component;
import rocks.metaldetector.discogs.facade.dto.DiscogsArtistDto;
import rocks.metaldetector.persistence.domain.artist.ArtistEntity;
import rocks.metaldetector.spotify.facade.dto.SpotifyArtistDto;

import java.util.List;

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
            .externalUri(spotifyArtist.getUri())
            .artistName(spotifyArtist.getName())
            .genres(buildGenreString(spotifyArtist.getGenres()))
            .source(SPOTIFY)
            .spotifyPopularity(spotifyArtist.getPopularity())
            .spotifyFollower(spotifyArtist.getFollower())
            .imageXs(spotifyArtist.getImages().get(XS))
            .imageS(spotifyArtist.getImages().get(S))
            .imageM(spotifyArtist.getImages().get(M))
            .imageL(spotifyArtist.getImages().get(L))
            .build();
  }

  public ArtistEntity transformDiscogsArtistDto(DiscogsArtistDto discogsArtist) {
    return ArtistEntity.builder()
            .externalId(discogsArtist.getId())
            .externalUrl(discogsArtist.getUrl())
            .externalUri(discogsArtist.getUri())
            .artistName(discogsArtist.getName())
            .source(DISCOGS)
            .imageXs(discogsArtist.getImages().get(XS))
            .imageS(discogsArtist.getImages().get(S))
            .imageM(discogsArtist.getImages().get(M))
            .imageL(discogsArtist.getImages().get(L))
            .build();
  }

  private String buildGenreString(List<String> genres) {
    String genreString = String.join(", ", genres);
    return genreString.substring(0, Math.min(genreString.length(), 256));
  }
}
