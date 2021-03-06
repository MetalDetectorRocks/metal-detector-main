package rocks.metaldetector.service.artist.transformer;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static rocks.metaldetector.persistence.domain.artist.ArtistSource.DISCOGS;
import static rocks.metaldetector.persistence.domain.artist.ArtistSource.SPOTIFY;
import static rocks.metaldetector.support.ImageSize.L;
import static rocks.metaldetector.support.ImageSize.M;
import static rocks.metaldetector.support.ImageSize.S;
import static rocks.metaldetector.support.ImageSize.XS;
import static rocks.metaldetector.testutil.DtoFactory.DiscogsArtistDtoFactory;
import static rocks.metaldetector.testutil.DtoFactory.SpotifyArtistDtoFactory;

class ArtistEntityTransformerTest implements WithAssertions {

  private final ArtistEntityTransformer underTest = new ArtistEntityTransformer();

  @Test
  @DisplayName("Should transform SpotifyArtistDto")
  void should_transform_spotify_artist() {
    // given
    var spotifyArtist = SpotifyArtistDtoFactory.createDefault();

    // when
    var artistEntity = underTest.transformSpotifyArtistDto(spotifyArtist);

    // then
    assertThat(artistEntity.getExternalId()).isEqualTo(spotifyArtist.getId());
    assertThat(artistEntity.getExternalUrl()).isEqualTo(spotifyArtist.getUrl());
    assertThat(artistEntity.getExternalUri()).isEqualTo(spotifyArtist.getUri());
    assertThat(artistEntity.getArtistName()).isEqualTo(spotifyArtist.getName());
    assertThat(artistEntity.getGenres()).isEqualTo(String.join(", ", spotifyArtist.getGenres()));
    assertThat(artistEntity.getSource()).isEqualTo(SPOTIFY);
    assertThat(artistEntity.getSpotifyPopularity()).isEqualTo(spotifyArtist.getPopularity());
    assertThat(artistEntity.getSpotifyFollower()).isEqualTo(spotifyArtist.getFollower());
    assertThat(artistEntity.getImageXs()).isEqualTo(spotifyArtist.getImages().get(XS));
    assertThat(artistEntity.getImageS()).isEqualTo(spotifyArtist.getImages().get(S));
    assertThat(artistEntity.getImageM()).isEqualTo(spotifyArtist.getImages().get(M));
    assertThat(artistEntity.getImageL()).isEqualTo(spotifyArtist.getImages().get(L));
  }

  @Test
  @DisplayName("Should transform DiscogsArtistDto")
  void should_transform_discogs_artist() {
    // given
    var discogsArtist = DiscogsArtistDtoFactory.createDefault();

    // when
    var artistEntity = underTest.transformDiscogsArtistDto(discogsArtist);

    // then
    assertThat(artistEntity.getExternalId()).isEqualTo(discogsArtist.getId());
    assertThat(artistEntity.getExternalUrl()).isEqualTo(discogsArtist.getUrl());
    assertThat(artistEntity.getExternalUri()).isEqualTo(discogsArtist.getUri());
    assertThat(artistEntity.getArtistName()).isEqualTo(discogsArtist.getName());
    assertThat(artistEntity.getSource()).isEqualTo(DISCOGS);
    assertThat(artistEntity.getImageXs()).isEqualTo(discogsArtist.getImages().get(XS));
    assertThat(artistEntity.getImageS()).isEqualTo(discogsArtist.getImages().get(S));
    assertThat(artistEntity.getImageM()).isEqualTo(discogsArtist.getImages().get(M));
    assertThat(artistEntity.getImageL()).isEqualTo(discogsArtist.getImages().get(L));
  }
}
