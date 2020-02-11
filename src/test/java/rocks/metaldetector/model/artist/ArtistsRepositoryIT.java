package rocks.metaldetector.model.artist;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import rocks.metaldetector.testutil.WithIntegrationTestProfile;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static rocks.metaldetector.web.DtoFactory.ArtistEntityFactory;

@DataJpaTest
class ArtistsRepositoryIT implements WithAssertions, WithIntegrationTestProfile {

  @Autowired
  private ArtistsRepository artistsRepository;

  @BeforeEach
  void setUp() {
    artistsRepository.save(ArtistEntityFactory.createArtistEntity(1L, "1", null));
    artistsRepository.save(ArtistEntityFactory.createArtistEntity(2L, "2", null));
    artistsRepository.save(ArtistEntityFactory.createArtistEntity(3L, "3", null));
    artistsRepository.save(ArtistEntityFactory.createArtistEntity(4L, "4", null));
    artistsRepository.save(ArtistEntityFactory.createArtistEntity(5L, "5", null));
  }

  @AfterEach
  void tearDown() {
    artistsRepository.deleteAll();
  }

  @ParameterizedTest(name = "[{index}] => Entity <{0}>")
  @ValueSource(longs = {1, 2, 3})
  @DisplayName("findByArtistDiscogsId() finds the correct entity for a given artist id if it exists")
  void find_by_artist_discogs_id_should_return_correct_entity(long entity) {
    Optional<ArtistEntity> artistEntityOptional = artistsRepository.findByArtistDiscogsId(entity);

    assertThat(artistEntityOptional).isPresent();
    assertThat(artistEntityOptional.get().getArtistDiscogsId()).isEqualTo(entity);
    assertThat(artistEntityOptional.get().getArtistName()).isEqualTo(String.valueOf(entity));
  }

  @Test
  @DisplayName("findByArtistDiscogsId() returns empty optional if given artist does not exist")
  void find_by_artist_discogs_id_should_return_empty_optional() {
    Optional<ArtistEntity> artistEntityOptional = artistsRepository.findByArtistDiscogsId(0L);

    assertThat(artistEntityOptional).isEmpty();
  }

  @ParameterizedTest(name = "[{index}] => Entity <{0}>")
  @ValueSource(longs = {1, 2, 3})
  @DisplayName("existsByArtistDiscogsId() should return true if artist exists")
  void exists_by_artist_discogs_id_should_return_true(long entity) {
    boolean exists = artistsRepository.existsByArtistDiscogsId(entity);

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
    List<ArtistEntity> artistEntities = artistsRepository.findAllByArtistDiscogsIdIn(0L, 1L, 2L);

    assertThat(artistEntities).hasSize(2);

    for (int i = 0; i < artistEntities.size(); i++) {
      ArtistEntity entity = artistEntities.get(i);
      assertThat(entity.getArtistName()).isEqualTo(String.valueOf(i + 1));
      assertThat(entity.getArtistDiscogsId()).isEqualTo(i + 1);
      assertThat(entity.getThumb()).isNull();
    }
  }

  @ParameterizedTest(name = "[{index}] => DiscogsIds <{0}>")
  @MethodSource("inputProviderDiscogsIds")
  @DisplayName("findAllByArtistDiscogsIds() should return correct entities if they exist")
  void find_all_by_artist_discogs_ids_should_return_correct_entities_parametrized(long[] discogsIds) {
    List<ArtistEntity> artistEntities = artistsRepository.findAllByArtistDiscogsIdIn(discogsIds);

    assertThat(artistEntities).hasSize(discogsIds.length);

    for (int i = 0; i < discogsIds.length; i++) {
      ArtistEntity entity = artistEntities.get(i);
      assertThat(entity.getArtistName()).isEqualTo(String.valueOf(i + 1));
      assertThat(entity.getArtistDiscogsId()).isEqualTo(i + 1);
      assertThat(entity.getThumb()).isNull();
    }
  }

  private static Stream<Arguments> inputProviderDiscogsIds() {
    return Stream.of(
        Arguments.of((Object) new long[] {2L, 1L}),
        Arguments.of((Object) new long[] {1L}),
        Arguments.of((Object) new long[] {})
    );
  }
}