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

  private static final String userId1              = "1";
  private static final String userId2              = "2";
  private static final String unknownUserId        = "0";
  private static final String artistName1          = "Darkthrone";
  private static final String artistName2          = "Opeth";
  private static final String unknownArtistName    = "Nirvana";
  private static final long artistDiscogsId1       = 252211L;
  private static final long artistDiscogsId2       = 245797L;
  private static final long unknownArtistDiscogsId = 0L;
  private static final FollowedArtistEntity FOLLOW_ARTIST_ENTITY1 = new FollowedArtistEntity(userId1, artistName1, artistDiscogsId1);
  private static final FollowedArtistEntity FOLLOW_ARTIST_ENTITY2 = new FollowedArtistEntity(userId2, artistName2, artistDiscogsId2);

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
    assertThat(followedArtistEntitiesPerUser.get(0).getArtistName()).isEqualTo(artistName1);
  }

  @Test
  @DisplayName("findAllByPublicUserId() returns empty list for a given user id if it does not exist")
  void find_all_by_user_id_should_return_empty_list() {
    List<FollowedArtistEntity> notFollowedArtistEntitiesPerUser = followedArtistsRepository.findAllByPublicUserId(unknownUserId);

    assertThat(notFollowedArtistEntitiesPerUser.isEmpty()).isTrue();
  }

  @Test
  @DisplayName("findAllByArtistDiscogsId() finds the correct entities for a given artist discogs id if it exists")
  void find_all_by_artist_discogs_id_should_return_correct_entities() {
    List<FollowedArtistEntity> followedArtistEntitiesPerArtist = followedArtistsRepository.findAllByArtistDiscogsId(artistDiscogsId1);

    assertThat(followedArtistEntitiesPerArtist).hasSize(1);
    assertThat(followedArtistEntitiesPerArtist.get(0).getArtistDiscogsId()).isEqualTo(artistDiscogsId1);
    assertThat(followedArtistEntitiesPerArtist.get(0).getPublicUserId()).isEqualTo(userId1);
    assertThat(followedArtistEntitiesPerArtist.get(0).getArtistName()).isEqualTo(artistName1);
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
    boolean existingUserAndArtistDiscogsId     = followedArtistsRepository.existsByPublicUserIdAndArtistDiscogsId(userId1, artistDiscogsId1);
    boolean notExistingUserAndArtistDiscogsId1 = followedArtistsRepository.existsByPublicUserIdAndArtistDiscogsId(unknownUserId, artistDiscogsId1);
    boolean notExistingUserAndArtistDiscogsId2 = followedArtistsRepository.existsByPublicUserIdAndArtistDiscogsId(userId1, unknownArtistDiscogsId);

    assertThat(existingUserAndArtistDiscogsId).isTrue();
    assertThat(notExistingUserAndArtistDiscogsId1).isFalse();
    assertThat(notExistingUserAndArtistDiscogsId2).isFalse();
  }

  @Test
  @DisplayName("Should return optional containing the correct entity for existing combinations of user id and artist discogs id")
  void find_by_user_id_and_artist_discogs_id_should_return_valid_optional() {
    Optional<FollowedArtistEntity> optionalFollowedArtistEntity = followedArtistsRepository.findByPublicUserIdAndArtistDiscogsId(userId1, artistDiscogsId1);

    assertThat(optionalFollowedArtistEntity.isPresent()).isTrue();
    assertThat(optionalFollowedArtistEntity.get().getArtistDiscogsId()).isEqualTo(artistDiscogsId1);
    assertThat(optionalFollowedArtistEntity.get().getPublicUserId()).isEqualTo(userId1);
    assertThat(optionalFollowedArtistEntity.get().getArtistName()).isEqualTo(artistName1);
  }

  @Test
  @DisplayName("Should return an empty optional for not existing combinations of user id and artist discogs id")
  void find_by_user_id_and_artist_discogs_id_should_return_empty_optional_if_not_exist() {
    Optional<FollowedArtistEntity> optionalFollowedArtistEntity = followedArtistsRepository.findByPublicUserIdAndArtistDiscogsId(unknownUserId, artistDiscogsId1);

    assertThat(optionalFollowedArtistEntity.isEmpty()).isTrue();
  }

  @Test
  @DisplayName("Should return an empty optional for false requests")
  void find_by_user_id_and_artist_discogs_id_should_return_empty_optional_if_false_request() {
    Optional<FollowedArtistEntity> optionalFollowedArtistEntity = followedArtistsRepository.findByPublicUserIdAndArtistDiscogsId(null, artistDiscogsId1);

    assertThat(optionalFollowedArtistEntity.isEmpty()).isTrue();
  }

  @Test
  @DisplayName("findAllByArtistName() finds the correct entities for a given artist name id if it exists")
  void find_all_by_artist_name_should_return_correct_entities() {
    List<FollowedArtistEntity> followedArtistEntitiesPerArtist = followedArtistsRepository.findAllByArtistName(artistName1);

    assertThat(followedArtistEntitiesPerArtist).hasSize(1);
    assertThat(followedArtistEntitiesPerArtist.get(0).getArtistDiscogsId()).isEqualTo(artistDiscogsId1);
    assertThat(followedArtistEntitiesPerArtist.get(0).getArtistName()).isEqualTo(artistName1);
    assertThat(followedArtistEntitiesPerArtist.get(0).getPublicUserId()).isEqualTo(userId1);
  }

  @Test
  @DisplayName("findAllByArtistName() returns empty list for a given artist name if it does not exist")
  void find_all_by_artist_name_should_return_empty_list() {
    List<FollowedArtistEntity> notFollowedArtistEntitiesPerArtist = followedArtistsRepository.findAllByArtistName(unknownArtistName);

    assertThat(notFollowedArtistEntitiesPerArtist.isEmpty()).isTrue();
  }

}