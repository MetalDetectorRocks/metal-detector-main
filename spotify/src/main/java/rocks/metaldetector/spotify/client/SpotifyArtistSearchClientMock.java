package rocks.metaldetector.spotify.client;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import rocks.metaldetector.spotify.api.SpotifyArtist;
import rocks.metaldetector.spotify.api.SpotifyImage;
import rocks.metaldetector.spotify.api.search.SpotifyArtistSearchResult;
import rocks.metaldetector.spotify.api.search.SpotifyArtistSearchResultContainer;
import rocks.metaldetector.spotify.api.search.SpotifyArtistsContainer;
import rocks.metaldetector.spotify.api.search.SpotifyFollowers;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@Profile("mockmode")
@AllArgsConstructor
public class SpotifyArtistSearchClientMock implements SpotifyArtistSearchClient {

  @Override
  public SpotifyArtistSearchResultContainer searchByName(String authorizationToken, String artistQueryString, int pageNumber, int pageSize) {
    return SpotifyArtistSearchResultContainer.builder()
        .artists(SpotifyArtistSearchResult.builder()
                     .href("https://api.spotify.com/v1/search?query=Opeth&type=artist&offset=0&limit=20")
                     .items(List.of(createOpeth()))
                     .limit(20)
                     .next(null)
                     .previous(null)
                     .offset(0)
                     .total(1)
                     .build())
        .build();
  }

  @Override
  public SpotifyArtist searchById(String authenticationToken, String artistId) {
    return createOpeth();
  }

  @Override
  public SpotifyArtistsContainer searchByIds(String authenticationToken, List<String> artistIds) {
    return SpotifyArtistsContainer.builder()
        .artists(List.of(createOpeth(), createDarkthrone()))
        .build();
  }

  private SpotifyArtist createOpeth() {
    return SpotifyArtist.builder()
        .id("0ybFZ2Ab08V8hueghSXm6E")
        .href("https://api.spotify.com/v1/artists/0ybFZ2Ab08V8hueghSXm6E")
        .popularity(61)
        .uri("spotify:artist:0ybFZ2Ab08V8hueghSXm6E")
        .genres(List.of("progressive metal", "alternative metal"))
        .externalUrls(Map.of("spotify", "https://open.spotify.com/artist/0ybFZ2Ab08V8hueghSXm6E"))
        .images(List.of(SpotifyImage.builder()
                            .url("https://i.scdn.co/image/f03a78ab9215535590b5634dd9e75fb057782f02")
                            .height(640)
                            .width(640)
                            .build()))
        .name("Opeth")
        .followers(SpotifyFollowers.builder().total(100).build())
        .build();
  }

  private SpotifyArtist createDarkthrone() {
    return SpotifyArtist.builder()
        .id("7kWnE981vITXDnAD2cZmCV")
        .href("https://api.spotify.com/v1/artists/7kWnE981vITXDnAD2cZmCV")
        .popularity(48)
        .uri("spotify:artist:7kWnE981vITXDnAD2cZmCV")
        .genres(List.of("black metal", "black 'n' roll"))
        .externalUrls(Map.of("spotify", "https://open.spotify.com/artist/7kWnE981vITXDnAD2cZmCV"))
        .images(List.of(SpotifyImage.builder()
                            .url("https://i.scdn.co/image/c1f48304cf42a409bb85741afe51620ca4f6bd08")
                            .height(640)
                            .width(640)
                            .build()))
        .name("Darkthrone")
        .followers(SpotifyFollowers.builder().total(100).build())
        .build();
  }
}
