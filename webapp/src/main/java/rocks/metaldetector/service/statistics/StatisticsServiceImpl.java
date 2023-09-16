package rocks.metaldetector.service.statistics;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import rocks.metaldetector.butler.facade.ButlerStatisticsService;
import rocks.metaldetector.butler.facade.dto.ButlerStatisticsDto;
import rocks.metaldetector.persistence.domain.artist.FollowActionRepository;
import rocks.metaldetector.persistence.domain.artist.FollowingsPerMonth;
import rocks.metaldetector.persistence.domain.user.AbstractUserEntity;
import rocks.metaldetector.persistence.domain.user.UserRepository;
import rocks.metaldetector.web.api.response.ArtistFollowingInfo;
import rocks.metaldetector.web.api.response.StatisticsResponse;
import rocks.metaldetector.web.api.response.UserInfo;

import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

  private final UserRepository userRepository;
  private final FollowActionRepository followActionRepository;
  private final ButlerStatisticsService butlerStatisticsService;

  @Override
  public StatisticsResponse createStatisticsResponse() {
    ButlerStatisticsDto butlerStatistics = butlerStatisticsService.getButlerStatistics();
    return StatisticsResponse.builder()
        .userInfo(buildUserInfo())
        .artistFollowingInfo(buildArtistFollowingInfo())
        .releaseInfo(butlerStatistics.getReleaseInfo())
        .importInfo(butlerStatistics.getImportInfo())
        .build();
  }

  private UserInfo buildUserInfo() {
    List<AbstractUserEntity> allUsers = userRepository.findAll();
    Map<YearMonth, Long> usersPerMonth = allUsers.stream()
        .collect(Collectors.groupingBy(user -> YearMonth.of(user.getCreatedDateTime().getYear(), user.getCreatedDateTime().getMonth()),
                                       TreeMap::new,
                                       Collectors.counting()));
    return UserInfo.builder()
        .usersPerMonth(usersPerMonth)
        .totalUsers(allUsers.size())
        .newThisMonth(usersPerMonth.getOrDefault(YearMonth.now(), 0L).intValue())
        .build();
  }

  private ArtistFollowingInfo buildArtistFollowingInfo() {
    Map<YearMonth, Long> followingsPerMonths = followActionRepository.groupFollowingsByYearAndMonth().stream()
        .collect(Collectors.toMap((FollowingsPerMonth followingsPerMonth) -> YearMonth.of(followingsPerMonth.getFollowingYear(), followingsPerMonth.getFollowingMonth()),
                                  FollowingsPerMonth::getFollowings,
                                  Long::sum,
                                  TreeMap::new));
    long totalFollowings = followingsPerMonths.values().stream().mapToLong((followings) -> followings).sum();
    return ArtistFollowingInfo.builder()
        .followingsPerMonth(followingsPerMonths)
        .totalFollowings(totalFollowings)
        .followingsThisMonth(followingsPerMonths.getOrDefault(YearMonth.now(), 0L))
        .build();
  }
}
