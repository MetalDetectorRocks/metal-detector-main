package com.metalr2.model.artist;

import com.metalr2.testutil.WithIntegrationTestProfile;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@DataJpaTest
class FollowedArtistsRepositoryIT implements WithAssertions, WithIntegrationTestProfile {

  private static final String userId1              = "1";
  private static final String userId2              = "2";
  private static final String unknownUserId        = "0";
  private static final long artistDiscogsId1       = 252211L;
  private static final long artistDiscogsId2       = 245797L;
  private static final long unknownArtistDiscogsId = 0L;
  private static final FollowedArtistEntity FOLLOW_ARTIST_ENTITY1 = new FollowedArtistEntity(userId1, artistDiscogsId1);
  private static final FollowedArtistEntity FOLLOW_ARTIST_ENTITY2 = new FollowedArtistEntity(userId2, artistDiscogsId2);

  @Autowired
  private FollowedArtistsRepository followedArtistsRepository;

  @BeforeEach
  void setup() {
    followedArtistsRepository.save(FOLLOW_ARTIST_ENTITY1);
    followedArtistsRepository.save(FOLLOW_ARTIST_ENTITY2);
  }

  @AfterEach
  void tearDown() {
    followedArtistsRepository.deleteAll();
  }

  @Test
  @DisplayName("findAllByPublicUserId() finds the correct entities for a given user id if it exists")
  void find_all_by_user_id_should_return_correct_entities() {
    List<FollowedArtistEntity> followedArtistEntitiesPerUser = followedArtistsRepository.findAllByPublicUserId(userId1);

    assertThat(followedArtistEntitiesPerUser).hasSize(1);
    assertThat(followedArtistEntitiesPerUser.get(0).getPublicUserId()).isEqualTo(userId1);
    assertThat(followedArtistEntitiesPerUser.get(0).getArtistDiscogsId()).isEqualTo(artistDiscogsId1);
  }

  @Test
  @DisplayName("findAllByPublicUserId() returns empty list for a given user id if it does not exist")
  void find_all_by_user_id_should_return_empty_list() {
    List<FollowedArtistEntity> notFollowedArtistEntitiesPerUser = followedArtistsRepository.findAllByPublicUserId(unknownUserId);

    assertThat(notFollowedArtistEntitiesPerUser).isEmpty();
  }

  @Test
  @DisplayName("Should return true for existing combination of user id and artist discogs id")
  void exists_by_user_id_and_artist_discogs_id() {
    boolean result = followedArtistsRepository.existsByPublicUserIdAndArtistDiscogsId(userId1, artistDiscogsId1);

    assertThat(result).isTrue();
  }


  @ParameterizedTest(name = "[{index}] => UserId <{0}> | ArtistDiscogsId <{1}>")
  @MethodSource("inputProviderExistsByFalse")
  @DisplayName("Should return false for not existing combinations of user id and artist discogs id")
  void exists_by_user_id_and_artist_discogs_id(String userId, long artistDiscogsId) {
    boolean result = followedArtistsRepository.existsByPublicUserIdAndArtistDiscogsId(userId, artistDiscogsId);

    assertThat(result).isFalse();
  }

  private static Stream<Arguments> inputProviderExistsByFalse() {
    return Stream.of(
            Arguments.of(unknownUserId, artistDiscogsId1),
            Arguments.of(userId1, unknownArtistDiscogsId)
    );
  }

  @Test
  @DisplayName("Should return optional containing the correct entity for existing combinations of user id and artist discogs id")
  void find_by_user_id_and_artist_discogs_id_should_return_valid_optional() {
    Optional<FollowedArtistEntity> optionalFollowedArtistEntity = followedArtistsRepository.findByPublicUserIdAndArtistDiscogsId(userId1, artistDiscogsId1);

    assertThat(optionalFollowedArtistEntity.isPresent()).isTrue();
    assertThat(optionalFollowedArtistEntity.get().getArtistDiscogsId()).isEqualTo(artistDiscogsId1);
    assertThat(optionalFollowedArtistEntity.get().getPublicUserId()).isEqualTo(userId1);
  }

  @ParameterizedTest(name = "[{index}] => UserId <{0}> | ArtistDiscogsId <{1}>")
  @MethodSource("inputProviderFalseArguments")
  @DisplayName("Should return an empty optional for not existing or faulty combinations of user id and artist discogs id")
  void find_by_user_id_and_artist_discogs_id_should_return_empty_optional(String userId, long artistDiscogsId) {
    Optional<FollowedArtistEntity> optionalFollowedArtistEntity = followedArtistsRepository.findByPublicUserIdAndArtistDiscogsId(userId, artistDiscogsId);

    assertThat(optionalFollowedArtistEntity).isEmpty();
  }

  private static Stream<Arguments> inputProviderFalseArguments() {
    return Stream.of(
            Arguments.of(unknownUserId, artistDiscogsId1),
            Arguments.of(null, artistDiscogsId1)
    );
  }
}
