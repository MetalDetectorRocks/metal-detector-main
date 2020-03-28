package rocks.metaldetector.discogs.client;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import rocks.metaldetector.discogs.api.DiscogsArtist;
import rocks.metaldetector.discogs.api.DiscogsArtistSearchResult;
import rocks.metaldetector.discogs.api.DiscogsArtistSearchResultContainer;
import rocks.metaldetector.discogs.api.DiscogsImage;
import rocks.metaldetector.discogs.api.DiscogsPagination;

import java.util.List;

@Service
@Profile("mockmode")
public class DiscogsArtistSearchRestClientMock implements DiscogsArtistSearchRestClient {

  static final long METALLICA_ID = 18839;
  static final long SLAYER_ID = 18845;

  private static final String METALLICA_IMAGE_URL = "https://img.discogs.com/jJVZAoJsAzrXr3qWDEWfXkM6GOQ=/fit-in/300x300/filters:strip_icc():format(jpeg):mode_rgb():quality(40)/discogs-images/A-18839-1577045241-6198.jpeg.jpg";
  private static final String SLAYER_IMAGE_URL = "https://img.discogs.com/V6DQdkTXpXe-v5X3Inzrj-rMPMk=/fit-in/300x300/filters:strip_icc():format(jpeg):mode_rgb():quality(40)/discogs-images/A-18845-1546167009-4213.jpeg.jpg";

  @Override
  public DiscogsArtistSearchResultContainer searchByName(String artistQueryString, int pageNumber, int pageSize) {
    return DiscogsArtistSearchResultContainer.builder()
            .results(List.of(createMetallica(), createSlayer()))
            .pagination(DiscogsPagination.builder().currentPage(1).itemsPerPage(10).pagesTotal(1).build())
            .build();
  }

  @Override
  public DiscogsArtist searchById(long artistId) {
    if (artistId == METALLICA_ID) {
      return DiscogsArtist.builder()
              .id(METALLICA_ID)
              .name("Metallica")
              .images(List.of(DiscogsImage.builder().resourceUrl(METALLICA_IMAGE_URL).build()))
              .build();
    }
    else if (artistId == SLAYER_ID) {
      return DiscogsArtist.builder()
              .id(SLAYER_ID)
              .name("Slayer")
              .images(List.of(DiscogsImage.builder().resourceUrl(SLAYER_IMAGE_URL).build()))
              .build();
    }
    else {
      throw new RuntimeException("should not happen");
    }
  }

  private DiscogsArtistSearchResult createMetallica() {
    return DiscogsArtistSearchResult.builder()
            .id(METALLICA_ID)
            .title("Metallica")
            .resourceUrl("https://www.discogs.com/de/artist/18839-Metallica")
            .thumb(METALLICA_IMAGE_URL)
            .build();
  }

  private DiscogsArtistSearchResult createSlayer() {
    return DiscogsArtistSearchResult.builder()
            .id(SLAYER_ID)
            .title("Slayer")
            .resourceUrl("https://www.discogs.com/de/artist/18845-Slayer")
            .thumb(SLAYER_IMAGE_URL)
            .build();
  }
}
