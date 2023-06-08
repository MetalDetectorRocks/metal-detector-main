package rocks.metaldetector.service.admin.dashboard;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.metaldetector.persistence.domain.user.UserRepository;
import rocks.metaldetector.service.user.UserEntityFactory;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AdminDashboardServiceImplTest implements WithAssertions {

  UserRepository userRepository = mock(UserRepository.class);

  AdminDashboardService adminDashboardServiceMock = new AdminDashboardServiceMock();

  AdminDashboardServiceImpl underTest = new AdminDashboardServiceImpl(adminDashboardServiceMock, userRepository);

  @AfterEach
  void tearDown() {
    reset(userRepository);
  }

  @Test
  @DisplayName("mockResponse is returned for ArtistFollowingInfos")
  void test_mock_artist_following_infos_returned() {
    var mockResponse = adminDashboardServiceMock.createAdminDashboardResponse();

    // when
    var result = underTest.createAdminDashboardResponse();

    // then
    assertThat(result.getArtistFollowingInfos()).isEqualTo(mockResponse.getArtistFollowingInfos());
  }

  @Test
  @DisplayName("mockResponse is returned for ReleaseInfos")
  void test_mock_release_infos_returned() {
    var mockResponse = adminDashboardServiceMock.createAdminDashboardResponse();

    // when
    var result = underTest.createAdminDashboardResponse();

    // then
    assertThat(result.getReleaseInfos()).isEqualTo(mockResponse.getReleaseInfos());
  }

  @Test
  @DisplayName("mockResponse is returned for ImportInfos")
  void test_mock_import_infos_returned() {
    var mockResponse = adminDashboardServiceMock.createAdminDashboardResponse();

    // when
    var result = underTest.createAdminDashboardResponse();

    // then
    assertThat(result.getImportInfos()).isEqualTo(mockResponse.getImportInfos());
  }

  @Nested
  @DisplayName("Tests for UserInfos")
  class UserInfosTest {

    @Test
    @DisplayName("userRepository is called")
    void test_user_repository_called() {
      // when
      underTest.createAdminDashboardResponse();

      // then
      verify(userRepository).findAll();
    }

    @Test
    @DisplayName("sorted map with created users per month is returned")
    void test_sorted_users_per_month_returned() {
      //given
      var user1 = UserEntityFactory.createDefaultUser();
      var user2 = UserEntityFactory.createDefaultUser();
      var user3 = UserEntityFactory.createDefaultUser();
      var user4 = UserEntityFactory.createDefaultUser();
      var user5 = UserEntityFactory.createDefaultUser();
      var localDate1 = LocalDate.of(2020, 1, 1);
      var localDate2 = LocalDate.of(2021, 1, 1);
      var localDate3 = LocalDate.of(2022, 1, 1);
      user1.setCreatedDateTime(Date.from(localDate1.atStartOfDay(ZoneId.systemDefault()).toInstant()));
      user2.setCreatedDateTime(Date.from(localDate2.atStartOfDay(ZoneId.systemDefault()).toInstant()));
      user3.setCreatedDateTime(Date.from(localDate3.atStartOfDay(ZoneId.systemDefault()).toInstant()));
      user4.setCreatedDateTime(Date.from(localDate1.atStartOfDay(ZoneId.systemDefault()).toInstant()));
      user5.setCreatedDateTime(Date.from(localDate2.atStartOfDay(ZoneId.systemDefault()).toInstant()));
      var users = List.of(user5, user4, user3, user2, user1);
      doReturn(users).when(userRepository).findAll();

      // when
      var result = underTest.createAdminDashboardResponse();

      // then
      var usersPerMonth = result.getUserInfos().getUsersPerMonth();
      assertThat(usersPerMonth.size()).isEqualTo(3);
      assertThat(usersPerMonth).containsExactly(
          Map.entry(YearMonth.of(2020, 1), 2L),
          Map.entry(YearMonth.of(2021, 1), 2L),
          Map.entry(YearMonth.of(2022, 1), 1L)
      );
    }

    @Test
    @DisplayName("total number of users is returned")
    void test_total_number_of_users_returned() {
      //given
      var user = UserEntityFactory.createDefaultUser();
      var localDate = LocalDate.now();
      user.setCreatedDateTime(Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
      var users = List.of(user, user, user, user, user);
      doReturn(users).when(userRepository).findAll();

      // when
      var result = underTest.createAdminDashboardResponse();

      // then
      assertThat(result.getUserInfos().getTotalUsers()).isEqualTo(5);
    }

    @Test
    @DisplayName("new users this month is returned")
    void test_new_users_this_month_returned() {
      //given
      var oldUser = UserEntityFactory.createDefaultUser();
      var newUser1 = UserEntityFactory.createDefaultUser();
      var newUser2 = UserEntityFactory.createDefaultUser();
      var localDate = LocalDate.now();
      oldUser.setCreatedDateTime(Date.from(localDate.minusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
      newUser1.setCreatedDateTime(Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
      newUser2.setCreatedDateTime(Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
      var users = List.of(oldUser, newUser1, newUser2);
      doReturn(users).when(userRepository).findAll();

      // when
      var result = underTest.createAdminDashboardResponse();

      // then
      assertThat(result.getUserInfos().getNewThisMonth()).isEqualTo(2);
    }
  }
}
