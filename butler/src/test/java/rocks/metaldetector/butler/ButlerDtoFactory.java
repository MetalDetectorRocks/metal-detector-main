package rocks.metaldetector.butler;

import rocks.metaldetector.butler.api.ButlerImportJobResponse;
import rocks.metaldetector.butler.api.ButlerRelease;
import rocks.metaldetector.butler.api.ButlerReleasesRequest;
import rocks.metaldetector.butler.api.ButlerReleasesResponse;
import rocks.metaldetector.butler.facade.dto.ImportJobResultDto;
import rocks.metaldetector.butler.facade.dto.ReleaseDto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class ButlerDtoFactory {

  public static class ButlerReleaseRequestFactory {

    public static ButlerReleasesRequest createDefault() {
      return ButlerReleasesRequest.builder()
              .artists(List.of("A", "B", "C"))
              .dateFrom(LocalDate.of(2020, 1, 1))
              .dateTo(LocalDate.of(2020, 12, 31))
              .build();
    }
  }

  public static class ButlerReleasesResponseFactory {

    public static ButlerReleasesResponse createDefault() {
      return ButlerReleasesResponse.builder()
              .releases(List.of(ButlerReleaseFactory.createDefault()))
              .build();
    }
  }

  public static class ButlerImportJobResponseFactory {

    public static ButlerImportJobResponse createDefault() {
      return ButlerImportJobResponse.builder()
              .totalCountImported(666)
              .totalCountRequested(666)
              .startTime(LocalDateTime.of(2020, 7, 1, 13, 37, 5))
              .endTime(LocalDateTime.of(2020, 7, 1, 13, 39, 19))
              .build();
    }
  }

  public static class ImportJobResultDtoFactory {

    public static ImportJobResultDto createDefault() {
      return ImportJobResultDto.builder()
              .totalCountRequested(666)
              .totalCountImported(666)
              .build();
    }
  }

  public static class ButlerReleaseFactory {

    public static ButlerRelease createDefault() {
      return ButlerRelease.builder()
              .artist("A")
              .albumTitle("Heavy Release")
              .releaseDate(LocalDate.now().plusDays(10))
              .genre("G")
              .type("demo")
              .metalArchivesArtistUrl("url")
              .metalArchivesAlbumUrl("url")
              .source("encyclopaedia MetallUm: THE Metal archives")
              .state("not set")
              .build();
    }
  }

  public static class ReleaseDtoFactory {

    public static ReleaseDto createDefault() {
      return ReleaseDto.builder()
              .artist("A")
              .albumTitle("Heavy Release")
              .releaseDate(LocalDate.now().plusDays(10))
              .genre("G")
              .type("Demo")
              .metalArchivesArtistUrl("url")
              .metalArchivesAlbumUrl("url")
              .source("Encyclopaedia Metallum: The Metal Archives")
              .state("Ok")
              .build();
    }
  }
}
