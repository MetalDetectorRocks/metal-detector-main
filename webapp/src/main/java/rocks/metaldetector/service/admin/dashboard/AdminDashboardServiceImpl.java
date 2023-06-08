package rocks.metaldetector.service.admin.dashboard;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import rocks.metaldetector.persistence.domain.user.AbstractUserEntity;
import rocks.metaldetector.persistence.domain.user.UserRepository;
import rocks.metaldetector.web.api.response.AdminDashboardResponse;
import rocks.metaldetector.web.api.response.UserInfos;

import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
public class AdminDashboardServiceImpl implements AdminDashboardService {

  private final AdminDashboardService adminDashboardServiceMock;
  private final UserRepository userRepository;

  public AdminDashboardServiceImpl(@Qualifier("adminDashboardServiceMock") AdminDashboardService adminDashboardService,
                                   UserRepository userRepository) {
    this.adminDashboardServiceMock = adminDashboardService;
    this.userRepository = userRepository;
  }

  @Override
  public AdminDashboardResponse createAdminDashboardResponse() {
    AdminDashboardResponse mockResponse = adminDashboardServiceMock.createAdminDashboardResponse();
    return AdminDashboardResponse.builder()
        .userInfos(buildUserInfos())
        .artistFollowingInfos(mockResponse.getArtistFollowingInfos())
        .releaseInfos(mockResponse.getReleaseInfos())
        .importInfos(mockResponse.getImportInfos())
        .build();
  }

  private UserInfos buildUserInfos() {
    List<AbstractUserEntity> allUsers = userRepository.findAll();
    Map<YearMonth, Long> usersPerMonth = allUsers.stream()
        .collect(Collectors.groupingBy(user -> YearMonth.of(user.getCreatedDateTime().getYear(), user.getCreatedDateTime().getMonth()),
                                       TreeMap::new,
                                       Collectors.counting()));
    return UserInfos.builder()
        .usersPerMonth(usersPerMonth)
        .totalUsers(allUsers.size())
        .newThisMonth(usersPerMonth.getOrDefault(YearMonth.now(), 0L).intValue())
        .build();
  }
}
