package rocks.metaldetector.service.admin.dashboard;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import rocks.metaldetector.persistence.domain.artist.FollowActionEntity;
import rocks.metaldetector.persistence.domain.artist.FollowActionRepository;
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
public class StatisticsServiceImpl implements StatisticsService {

  private final StatisticsService statisticsServiceMock;
  private final UserRepository userRepository;
  private final FollowActionRepository followActionRepository;

  public StatisticsServiceImpl(@Qualifier("statisticsServiceMock") StatisticsService statisticsService,
                               UserRepository userRepository,
                               FollowActionRepository followActionRepository) {
    this.statisticsServiceMock = statisticsService;
    this.userRepository = userRepository;
    this.followActionRepository = followActionRepository;
  }

  @Override
  public StatisticsResponse createStatisticsResponse() {
    StatisticsResponse mockResponse = statisticsServiceMock.createStatisticsResponse();
    return StatisticsResponse.builder()
        .userInfo(buildUserInfo())
        .artistFollowingInfo(buildArtistFollowingInfo())
        .releaseInfo(mockResponse.getReleaseInfo())
        .importInfo(mockResponse.getImportInfo())
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
    List<FollowActionEntity> allFollowActions = followActionRepository.findAll();
    Map<YearMonth, Long> followActionsPerMonth = allFollowActions.stream()
        .collect(Collectors.groupingBy(followActionEntity -> YearMonth.of(followActionEntity.getCreatedDateTime().getYear(), followActionEntity.getCreatedDateTime().getMonth()),
                                       TreeMap::new,
                                       Collectors.counting()));
    return ArtistFollowingInfo.builder()
        .followingsPerMonth(followActionsPerMonth)
        .totalFollowings(allFollowActions.size())
        .followingsThisMonth(followActionsPerMonth.getOrDefault(YearMonth.now(), 0L))
        .build();
  }
}
