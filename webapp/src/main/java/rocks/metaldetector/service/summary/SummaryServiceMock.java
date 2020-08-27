package rocks.metaldetector.service.summary;

import org.springframework.stereotype.Service;
import rocks.metaldetector.butler.facade.dto.ReleaseDto;
import rocks.metaldetector.service.artist.ArtistDto;
import rocks.metaldetector.web.api.response.SummaryResponse;

import java.time.LocalDate;
import java.util.List;

@Service
public class SummaryServiceMock implements SummaryService {

  @Override
  public SummaryResponse createSummaryResponse() {
    LocalDate now = LocalDate.now();
    List<ReleaseDto> upcomingReleases = createUpcomingReleases(now);
    List<ReleaseDto> recentReleases = createRecentReleases(now);
    List<ArtistDto> recentlyFollowedArtists = createRecentlyFollowedArtists(now);

    return SummaryResponse.builder()
        .upcomingReleases(upcomingReleases)
        .recentReleases(recentReleases)
        .mostExpectedReleases(upcomingReleases)
        .recentlyFollowedArtists(recentlyFollowedArtists)
        .favoriteCommunityArtists(recentlyFollowedArtists)
        .build();
  }

  private List<ReleaseDto> createUpcomingReleases(LocalDate now) {
    ReleaseDto waldvolk = ReleaseDto.builder().albumTitle("Waldvolk").artist("XIV Dark Centuries").releaseDate(now.plusDays(11)).coverUrl("/images/dummy/xiv-dark-centuries_waldvolk.jpg").build();
    ReleaseDto transilvanianHunger = ReleaseDto.builder().albumTitle("Transilvanian Hunger").artist("Darkthrone").releaseDate(now.plusDays(21)).coverUrl("/images/dummy/darkthrone_transilvanian-hunger.jpg").build();
    ReleaseDto traktat = ReleaseDto.builder().albumTitle("Traktat").artist("Karg").releaseDate(now.plusDays(25)).coverUrl("/images/dummy/karg_traktat.jpg").build();
    return List.of(waldvolk, transilvanianHunger, traktat);
  }

  private List<ReleaseDto> createRecentReleases(LocalDate now) {
    ReleaseDto freierWilleFreierGeist = ReleaseDto.builder().albumTitle("Freier Wille, Freier Geist").artist("Thormesis").releaseDate(now.minusDays(2)).coverUrl("/images/dummy/thormesis_freier-wille-freier-geist.jpg").build();
    ReleaseDto sonsOfNorthernDarkness = ReleaseDto.builder().albumTitle("Sons of Northern Darkness").artist("Immortal").releaseDate(now.minusDays(13)).coverUrl("/images/dummy/immortal_sons-of_northern_darkness.jpg").build();
    ReleaseDto ritualeSatanum = ReleaseDto.builder().albumTitle("Rituale Satanum").artist("Behexen").releaseDate(now.minusDays(25)).coverUrl("/images/dummy/behexen_rituale-satanum.jpg").build();
    ReleaseDto ageOfExcuse = ReleaseDto.builder().albumTitle("Age of Excuse").artist("Mgla").releaseDate(now.minusDays(29)).coverUrl("/images/dummy/mgla_age-of-excuse.jpg").build();
    return List.of(freierWilleFreierGeist, sonsOfNorthernDarkness, ritualeSatanum, ageOfExcuse);
  }

  private List<ArtistDto> createRecentlyFollowedArtists(LocalDate now) {
    ArtistDto harakiriForTheSky = ArtistDto.builder().artistName("Harakiri For the Sky").thumb("/images/dummy/harakiri-for-the-sky.jpg").followedSince(now.minusDays(3)).build();
    ArtistDto marduk = ArtistDto.builder().artistName("Marduk").thumb("/images/dummy/marduk.jpg").followedSince(now.minusDays(7)).build();
    ArtistDto abbath = ArtistDto.builder().artistName("Abbath").thumb("/images/dummy/abbath.jpg").followedSince(now.minusDays(15)).build();
    ArtistDto alcest = ArtistDto.builder().artistName("Alcest").thumb("/images/dummy/alcest.jpg").followedSince(now.minusDays(26)).build();
    return List.of(harakiriForTheSky, marduk, abbath, alcest);
  }
}
