package rocks.metaldetector.service.summary;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import rocks.metaldetector.butler.facade.ReleaseService;
import rocks.metaldetector.butler.facade.dto.ReleaseDto;
import rocks.metaldetector.service.artist.ArtistDto;
import rocks.metaldetector.support.Page;
import rocks.metaldetector.support.PageRequest;
import rocks.metaldetector.support.Sorting;
import rocks.metaldetector.support.TimeRange;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static rocks.metaldetector.service.summary.SummaryServiceImpl.RESULT_LIMIT;
import static rocks.metaldetector.service.summary.SummaryServiceImpl.TIME_RANGE_MONTHS;
import static rocks.metaldetector.support.Sorting.Direction.ASC;
import static rocks.metaldetector.support.Sorting.Direction.DESC;

@Component
@AllArgsConstructor
public class ReleaseCollector {

  private final ReleaseService releaseService;

  public List<ReleaseDto> collectUpcomingReleases(List<ArtistDto> artists) {
    LocalDate now = LocalDate.now();
    TimeRange timeRange = new TimeRange(now, now.plusMonths(TIME_RANGE_MONTHS));
    Sorting sorting = new Sorting(ASC, List.of("releaseDate", "artist", "albumTitle"));
    return collectReleases(artists, timeRange, sorting);
  }

  public List<ReleaseDto> collectRecentReleases(List<ArtistDto> artists) {
    LocalDate now = LocalDate.now();
    TimeRange timeRange = new TimeRange(now.minusMonths(TIME_RANGE_MONTHS), now);
    Sorting sorting = createDescendingSorting();
    return collectReleases(artists, timeRange, sorting);
  }

  private List<ReleaseDto> collectReleases(List<ArtistDto> artists, TimeRange timeRange, Sorting sorting) {
    if (artists.isEmpty()) {
      return Collections.emptyList();
    }

    List<String> artistNames = artists.stream().map(ArtistDto::getArtistName).collect(Collectors.toList());
    PageRequest pageRequest = new PageRequest(1, RESULT_LIMIT, sorting);
    Page<ReleaseDto> releasePage = releaseService.findReleases(artistNames, timeRange, pageRequest);
    return releasePage.getItems();
  }

  private Sorting createDescendingSorting() {
    List<Sorting.Order> sortingOrders = List.of(new Sorting.Order(DESC, "releaseDate"),
                                                new Sorting.Order(ASC, "artist"),
                                                new Sorting.Order(ASC, "albumTitle"));
    return new Sorting(sortingOrders);
  }
}
