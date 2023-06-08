package rocks.metaldetector.service.admin.dashboard;

import org.springframework.stereotype.Service;
import rocks.metaldetector.web.api.response.AdminDashboardResponse;
import rocks.metaldetector.web.api.response.ArtistFollowingInfos;
import rocks.metaldetector.web.api.response.ImportInfos;
import rocks.metaldetector.web.api.response.ReleaseInfos;
import rocks.metaldetector.web.api.response.UserInfos;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

@Service
public class AdminDashboardServiceMock implements AdminDashboardService {

  @Override
  public AdminDashboardResponse createAdminDashboardResponse() {
    return AdminDashboardResponse.builder()
        .userInfos(buildUserInfos())
        .artistFollowingInfos(buildArtistFollowingInfos())
        .releaseInfos(buildReleaseInfos())
        .importInfos(buildImportInfos())
        .build();
  }

  private List<ImportInfos> buildImportInfos() {
    return List.of(
        ImportInfos.builder()
            .source("Metal Archives")
            .successRate(98)
            .lastImport(LocalDate.now().minusDays(1))
            .lastSuccessfulImport(LocalDate.now().minusDays(1))
            .build(),
        ImportInfos.builder()
            .source("Time for Metal")
            .successRate(66)
            .lastImport(LocalDate.now().minusDays(1))
            .lastSuccessfulImport(LocalDate.now().minusDays(2))
            .build()
    );
  }

  private ReleaseInfos buildReleaseInfos() {
    return ReleaseInfos.builder()
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

  private ArtistFollowingInfos buildArtistFollowingInfos() {
    return ArtistFollowingInfos.builder()
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

  private UserInfos buildUserInfos() {
    return UserInfos.builder()
        .totalUsers(666)
        .newThisMonth(6)
        .usersPerMonth(Map.of(YearMonth.of(2020, 1), 2,
                              YearMonth.of(2020, 2), 20,
                              YearMonth.of(2020, 3), 200,
                              YearMonth.of(2020, 4), 300,
                              YearMonth.of(2020, 5), 450,
                              YearMonth.of(2020, 6), 666))
        .build();
  }
}
