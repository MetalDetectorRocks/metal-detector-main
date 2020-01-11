package com.metalr2.model.artist;

import com.metalr2.testutil.WithIntegrationTestProfile;
import com.metalr2.web.DtoFactory;
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
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@DataJpaTest
class FollowedArtistsRepositoryIT implements WithAssertions, WithIntegrationTestProfile {

  @Autowired
  private FollowedArtistsRepository followedArtistsRepository;

  @BeforeEach
  void setup() {
    followedArtistsRepository.saveAll(DtoFactory.FollowArtistFactory.createFollowArtistEntities(6));
  }

  @AfterEach
  void tearDown() {
    followedArtistsRepository.deleteAll();
  }

  @Test
  @DisplayName("findAllByPublicUserId() finds the correct entities for a given user id if it exists")
  void find_all_by_user_id_should_return_correct_entities() {
    List<FollowedArtistEntity> entities = followedArtistsRepository.findAllByPublicUserId("1");

    assertThat(entities).hasSize(6);

    for (int i = 0; i < entities.size(); i++) {
      FollowedArtistEntity entity = entities.get(i);
      assertThat(entity.getPublicUserId()).isEqualTo("1");
      assertThat(entity.getArtistDiscogsId()).isEqualTo(i+1);
    }
  }

  @Test
  @DisplayName("findAllByPublicUserId() returns empty list for a given user id if it does not exist")
  void find_all_by_user_id_should_return_empty_list() {
    List<FollowedArtistEntity> notFollowedArtistEntitiesPerUser = followedArtistsRepository.findAllByPublicUserId("0");

    assertThat(notFollowedArtistEntitiesPerUser).isEmpty();
  }

  @Test
  @DisplayName("Should return true for existing combination of user id and artist discogs id")
  void exists_by_user_id_and_artist_discogs_id() {
    boolean result = followedArtistsRepository.existsByPublicUserIdAndArtistDiscogsId("1", 1L);

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
            Arguments.of("0", 1L),
            Arguments.of("1", 0L)
    );
  }

  @Test
  @DisplayName("Should return optional containing the correct entity for existing combinations of user id and artist discogs id")
  void find_by_user_id_and_artist_discogs_id_should_return_valid_optional() {
    Optional<FollowedArtistEntity> optionalFollowedArtistEntity = followedArtistsRepository.findByPublicUserIdAndArtistDiscogsId("1", 1L);

    assertThat(optionalFollowedArtistEntity.isPresent()).isTrue();
    assertThat(optionalFollowedArtistEntity.get().getArtistDiscogsId()).isEqualTo(1L);
    assertThat(optionalFollowedArtistEntity.get().getPublicUserId()).isEqualTo("1");
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
            Arguments.of("0", 1L),
            Arguments.of(null, 1L)
    );
  }

  @Test
  @DisplayName("findAllByPublicUserIdAndArtistDiscogsIdIn() should return a list with correct results")
  void find_all_by_user_id_discogs_ids_should_return_correct_results() {
    List<FollowedArtistEntity> entities = followedArtistsRepository.findAllByPublicUserIdAndArtistDiscogsIdIn("1", 1L, 0L);

    assertThat(entities).hasSize(1);
    assertThat(entities.get(0).getArtistDiscogsId()).isEqualTo(1L);
    assertThat(entities.get(0).getPublicUserId()).isEqualTo("1");
  }

  @Test
  @DisplayName("findAllByPublicUserId(id, pageable) should return correct paginated items")
  void find_all_by_discogs_id_paginated() {
    List<FollowedArtistEntity> entities = followedArtistsRepository.findAllByPublicUserId("1", PageRequest.of(1,2));

    assertThat(entities).hasSize(2);

    assertThat(entities.get(0).getArtistDiscogsId()).isEqualTo(3L);
    assertThat(entities.get(0).getPublicUserId()).isEqualTo("1");

    assertThat(entities.get(1).getArtistDiscogsId()).isEqualTo(4L);
    assertThat(entities.get(1).getPublicUserId()).isEqualTo("1");
  }
}
