package com.metalr2.model.followArtist;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Optional;

@DataJpaTest
@Tag("integration-test")
@TestPropertySource(locations = "classpath:application-test.properties")
class FollowedArtistsRepositoryIT implements WithAssertions {

  private static final String userId               = "1";
  private static final String unknownUserId        = "0";
  private static final long artistDiscogsId        = 252211L;
  private static final long unknownArtistDiscogsId = 0L;
  private static final FollowedArtistEntity FOLLOW_ARTIST_ENTITY = new FollowedArtistEntity(userId, artistDiscogsId);

  @Autowired
  private FollowedArtistsRepository followedArtistsRepository;

  @BeforeEach
  void setup() {
    followedArtistsRepository.save(FOLLOW_ARTIST_ENTITY);
  }

  @AfterEach
  void tearDown() {
    followedArtistsRepository.deleteAll();
  }

  @Test
  @DisplayName("findAllByPublicUserId() finds the correct entities for a given user id if it exists")
  void find_all_by_user_id_should_return_user_entities() {
    List<FollowedArtistEntity> followedArtistEntitiesPerUser = followedArtistsRepository.findAllByPublicUserId(userId);

    assertThat(followedArtistEntitiesPerUser.size()).isEqualTo(1);
    assertThat(followedArtistEntitiesPerUser.get(0).getPublicUserId()).isEqualTo(FOLLOW_ARTIST_ENTITY.getPublicUserId());
    assertThat(followedArtistEntitiesPerUser.get(0).getArtistDiscogsId()).isEqualTo(FOLLOW_ARTIST_ENTITY.getArtistDiscogsId());
  }

  @Test
  @DisplayName("findAllByPublicUserId() returns empty list for a given user id if it does not exist")
  void find_all_by_user_id_should_return_empty_list() {
    List<FollowedArtistEntity> notFollowedArtistEntitiesPerUser = followedArtistsRepository.findAllByPublicUserId(unknownUserId);

    assertThat(notFollowedArtistEntitiesPerUser.isEmpty()).isTrue();
  }

  @Test
  @DisplayName("findAllByArtistDiscogsId() finds the correct entities for a given artist discogs id if it exists")
  void find_all_by_artist_discogs_id_should_return_user_entities() {
    List<FollowedArtistEntity> followedArtistEntitiesPerArtist = followedArtistsRepository.findAllByArtistDiscogsId(artistDiscogsId);

    assertThat(followedArtistEntitiesPerArtist.size()).isEqualTo(1);
    assertThat(followedArtistEntitiesPerArtist.get(0).getArtistDiscogsId()).isEqualTo(FOLLOW_ARTIST_ENTITY.getArtistDiscogsId());
    assertThat(followedArtistEntitiesPerArtist.get(0).getPublicUserId()).isEqualTo(FOLLOW_ARTIST_ENTITY.getPublicUserId());
  }

  @Test
  @DisplayName("findAllByArtistDiscogsId() returns empty list for a given user id if it does not exist")
  void find_all_by_artist_discogs_id_should_return_empty_list() {
    List<FollowedArtistEntity> notFollowedArtistEntitiesPerArtist = followedArtistsRepository.findAllByArtistDiscogsId(unknownArtistDiscogsId);

    assertThat(notFollowedArtistEntitiesPerArtist.isEmpty()).isTrue();
  }

  @Test
  @DisplayName("Should return true for existing and false for not existing combinations of user id and artist discogs id")
  void exists_by_user_id_and_artist_discogs_id() {
    boolean existingUserAndArtistDiscogsId     = followedArtistsRepository.existsByPublicUserIdAndArtistDiscogsId(userId, artistDiscogsId);
    boolean notExistingUserAndArtistDiscogsId1 = followedArtistsRepository.existsByPublicUserIdAndArtistDiscogsId(unknownUserId, artistDiscogsId);
    boolean notExistingUserAndArtistDiscogsId2 = followedArtistsRepository.existsByPublicUserIdAndArtistDiscogsId(userId, unknownArtistDiscogsId);

    assertThat(existingUserAndArtistDiscogsId).isTrue();
    assertThat(notExistingUserAndArtistDiscogsId1).isFalse();
    assertThat(notExistingUserAndArtistDiscogsId2).isFalse();
  }

  @Test
  @DisplayName("Should return optional containing the correct entity for existing combinations of user id and artist discogs id")
  void find_by_user_id_and_artist_discogs_id_should_return_valid_optional() {
    Optional<FollowedArtistEntity> optionalFollowedArtistEntity = followedArtistsRepository.findByPublicUserIdAndArtistDiscogsId(userId,artistDiscogsId);

    assertThat(optionalFollowedArtistEntity.isPresent()).isTrue();
    assertThat(optionalFollowedArtistEntity.get().getArtistDiscogsId()).isEqualTo(artistDiscogsId);
    assertThat(optionalFollowedArtistEntity.get().getPublicUserId()).isEqualTo(userId);
  }

  @Test
  @DisplayName("Should return an empty optional for not existing combinations of user id and artist discogs id")
  void find_by_user_id_and_artist_discogs_id_should_return_empty_optional_if_not_exist() {
    Optional<FollowedArtistEntity> optionalFollowedArtistEntity = followedArtistsRepository.findByPublicUserIdAndArtistDiscogsId(unknownUserId,artistDiscogsId);

    assertThat(optionalFollowedArtistEntity.isEmpty()).isTrue();
  }

  @Test
  @DisplayName("Should return an empty optional for not existing combinations of user id and artist discogs id")
  void find_by_user_id_and_artist_discogs_id_should_return_empty_optional_if_false_request() {
    Optional<FollowedArtistEntity> optionalFollowedArtistEntity = followedArtistsRepository.findByPublicUserIdAndArtistDiscogsId(null,artistDiscogsId);

    assertThat(optionalFollowedArtistEntity.isEmpty()).isTrue();
  }
}