package rocks.metaldetector.spotify.client;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import rocks.metaldetector.spotify.api.search.SpotifyArtist;
import rocks.metaldetector.spotify.api.search.SpotifyArtistSearchResult;
import rocks.metaldetector.spotify.api.search.SpotifyArtistSearchResultContainer;
import rocks.metaldetector.spotify.api.search.SpotifyImage;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@Profile("mockmode")
@AllArgsConstructor
public class SpotifyArtistSearchClientMock implements SpotifyArtistSearchClient {

  @Override
  public SpotifyArtistSearchResultContainer searchByName(String authenticationToken, String artistQueryString, int pageNumber, int pageSize) {
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
        .name("Opteh")
        .build();
  }
}
