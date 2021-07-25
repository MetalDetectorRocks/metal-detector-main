package rocks.metaldetector.spotify.client;

import rocks.metaldetector.spotify.api.SpotifyArtist;
import rocks.metaldetector.spotify.api.SpotifyImage;
import rocks.metaldetector.spotify.api.imports.SpotifyAlbum;
import rocks.metaldetector.spotify.api.search.SpotifyArtistSearchResult;
import rocks.metaldetector.spotify.api.search.SpotifyArtistSearchResultContainer;
import rocks.metaldetector.spotify.api.search.SpotifyFollowers;
import rocks.metaldetector.spotify.facade.dto.SpotifyArtistDto;
import rocks.metaldetector.spotify.facade.dto.SpotifyArtistSearchResultDto;
import rocks.metaldetector.support.Pagination;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static rocks.metaldetector.support.ImageSize.M;

public class SpotifyDtoFactory {

  public static class SpotifyArtistSearchResultContainerFactory {

    public static SpotifyArtistSearchResultContainer createDefault() {
      return SpotifyArtistSearchResultContainer.builder()
          .artists(SpotifyArtistSearchResultFactory.createDefault())
          .build();
    }

    public static SpotifyArtistSearchResultContainer withIndividualPagination(int offset, int limit, int total) {
      return SpotifyArtistSearchResultContainer.builder()
          .artists(SpotifyArtistSearchResultFactory.withIndividualPagination(offset, limit, total))
          .build();
    }
  }

  static class SpotifyArtistSearchResultFactory {

    static SpotifyArtistSearchResult createDefault() {
      return SpotifyArtistSearchResult.builder()
          .href("query")
          .limit(10)
          .next("nextPageLink")
          .previous("previousPageLink")
          .offset(10)
          .total(20)
          .items(List.of(
              SpotfiyArtistFactory.withArtistName("A"),
              SpotfiyArtistFactory.withArtistName("B")
          ))
          .build();
    }

    static SpotifyArtistSearchResult withIndividualPagination(int offset, int limit, int total) {
      return SpotifyArtistSearchResult.builder()
          .href("query")
          .limit(limit)
          .next("nextPageLink")
          .previous("previousPageLink")
          .offset(offset)
          .total(total)
          .items(Collections.emptyList())
          .build();
    }
  }

  public static class SpotfiyArtistFactory {

    public static SpotifyArtist withArtistName(String artistName) {
      return SpotifyArtist.builder()
          .externalUrls(Map.of("spotify", "http://example.com/" + artistName))
          .followers(SpotfiyFollowersFatory.createDefault())
          .genres(List.of("Black Metal"))
          .href("artistLink")
          .id("abc")
          .images(List.of(SpotfiyImageFatory.createDefault()))
          .name(artistName)
          .popularity(100)
          .uri("artistUri")
          .build();
    }
  }

  static class SpotfiyFollowersFatory {

    static SpotifyFollowers createDefault() {
      return SpotifyFollowers.builder()
          .total(666)
          .href("link")
          .build();
    }
  }

  static class SpotfiyImageFatory {

    static SpotifyImage createDefault() {
      return SpotifyImage.builder()
          .height(150)
          .width(150)
          .url("link")
          .build();
    }
  }

  public static class SpotifyArtistSearchResultDtoFactory {

    public static SpotifyArtistSearchResultDto createDefault() {
      return SpotifyArtistSearchResultDto.builder()
          .pagination(new Pagination(1, 1, 10))
          .searchResults(List.of(
              SpotifyArtistDtoFactory.withArtistName("A"),
              SpotifyArtistDtoFactory.withArtistName("B")
          ))
          .build();
    }
  }

  public static class SpotifyArtistDtoFactory {

    public static SpotifyArtistDto withArtistName(String artistName) {
      return SpotifyArtistDto.builder()
          .popularity(100)
          .genres(List.of("Black Metal"))
          .id("abcdef12345")
          .images(Map.of(M, "http://artist-image"))
          .name(artistName)
          .uri("uri")
          .build();
    }
  }

  public static class SpotifyAlbumFactory {

    public static SpotifyAlbum createDefault() {
      return SpotifyAlbum.builder()
          .id("albumId")
          .name("bestAlbumEver")
          .genres(List.of("genre"))
          .artists(new ArrayList<>(List.of(SpotfiyArtistFactory.withArtistName("artist"))))
          .popularity(666)
          .images(List.of(SpotfiyImageFatory.createDefault()))
          .uri("uri")
          .build();
    }

    public static SpotifyAlbum withName(String name) {
      return SpotifyAlbum.builder()
          .id("albumId")
          .name(name)
          .genres(List.of("genre"))
          .artists(new ArrayList<>(List.of(SpotfiyArtistFactory.withArtistName("artist"))))
          .popularity(666)
          .images(List.of(SpotfiyImageFatory.createDefault()))
          .uri("uri")
          .build();
    }
  }
}
