package rocks.metaldetector.butler;

import rocks.metaldetector.butler.api.ButlerImportResponse;
import rocks.metaldetector.butler.api.ButlerRelease;
import rocks.metaldetector.butler.api.ButlerReleasesRequest;
import rocks.metaldetector.butler.api.ButlerReleasesResponse;
import rocks.metaldetector.butler.facade.dto.ImportResultDto;
import rocks.metaldetector.butler.facade.dto.ReleaseDto;

import java.time.LocalDate;
import java.util.Collections;
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

    public static ButlerReleasesResponse withEmptyResult() {
      return ButlerReleasesResponse.builder().releases(Collections.emptyList()).build();
    }
  }

  public static class ButlerImportResponseFactory {

    public static ButlerImportResponse createDefault() {
      return ButlerImportResponse.builder()
              .totalCountImported(666)
              .totalCountRequested(666)
              .build();
    }
  }

  public static class ImportResultDtoFactory {

    public static ImportResultDto createDefault() {
      return ImportResultDto.builder()
              .totalCountRequested(666)
              .totalCountImported(666)
              .build();
    }
  }

  public static class ButlerReleaseFactory {

    static ButlerRelease createDefault() {
      return ButlerRelease.builder()
              .artist("A")
              .albumTitle("Heavy Release")
              .releaseDate(LocalDate.now().plusDays(10))
              .build();
    }
  }

  public static class ReleaseDtoFactory {

    public static ReleaseDto createDefault() {
      return ReleaseDto.builder()
              .artist("A")
              .albumTitle("Heavy Release")
              .releaseDate(LocalDate.now().plusDays(10))
              .build();
    }
  }
}
