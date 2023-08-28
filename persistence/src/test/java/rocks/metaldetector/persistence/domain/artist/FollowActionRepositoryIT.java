package rocks.metaldetector.persistence.domain.artist;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import rocks.metaldetector.persistence.BaseDataJpaTest;
import rocks.metaldetector.persistence.WithIntegrationTestConfig;
import rocks.metaldetector.persistence.domain.user.UserEntity;
import rocks.metaldetector.persistence.domain.user.UserFactory;
import rocks.metaldetector.persistence.domain.user.UserRepository;

import java.time.LocalDate;
import java.util.List;

import static rocks.metaldetector.persistence.domain.artist.ArtistSource.DISCOGS;
import static rocks.metaldetector.persistence.domain.artist.ArtistSource.SPOTIFY;

class FollowActionRepositoryIT extends BaseDataJpaTest implements WithAssertions, WithIntegrationTestConfig {

  @Autowired
  private FollowActionRepository underTest;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ArtistRepository artistRepository;

  private final UserEntity userA = UserFactory.createUser("A", "a@test.com");
  private final UserEntity userB = UserFactory.createUser("B", "b@test.com");
  private final ArtistEntity artist1 = ArtistEntityFactory.createArtistEntity("1", "1", SPOTIFY);
  private final ArtistEntity artist2 = ArtistEntityFactory.createArtistEntity("2", "2", SPOTIFY);
  private final ArtistEntity artist3 = ArtistEntityFactory.createArtistEntity("3", "3", DISCOGS);
  private final FollowActionEntity userAFollowsArtist1 = FollowActionEntity.builder().user(userA).artist(artist1).build();
  private final FollowActionEntity userAFollowsArtist2 = FollowActionEntity.builder().user(userA).artist(artist2).build();
  private final FollowActionEntity userAFollowsArtist3 = FollowActionEntity.builder().user(userA).artist(artist3).build();
  private final FollowActionEntity userBFollowsArtist1 = FollowActionEntity.builder().user(userB).artist(artist1).build();
  private final FollowActionEntity userBFollowsArtist2 = FollowActionEntity.builder().user(userB).artist(artist2).build();

  @BeforeEach
  void setUp() {
    userRepository.saveAll(List.of(userA, userB));
    artistRepository.saveAll(List.of(artist1, artist2, artist3));
    underTest.saveAll(List.of(userAFollowsArtist1, userAFollowsArtist2, userAFollowsArtist3, userBFollowsArtist1, userBFollowsArtist2));
  }

  @AfterEach
  void tearDown() {
    underTest.deleteAll();
    userRepository.deleteAll();
    artistRepository.deleteAll();
  }

  @Test
  @DisplayName("Should return all FollowActions by a given user")
  void should_return_all_follow_actions_by_a_given_user() {
    // when
    List<FollowActionEntity> followActions = underTest.findAllByUser(userA);

    // then
    assertThat(followActions).containsExactly(userAFollowsArtist1, userAFollowsArtist2, userAFollowsArtist3);
  }

  @Test
  @DisplayName("Should delete a FollowAction by a given user and artist")
  void should_delete_a_follow_action_by_a_given_user_and_artist() {
    // when
    underTest.deleteByUserAndArtist(userB, artist1);
    List<FollowActionEntity> followActions = underTest.findAllByUser(userB);

    // then
    assertThat(followActions).containsExactly(userBFollowsArtist2);
  }

  @Test
  @DisplayName("Should delete all FollowActions by a given user")
  void test_delete_all_by_user() {
    // when
    underTest.deleteAllByUser(userA);
    var followActions = underTest.findAllByUser(userA);

    // then
    assertThat(followActions).isEmpty();
  }

  @Test
  @DisplayName("Should return true if a follow action exists for a given user and artist")
  void should_return_true_if_a_follow_action_exists() {
    // when
    boolean result = underTest.existsByUserAndArtist(userA, artist1);

    // then
    assertThat(result).isTrue();
  }

  @Test
  @DisplayName("Should return false if a follow action does not exist for a given user and artist")
  void should_return_false_if_a_follow_action_does_not_exist() {
    // when
    boolean result = underTest.existsByUserAndArtist(userB, artist3);

    // then
    assertThat(result).isFalse();
  }

  @Test
  @DisplayName("Should return correct followings per month")
  void should_return_followings_per_month() {
    // when
    List<FollowingsPerMonth> result = underTest.groupFollowingsByYearAndMonth();

    // then
    assertThat(result.size()).isEqualTo(1);
    var now = LocalDate.now();
    assertThat(result.get(0).getFollowingYear()).isEqualTo(now.getYear());
    assertThat(result.get(0).getFollowingMonth()).isEqualTo(now.getMonth().getValue());
    assertThat(result.get(0).getFollowings()).isEqualTo(5);
  }
}
