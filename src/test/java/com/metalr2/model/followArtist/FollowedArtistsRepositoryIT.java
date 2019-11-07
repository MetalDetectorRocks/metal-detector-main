package com.metalr2.model.followArtist;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

@DataJpaTest
@Tag("integration-test")
@TestPropertySource(locations = "classpath:application-test.properties")
class FollowedArtistsRepositoryIT implements WithAssertions {

  private static final long userId                 = 1L;
  private static final long artistDiscogsId        = 252211L;
  private static final long unknownUserId          = 0L;
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
  @DisplayName("findFollowedArtistEntitiesByUserId() finds the correct entities for a given user id if it exists")
  void find_followed_artist_entities_by_user_id_should_return_user_entities() {
    List<FollowedArtistEntity> followedArtistEntitiesPerUser = followedArtistsRepository.findFollowedArtistEntitiesByUserId(userId);

    assertThat(followedArtistEntitiesPerUser.size()).isEqualTo(1);
    assertThat(followedArtistEntitiesPerUser.get(0)).isEqualTo(FOLLOW_ARTIST_ENTITY);
  }

  @Test
  @DisplayName("findFollowedArtistEntitiesByUserId() returns empty list for a given user id if it does not exist")
  void find_followed_artist_entities_by_user_id_should_return_empty_list() {
    List<FollowedArtistEntity> notFollowedArtistEntitiesPerUser = followedArtistsRepository.findFollowedArtistEntitiesByUserId(unknownUserId);

    assertThat(notFollowedArtistEntitiesPerUser.size()).isEqualTo(0);
  }

  @Test
  @DisplayName("findFollowedArtistEntitiesByArtistDiscogsId() finds the correct entities for a given artist discogs id if it exists")
  void find_followed_artist_entities_by_artist_discogs_id_should_return_user_entities() {
    List<FollowedArtistEntity> followedArtistEntitiesPerArtist = followedArtistsRepository.findFollowedArtistEntitiesByArtistDiscogsId(artistDiscogsId);

    assertThat(followedArtistEntitiesPerArtist.size()).isEqualTo(1);
    assertThat(followedArtistEntitiesPerArtist.get(0)).isEqualTo(FOLLOW_ARTIST_ENTITY);
  }

  @Test
  @DisplayName("findFollowedArtistEntitiesByArtistDiscogsId() returns empty list for a given user id if it does not exist")
  void find_followed_artist_entities_by_artist_discogs_id_should_return_empty_list() {
    List<FollowedArtistEntity> notFollowedArtistEntitiesPerArtist = followedArtistsRepository.findFollowedArtistEntitiesByArtistDiscogsId(unknownArtistDiscogsId);

    assertThat(notFollowedArtistEntitiesPerArtist.size()).isEqualTo(0);
  }

  @Test
  @DisplayName("Should return true for existing and false for not existing combinations of user id and artist discogs id")
  void exists_followed_artist_entity_by_user_id_and_artist_discogs_id() {
    boolean existingUserAndArtistDiscogsId     = followedArtistsRepository.existsFollowedArtistEntityByUserIdAndArtistDiscogsId(userId, artistDiscogsId);
    boolean notExistingUserAndArtistDiscogsId1 = followedArtistsRepository.existsFollowedArtistEntityByUserIdAndArtistDiscogsId(unknownUserId, artistDiscogsId);
    boolean notExistingUserAndArtistDiscogsId2 = followedArtistsRepository.existsFollowedArtistEntityByUserIdAndArtistDiscogsId(userId, unknownArtistDiscogsId);

    assertThat(existingUserAndArtistDiscogsId).isTrue();
    assertThat(notExistingUserAndArtistDiscogsId1).isFalse();
    assertThat(notExistingUserAndArtistDiscogsId2).isFalse();
  }
}