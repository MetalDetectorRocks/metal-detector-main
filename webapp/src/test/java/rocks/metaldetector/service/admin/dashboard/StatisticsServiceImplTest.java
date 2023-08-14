package rocks.metaldetector.service.admin.dashboard;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.metaldetector.persistence.domain.artist.FollowActionEntity;
import rocks.metaldetector.persistence.domain.artist.FollowActionRepository;
import rocks.metaldetector.persistence.domain.user.UserRepository;
import rocks.metaldetector.service.artist.ArtistEntityFactory;
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
class StatisticsServiceImplTest implements WithAssertions {

  UserRepository userRepository = mock(UserRepository.class);
  FollowActionRepository followActionRepository = mock(FollowActionRepository.class);

  StatisticsService statisticsServiceMock = new StatisticsServiceMock();

  StatisticsServiceImpl underTest = new StatisticsServiceImpl(statisticsServiceMock, userRepository, followActionRepository);

  @AfterEach
  void tearDown() {
    reset(userRepository, followActionRepository);
  }

  @Test
  @DisplayName("mockResponse is returned for ReleaseInfo")
  void test_mock_release_info_returned() {
    var mockResponse = statisticsServiceMock.createStatisticsResponse();

    // when
    var result = underTest.createStatisticsResponse();

    // then
    assertThat(result.getReleaseInfo()).isEqualTo(mockResponse.getReleaseInfo());
  }

  @Test
  @DisplayName("mockResponse is returned for ImportInfo")
  void test_mock_import_info_returned() {
    var mockResponse = statisticsServiceMock.createStatisticsResponse();

    // when
    var result = underTest.createStatisticsResponse();

    // then
    assertThat(result.getImportInfo()).isEqualTo(mockResponse.getImportInfo());
  }

  @Nested
  @DisplayName("Tests for UserInfo")
  class UserInfoTest {

    @Test
    @DisplayName("userRepository is called")
    void test_user_repository_called() {
      // when
      underTest.createStatisticsResponse();

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
      var result = underTest.createStatisticsResponse();

      // then
      var usersPerMonth = result.getUserInfo().getUsersPerMonth();
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
      var result = underTest.createStatisticsResponse();

      // then
      assertThat(result.getUserInfo().getTotalUsers()).isEqualTo(5);
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
      var result = underTest.createStatisticsResponse();

      // then
      assertThat(result.getUserInfo().getNewThisMonth()).isEqualTo(2);
    }
  }

  @Nested
  @DisplayName("Tests for ArtistFollowingInfo")
  class ArtistFollowingInfoTest {

    @Test
    @DisplayName("followActionRepository is called")
    void test_follow_action_repository_called() {
      // when
      underTest.createStatisticsResponse();

      // then
      verify(followActionRepository).findAll();
    }

    @Test
    @DisplayName("sorted map with created follow actions per month is returned")
    void test_sorted_follow_actions_per_month_returned() {
      //given
      var user = UserEntityFactory.createDefaultUser();
      var artist = ArtistEntityFactory.withExternalId("someId");
      var followAction1 = FollowActionEntity.builder().user(user).artist(artist).build();
      var followAction2 = FollowActionEntity.builder().user(user).artist(artist).build();
      var followAction3 = FollowActionEntity.builder().user(user).artist(artist).build();
      var followAction4 = FollowActionEntity.builder().user(user).artist(artist).build();
      var followAction5 = FollowActionEntity.builder().user(user).artist(artist).build();
      var localDate1 = LocalDate.of(2020, 1, 1);
      var localDate2 = LocalDate.of(2021, 1, 1);
      var localDate3 = LocalDate.of(2022, 1, 1);
      followAction1.setCreatedDateTime(Date.from(localDate1.atStartOfDay(ZoneId.systemDefault()).toInstant()));
      followAction2.setCreatedDateTime(Date.from(localDate2.atStartOfDay(ZoneId.systemDefault()).toInstant()));
      followAction3.setCreatedDateTime(Date.from(localDate3.atStartOfDay(ZoneId.systemDefault()).toInstant()));
      followAction4.setCreatedDateTime(Date.from(localDate1.atStartOfDay(ZoneId.systemDefault()).toInstant()));
      followAction5.setCreatedDateTime(Date.from(localDate2.atStartOfDay(ZoneId.systemDefault()).toInstant()));
      var followActions = List.of(followAction5, followAction4, followAction3, followAction2, followAction1);
      doReturn(followActions).when(followActionRepository).findAll();

      // when
      var result = underTest.createStatisticsResponse();

      // then
      var followingsPerMonth = result.getArtistFollowingInfo().getFollowingsPerMonth();
      assertThat(followingsPerMonth.size()).isEqualTo(3);
      assertThat(followingsPerMonth).containsExactly(
          Map.entry(YearMonth.of(2020, 1), 2L),
          Map.entry(YearMonth.of(2021, 1), 2L),
          Map.entry(YearMonth.of(2022, 1), 1L)
      );
    }

    @Test
    @DisplayName("total number of followActions is returned")
    void test_total_number_of_follow_actions_returned() {
      //given
      var user = UserEntityFactory.createDefaultUser();
      var artist = ArtistEntityFactory.withExternalId("someId");
      var followAction = FollowActionEntity.builder().user(user).artist(artist).build();
      var localDate = LocalDate.now();
      followAction.setCreatedDateTime(Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
      var followActions = List.of(followAction, followAction, followAction, followAction, followAction);
      doReturn(followActions).when(followActionRepository).findAll();

      // when
      var result = underTest.createStatisticsResponse();

      // then
      assertThat(result.getArtistFollowingInfo().getTotalFollowings()).isEqualTo(5);
    }

    @Test
    @DisplayName("new followings this month is returned")
    void test_new_followings_this_month_returned() {
      //given
      var user = UserEntityFactory.createDefaultUser();
      var artist = ArtistEntityFactory.withExternalId("someId");
      var oldFollowAction = FollowActionEntity.builder().user(user).artist(artist).build();
      var newFollowAction1 = FollowActionEntity.builder().user(user).artist(artist).build();
      var newFollowAction2 = FollowActionEntity.builder().user(user).artist(artist).build();
      var localDate = LocalDate.now();
      oldFollowAction.setCreatedDateTime(Date.from(localDate.minusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
      newFollowAction1.setCreatedDateTime(Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
      newFollowAction2.setCreatedDateTime(Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
      var followActions = List.of(oldFollowAction, newFollowAction1, newFollowAction2);
      doReturn(followActions).when(followActionRepository).findAll();

      // when
      var result = underTest.createStatisticsResponse();

      // then
      assertThat(result.getArtistFollowingInfo().getFollowingsThisMonth()).isEqualTo(2);
    }
  }
}
