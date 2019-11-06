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
class FollowedArtistRepositoryIT implements WithAssertions {

  private static final long userId                 = 1L;
  private static final long artistDiscogsId        = 252211L;
  private static final long unknownUserId          = 0L;
  private static final long unknownArtistDiscogsId = 0L;
  private static final FollowedArtistEntity followedArtistEntity = new FollowedArtistEntity(userId, artistDiscogsId);

  @Autowired
  private FollowedArtistRepository followedArtistRepository;

  @BeforeEach
  void setup() {
    followedArtistRepository.save(followedArtistEntity);
  }

  @AfterEach
  void tearDown() {
    followedArtistRepository.deleteAll();
  }

  @Test
  @DisplayName("findFollowedArtistEntitiesByUserId() finds the correct entities for a given user id")
  void find_followed_artist_entities_by_user_id() {
    List<FollowedArtistEntity> followedArtistEntitiesPerUser = followedArtistRepository.findFollowedArtistEntitiesByUserId(userId);

    assertThat(followedArtistEntitiesPerUser.size()).isEqualTo(1);
    assertThat(followedArtistEntitiesPerUser.get(0)).isEqualTo(followedArtistEntity);

    List<FollowedArtistEntity> notFollowedArtistEntitiesPerUser = followedArtistRepository.findFollowedArtistEntitiesByUserId(unknownUserId);

    assertThat(notFollowedArtistEntitiesPerUser.size()).isEqualTo(0);
  }

  @Test
  @DisplayName("findFollowedArtistEntitiesByArtistDiscogsId() finds the correct entities for a given artist discogs id")
  void find_followed_artist_entities_by_artist_discogs_id() {
    List<FollowedArtistEntity> followedArtistEntitiesPerArtist = followedArtistRepository.findFollowedArtistEntitiesByArtistDiscogsId(artistDiscogsId);

    assertThat(followedArtistEntitiesPerArtist.size()).isEqualTo(1);
    assertThat(followedArtistEntitiesPerArtist.get(0)).isEqualTo(followedArtistEntity);

    List<FollowedArtistEntity> notFollowedArtistEntitiesPerArtist = followedArtistRepository.findFollowedArtistEntitiesByArtistDiscogsId(unknownArtistDiscogsId);

    assertThat(notFollowedArtistEntitiesPerArtist.size()).isEqualTo(0);
  }

  @Test
  @DisplayName("Should return true for existing and false for not existing combinations of user id and artist discogs id")
  void exists_followed_artist_entity_by_user_id_and_artist_discogs_id() {
    boolean existingUserAndArtistDiscogsId     = followedArtistRepository.existsFollowedArtistEntityByUserIdAndArtistDiscogsId(userId, artistDiscogsId);
    boolean notExistingUserAndArtistDiscogsId1 = followedArtistRepository.existsFollowedArtistEntityByUserIdAndArtistDiscogsId(unknownUserId, artistDiscogsId);
    boolean notExistingUserAndArtistDiscogsId2 = followedArtistRepository.existsFollowedArtistEntityByUserIdAndArtistDiscogsId(userId, unknownArtistDiscogsId);

    assertThat(existingUserAndArtistDiscogsId).isTrue();
    assertThat(notExistingUserAndArtistDiscogsId1).isFalse();
    assertThat(notExistingUserAndArtistDiscogsId2).isFalse();
  }
}