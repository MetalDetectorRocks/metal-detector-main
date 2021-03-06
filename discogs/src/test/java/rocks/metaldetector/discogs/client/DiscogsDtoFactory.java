package rocks.metaldetector.discogs.client;

import rocks.metaldetector.discogs.api.DiscogsArtist;
import rocks.metaldetector.discogs.api.DiscogsArtistSearchResult;
import rocks.metaldetector.discogs.api.DiscogsArtistSearchResultContainer;
import rocks.metaldetector.discogs.api.DiscogsImage;
import rocks.metaldetector.discogs.api.DiscogsPagination;
import rocks.metaldetector.discogs.facade.dto.DiscogsArtistDto;
import rocks.metaldetector.discogs.facade.dto.DiscogsArtistSearchResultDto;
import rocks.metaldetector.support.Pagination;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static rocks.metaldetector.support.ImageSize.M;

public class DiscogsDtoFactory {

  public static class DiscogsArtistSearchResultContainerFactory {

    public static DiscogsArtistSearchResultContainer createDefault() {
      return DiscogsArtistSearchResultContainer.builder()
              .pagination(DiscogsPaginationFactory.createDefault())
              .results(
                      List.of(DiscogsArtistSearchResultFactory.withArtistName("A"),
                              DiscogsArtistSearchResultFactory.withArtistName("AA"))
              )
              .build();
    }

    public static DiscogsArtistSearchResultContainer withArtistNames(List<String> artistNames) {
      return DiscogsArtistSearchResultContainer.builder()
          .pagination(DiscogsPaginationFactory.createDefault())
          .results(
              artistNames.stream().map(DiscogsArtistSearchResultFactory::withArtistName).collect(Collectors.toList())
          )
          .build();
    }

    static DiscogsArtistSearchResultContainer withEmptyResult() {
      return DiscogsArtistSearchResultContainer.builder()
              .pagination(new DiscogsPagination())
              .results(Collections.emptyList())
              .build();
    }
  }

  static class DiscogsPaginationFactory {

    static DiscogsPagination createDefault() {
      return DiscogsPagination.builder()
              .currentPage(1)
              .itemsPerPage(10)
              .itemsTotal(100)
              .pagesTotal(10)
              .build();
    }
  }

  static class DiscogsArtistSearchResultFactory {

    static DiscogsArtistSearchResult withArtistName(String artistName) {
        return DiscogsArtistSearchResult.builder()
                .id(1)
                .title(artistName)
                .resourceUrl("http://example.com/resource")
                .thumb("http://example.com/image-thumb")
                .uri("/" + artistName)
                .build();
    }
  }

  public static class DiscogsArtistFactory {

    public static DiscogsArtist createDefault() {
      return DiscogsArtist.builder()
              .id(252211L)
              .uri("http://example.com/Darkthrone")
              .resourceUrl("http://example.com/api/v1/1234")
              .name("Darkthrone")
              .profile("profile-description")
              .build();
    }

  }

  public static class DiscogsImageFactory {

    public static DiscogsImage createDefault(String name) {
      return DiscogsImage.builder()
              .height(100)
              .width(100)
              .type("DUMMY")
              .resourceUrl("http://example.com/" + name)
              .uri(name)
              .uri150(name + "-150")
              .build();
    }
  }

  public static class DiscogsArtistSearchResultDtoFactory {

    public static DiscogsArtistSearchResultDto createDefault() {
      return DiscogsArtistSearchResultDto.builder()
              .searchResults(Collections.emptyList())
              .pagination(new Pagination())
              .build();
    }
  }

  public static class DiscogsArtistDtoFactory {

    public static DiscogsArtistDto createDefault() {
      return DiscogsArtistDto.builder()
              .id("666")
              .url("http://example.com/Darkthrone")
              .uri("http://example.com/api/v1/1234")
              .name("Dummy artist")
              .images(Map.of(M, "http://example.com/image-m.jpg"))
              .build();
    }
  }
}
