package rocks.metaldetector.discogs;

import rocks.metaldetector.discogs.api.DiscogsArtist;
import rocks.metaldetector.discogs.api.DiscogsArtistSearchResult;
import rocks.metaldetector.discogs.api.DiscogsArtistSearchResultContainer;
import rocks.metaldetector.discogs.api.DiscogsPagination;

import java.util.Collections;
import java.util.List;

public class DtoFactory {

  static class DiscogsArtistSearchResultFactory {

    static DiscogsArtistSearchResultContainer withOneResult() {
      return createDefaultResultContainer();
    }

    static DiscogsArtistSearchResultContainer withEmptyResult() {
      DiscogsArtistSearchResultContainer resultContainer = createDefaultResultContainer();
      resultContainer.setResults(Collections.emptyList());

      return resultContainer;
    }

    private static DiscogsArtistSearchResultContainer createDefaultResultContainer() {
      DiscogsPagination discogsPagination = new DiscogsPagination();
      DiscogsArtistSearchResult discogsArtistSearchResult = new DiscogsArtistSearchResult();

      DiscogsArtistSearchResultContainer resultContainer = new DiscogsArtistSearchResultContainer();
      resultContainer.setDiscogsPagination(discogsPagination);
      resultContainer.setResults(List.of(discogsArtistSearchResult));

      return resultContainer;
    }
  }

  static class DiscogsArtistFactory {

    static DiscogsArtist createTestArtist() {
      DiscogsArtist discogsArtist = new DiscogsArtist();
      discogsArtist.setId(252211L);
      discogsArtist.setName("Darkthrone");
      discogsArtist.setProfile("profile");
      return discogsArtist;
    }
  }
}
