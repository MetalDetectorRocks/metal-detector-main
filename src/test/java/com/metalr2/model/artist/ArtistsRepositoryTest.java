package com.metalr2.model.artist;

import com.metalr2.testutil.WithIntegrationTestProfile;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

@DataJpaTest
class ArtistsRepositoryTest implements WithAssertions, WithIntegrationTestProfile {

  private static final long artistDiscogsId1 = 245797;
  private static final long artistDiscogsId2 = 252211;
  private static final String artistName1 = "Opeth";
  private static final String artistName2 = "Darkthrone";
  private static final String thumb = "thumb";
  private static final ArtistEntity artist1 = new ArtistEntity(artistDiscogsId1, artistName1, thumb);
  private static final ArtistEntity artist2 = new ArtistEntity(artistDiscogsId2, artistName2, null);

  @Autowired
  private ArtistsRepository artistsRepository;

  @BeforeEach
  void setUp() {
    artistsRepository.save(artist1);
    artistsRepository.save(artist2);
  }

  @AfterEach
  void tearDown() {
    artistsRepository.deleteAll();
  }

  @Test
  @DisplayName("findByArtistDiscogsId() finds the correct entity for a given artist id if it exists")
  void find_by_artist_discogs_id_should_return_correct_entity() {
    Optional<ArtistEntity> artistEntityOptional = artistsRepository.findByArtistDiscogsId(artistDiscogsId1);

    assertThat(artistEntityOptional).isPresent();
    assertThat(artistEntityOptional.get()).isEqualTo(artist1);
  }

  @Test
  @DisplayName("findByArtistDiscogsId() returns empty optional if given artist does not exist")
  void find_by_artist_discogs_id_should_return_empty_optional() {
    Optional<ArtistEntity> artistEntityOptional = artistsRepository.findByArtistDiscogsId(0L);

    assertThat(artistEntityOptional).isEmpty();
  }

  @Test
  @DisplayName("existsByArtistDiscogsId() should return true if artist exists")
  void exists_by_artist_discogs_id_should_return_true() {
    boolean exists = artistsRepository.existsByArtistDiscogsId(artistDiscogsId1);

    assertThat(exists).isTrue();
  }

  @Test
  @DisplayName("existsByArtistDiscogsId() should return false if artist does not exist")
  void exists_by_artist_discogs_id_should_return_false() {
    boolean exists = artistsRepository.existsByArtistDiscogsId(0L);

    assertThat(exists).isFalse();
  }

  @Test
  @DisplayName("findAllByArtistDiscogsIds() should return correct entities if they exist")
  void find_all_by_artist_discogs_ids_should_return_correct_entities() {
    List<ArtistEntity> artistEntities = artistsRepository.findAllByArtistDiscogsIdIn(artistDiscogsId1,artistDiscogsId2,0L);

    assertThat(artistEntities).hasSize(2);

    assertThat(artistEntities.get(0)).isEqualTo(artist1);
    assertThat(artistEntities.get(1)).isEqualTo(artist2);
  }

}