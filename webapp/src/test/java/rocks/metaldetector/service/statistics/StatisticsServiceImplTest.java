package rocks.metaldetector.service.statistics;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.metaldetector.butler.facade.ButlerStatisticsService;
import rocks.metaldetector.butler.facade.dto.ButlerStatisticsDto;
import rocks.metaldetector.butler.facade.dto.ImportInfoDto;
import rocks.metaldetector.butler.facade.dto.ReleaseInfoDto;
import rocks.metaldetector.persistence.domain.artist.FollowActionRepository;
import rocks.metaldetector.persistence.domain.artist.FollowingsPerMonth;
import rocks.metaldetector.persistence.domain.user.UserRepository;
import rocks.metaldetector.service.user.UserEntityFactory;
import rocks.metaldetector.web.api.response.ImportInfo;
import rocks.metaldetector.web.api.response.ReleaseInfo;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class StatisticsServiceImplTest implements WithAssertions {

  @Mock
  UserRepository userRepository;
  @Mock
  FollowActionRepository followActionRepository;
  @Mock
  ButlerStatisticsService butlerStatisticsService;
  @Mock
  ObjectMapper objectMapper;

  @InjectMocks
  StatisticsServiceImpl underTest;

  @BeforeEach
  void setup() {
    doReturn(ButlerStatisticsDto.builder().importInfo(Collections.emptyList()).build()).when(butlerStatisticsService).getButlerStatistics();
  }

  @AfterEach
  void tearDown() {
    reset(userRepository, followActionRepository, butlerStatisticsService, objectMapper);
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
      verify(followActionRepository).groupFollowingsByYearAndMonth();
    }

    @Test
    @DisplayName("sorted map with created follow actions per month is returned")
    void test_sorted_follow_actions_per_month_returned() {
      //given
      var followingsPerMonths = setupFollowingPerMonthTest();
      doReturn(followingsPerMonths).when(followActionRepository).groupFollowingsByYearAndMonth();

      // when
      var result = underTest.createStatisticsResponse();

      // then
      var resultFollowingsPerMonth = result.getArtistFollowingInfo().getFollowingsPerMonth();
      assertThat(resultFollowingsPerMonth.size()).isEqualTo(3);
      assertThat(resultFollowingsPerMonth).containsExactly(
          Map.entry(YearMonth.of(2020, 1), 1L),
          Map.entry(YearMonth.of(2021, 1), 2L),
          Map.entry(YearMonth.of(2022, 1), 3L)
      );
    }

    @Test
    @DisplayName("total number of followActions is returned")
    void test_total_number_of_follow_actions_returned() {
      //given
      var followingsPerMonths = setupFollowingPerMonthTest();
      doReturn(followingsPerMonths).when(followActionRepository).groupFollowingsByYearAndMonth();

      // when
      var result = underTest.createStatisticsResponse();

      // then
      assertThat(result.getArtistFollowingInfo().getTotalFollowings()).isEqualTo(6);
    }

    @Test
    @DisplayName("new followings this month is returned")
    void test_new_followings_this_month_returned() {
      //given
      var localDateNow = LocalDate.now();
      var followingsPerMonthNow = mock(FollowingsPerMonth.class);
      doReturn(6L).when(followingsPerMonthNow).getFollowings();
      doReturn(localDateNow.getYear()).when(followingsPerMonthNow).getFollowingYear();
      doReturn(localDateNow.getMonth().getValue()).when(followingsPerMonthNow).getFollowingMonth();

      var followingsPerMonths = setupFollowingPerMonthTest();
      followingsPerMonths.add(followingsPerMonthNow);
      doReturn(followingsPerMonths).when(followActionRepository).groupFollowingsByYearAndMonth();

      // when
      var result = underTest.createStatisticsResponse();

      // then
      assertThat(result.getArtistFollowingInfo().getFollowingsThisMonth()).isEqualTo(6);
    }

    private List<FollowingsPerMonth> setupFollowingPerMonthTest() {
      var followingsPerMonth1 = mock(FollowingsPerMonth.class);
      var followingsPerMonth2 = mock(FollowingsPerMonth.class);
      var followingsPerMonth3 = mock(FollowingsPerMonth.class);
      var followingsPerMonth4 = mock(FollowingsPerMonth.class);
      var followingsPerMonth5 = mock(FollowingsPerMonth.class);

      var localDate1 = LocalDate.of(2020, 1, 1);
      var localDate2 = LocalDate.of(2021, 1, 1);
      var localDate3 = LocalDate.of(2022, 1, 1);

      doReturn(localDate1.getYear()).when(followingsPerMonth1).getFollowingYear();
      doReturn(localDate1.getMonth().getValue()).when(followingsPerMonth1).getFollowingMonth();
      doReturn(localDate2.getYear()).when(followingsPerMonth2).getFollowingYear();
      doReturn(localDate2.getMonth().getValue()).when(followingsPerMonth2).getFollowingMonth();
      doReturn(localDate3.getYear()).when(followingsPerMonth3).getFollowingYear();
      doReturn(localDate3.getMonth().getValue()).when(followingsPerMonth3).getFollowingMonth();
      doReturn(localDate1.getYear()).when(followingsPerMonth4).getFollowingYear();
      doReturn(localDate1.getMonth().getValue()).when(followingsPerMonth4).getFollowingMonth();
      doReturn(localDate2.getYear()).when(followingsPerMonth5).getFollowingYear();
      doReturn(localDate2.getMonth().getValue()).when(followingsPerMonth5).getFollowingMonth();

      doReturn(1L).when(followingsPerMonth1).getFollowings();
      doReturn(2L).when(followingsPerMonth2).getFollowings();
      doReturn(3L).when(followingsPerMonth3).getFollowings();

      List<FollowingsPerMonth> followingsPerMonths = new ArrayList<>();
      followingsPerMonths.add(followingsPerMonth1);
      followingsPerMonths.add(followingsPerMonth2);
      followingsPerMonths.add(followingsPerMonth3);
      followingsPerMonths.add(followingsPerMonth4);
      followingsPerMonths.add(followingsPerMonth5);
      return followingsPerMonths;
    }
  }

  @Nested
  @DisplayName("Tests for ReleaseInfo")
  class ReleaseInfoTest {

    @Test
    @DisplayName("butlerStatisticsService is called")
    void test_butler_statistics_service_called() {
      // when
      underTest.createStatisticsResponse();

      // then
      verify(butlerStatisticsService).getButlerStatistics();
    }

    @Test
    @DisplayName("objectMapper is called")
    void test_object_mapper_called() {
      // given
      var expectedResponse = ButlerStatisticsDto.builder()
          .releaseInfo(ReleaseInfoDto.builder().build())
          .importInfo(Collections.emptyList())
          .build();
      doReturn(expectedResponse).when(butlerStatisticsService).getButlerStatistics();

      // when
      underTest.createStatisticsResponse();

      // then
      verify(objectMapper).convertValue(expectedResponse.getReleaseInfo(), ReleaseInfo.class);
    }

    @Test
    @DisplayName("releaseInfo is returned")
    void test_release_info_returned() {
      // given
      var expectedResponse = ReleaseInfo.builder().build();
      doReturn(expectedResponse).when(objectMapper).convertValue(any(), eq(ReleaseInfo.class));

      // when
      var result = underTest.createStatisticsResponse();

      // then
      assertThat(result.getReleaseInfo()).isEqualTo(expectedResponse);
    }
  }

  @Nested
  @DisplayName("Tests for ImportInfo")
  class ImportInfoTest {

    @Test
    @DisplayName("butlerStatisticsService is called")
    void test_butler_statistics_service_called() {
      // when
      underTest.createStatisticsResponse();

      // then
      verify(butlerStatisticsService).getButlerStatistics();
    }

    @Test
    @DisplayName("objectMapper is called for every info")
    void test_object_mapper_called() {
      // given
      var importInfos = List.of(ImportInfoDto.builder().source("source1").build(),
                                ImportInfoDto.builder().source("source2").build());
      var expectedResponse = ButlerStatisticsDto.builder()
          .releaseInfo(ReleaseInfoDto.builder().build())
          .importInfo(importInfos)
          .build();
      doReturn(expectedResponse).when(butlerStatisticsService).getButlerStatistics();

      // when
      underTest.createStatisticsResponse();

      // then
      verify(objectMapper).convertValue(importInfos.get(0), ImportInfo.class);
      verify(objectMapper).convertValue(importInfos.get(1), ImportInfo.class);
    }

    @Test
    @DisplayName("importInfo is returned")
    void test_import_info_returned() {
      // given
      var expectedResponse = ImportInfo.builder().source("source1").build();
      doReturn(expectedResponse).when(objectMapper).convertValue(any(), eq(ImportInfo.class));
      doReturn(null).when(objectMapper).convertValue(any(), eq(ReleaseInfo.class));
      doReturn(ButlerStatisticsDto.builder().importInfo(List.of(ImportInfoDto.builder().build())).build())
          .when(butlerStatisticsService).getButlerStatistics();

      // when
      var result = underTest.createStatisticsResponse();

      // then
      assertThat(result.getImportInfo()).isNotEmpty();
      assertThat(result.getImportInfo().get(0)).isEqualTo(expectedResponse);
    }
  }
}
