package rocks.metaldetector.service.admin.dashboard;

import org.springframework.stereotype.Service;
import rocks.metaldetector.web.api.response.ArtistFollowingInfo;
import rocks.metaldetector.web.api.response.ImportInfo;
import rocks.metaldetector.web.api.response.ReleaseInfo;
import rocks.metaldetector.web.api.response.StatisticsResponse;
import rocks.metaldetector.web.api.response.UserInfo;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

@Service
public class StatisticsServiceMock implements StatisticsService {

  @Override
  public StatisticsResponse createStatisticsResponse() {
    return StatisticsResponse.builder()
        .userInfo(buildUserInfo())
        .artistFollowingInfo(buildArtistFollowingInfo())
        .releaseInfo(buildReleaseInfo())
        .importInfo(buildImportInfo())
        .build();
  }

  private List<ImportInfo> buildImportInfo() {
    return List.of(
        ImportInfo.builder()
            .source("Metal Archives")
            .successRate(98)
            .lastImport(LocalDate.now().minusDays(1))
            .lastSuccessfulImport(LocalDate.now().minusDays(1))
            .build(),
        ImportInfo.builder()
            .source("Time for Metal")
            .successRate(66)
            .lastImport(LocalDate.now().minusDays(1))
            .lastSuccessfulImport(LocalDate.now().minusDays(2))
            .build()
    );
  }

  private ReleaseInfo buildReleaseInfo() {
    return ReleaseInfo.builder()
        .releasesPerMonth(Map.of(YearMonth.of(2020, 1), 2000,
                                 YearMonth.of(2020, 2), 3000,
                                 YearMonth.of(2020, 3), 6400,
                                 YearMonth.of(2020, 4), 3400,
                                 YearMonth.of(2020, 5), 4500,
                                 YearMonth.of(2020, 6), 6666))
        .totalReleases(66666)
        .duplicates(66)
        .releasesThisMonth(666)
        .upcomingReleases(66)
        .build();
  }

  private ArtistFollowingInfo buildArtistFollowingInfo() {
    return ArtistFollowingInfo.builder()
        .followingsPerMonth(Map.of(YearMonth.of(2020, 1), 200L,
                                   YearMonth.of(2020, 2), 4000L,
                                   YearMonth.of(2020, 3), 3300L,
                                   YearMonth.of(2020, 4), 4100L,
                                   YearMonth.of(2020, 5), 5000L,
                                   YearMonth.of(2020, 6), 6666L))
        .totalFollowings(6666)
        .followingsThisMonth(666)
        .build();
  }

  private UserInfo buildUserInfo() {
    return UserInfo.builder()
        .totalUsers(666)
        .newThisMonth(6)
        .usersPerMonth(Map.of(YearMonth.of(2020, 1), 2L,
                              YearMonth.of(2020, 2), 20L,
                              YearMonth.of(2020, 3), 200L,
                              YearMonth.of(2020, 4), 300L,
                              YearMonth.of(2020, 5), 450L,
                              YearMonth.of(2020, 6), 666L))
        .build();
  }
}
