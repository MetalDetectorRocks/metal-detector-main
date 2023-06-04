package rocks.metaldetector.service.admin.dashboard;

import org.springframework.stereotype.Service;
import rocks.metaldetector.web.api.response.AdminDashboardResponse;
import rocks.metaldetector.web.api.response.ArtistFollowingInfos;
import rocks.metaldetector.web.api.response.ImportInfos;
import rocks.metaldetector.web.api.response.ReleaseInfos;
import rocks.metaldetector.web.api.response.UserInfos;

import java.time.LocalDate;
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
        .releasesPerMonth(Map.of("2020-01", 2000L,
                                 "2020-02", 3000L,
                                 "2020-03", 6400L,
                                 "2020-04", 3400L,
                                 "2020-05", 4500L,
                                 "2020-06", 6666L))
        .totalReleases(66666)
        .duplicates(66)
        .releasesThisMonth(666)
        .upcomingReleases(66)
        .build();
  }

  private ArtistFollowingInfos buildArtistFollowingInfos() {
    return ArtistFollowingInfos.builder()
        .followingsPerMonth(Map.of("2020-01", 200L,
                                   "2020-02", 4000L,
                                   "2020-03", 3300L,
                                   "2020-04", 4100L,
                                   "2020-05", 5000L,
                                   "2020-06", 6666L))
        .totalFollowings(6666)
        .followingsThisMonth(666)
        .build();
  }

  private UserInfos buildUserInfos() {
    return UserInfos.builder()
        .totalUsers(666)
        .newThisMonth(6)
        .usersPerMonth(Map.of("2020-01", 2L,
                              "2020-02", 20L,
                              "2020-03", 200L,
                              "2020-04", 300L,
                              "2020-05", 450L,
                              "2020-06", 666L))
        .build();
  }
}
