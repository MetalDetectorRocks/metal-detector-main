package rocks.metaldetector.persistence.domain.artist;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import rocks.metaldetector.persistence.BaseDataJpaTest;
import rocks.metaldetector.persistence.WithIntegrationTestConfig;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static rocks.metaldetector.persistence.domain.artist.ArtistSource.DISCOGS;
import static rocks.metaldetector.persistence.domain.artist.ArtistSource.SPOTIFY;

class ArtistRepositoryIT extends BaseDataJpaTest implements WithAssertions, WithIntegrationTestConfig {

  @Autowired
  private ArtistRepository artistRepository;

  @BeforeEach
  void setUp() {
    artistRepository.save(ArtistEntityFactory.createArtistEntity("1", "1", SPOTIFY));
    artistRepository.save(ArtistEntityFactory.createArtistEntity("2", "2", SPOTIFY));
    artistRepository.save(ArtistEntityFactory.createArtistEntity("3", "3", DISCOGS));
    artistRepository.save(ArtistEntityFactory.createArtistEntity("4", "4", SPOTIFY));
    artistRepository.save(ArtistEntityFactory.createArtistEntity("5", "5", SPOTIFY));
  }

  @AfterEach
  void tearDown() {
    artistRepository.deleteAll();
  }

  @ParameterizedTest(name = "[{index}] => Entity <{0}>")
  @MethodSource("artistDetailsProvider")
  @DisplayName("findByExternalIdAndSource() finds the correct entity for a given artist id and source if it exists")
  void find_by_artist_external_id_should_return_correct_entity(String artistId, ArtistSource source) {
    Optional<ArtistEntity> artistEntityOptional = artistRepository.findByExternalIdAndSource(artistId, source);

    assertThat(artistEntityOptional).isPresent();
    assertThat(artistEntityOptional.get().getExternalId()).isEqualTo(artistId);
    assertThat(artistEntityOptional.get().getArtistName()).isEqualTo(String.valueOf(artistId));
  }

  @Test
  @DisplayName("findByExternalIdAndSource() returns empty optional if given artist does not exist")
  void find_by_artist_external_id_should_return_empty_optional() {
    Optional<ArtistEntity> artistEntityOptional = artistRepository.findByExternalIdAndSource("0", SPOTIFY);

    assertThat(artistEntityOptional).isEmpty();
  }

  @ParameterizedTest(name = "[{index}] => Entity <{0}>")
  @MethodSource("artistDetailsProvider")
  @DisplayName("existsByExternalIdAndSource() should return true if artist exists")
  void exists_by_artist_external_id_should_return_true(String entity, ArtistSource source) {
    boolean exists = artistRepository.existsByExternalIdAndSource(entity, source);

    assertThat(exists).isTrue();
  }

  @Test
  @DisplayName("existsByExternalIdAndSource() should return false if artist does not exist")
  void exists_by_artist_external_id_should_return_false() {
    boolean exists = artistRepository.existsByExternalIdAndSource("0", SPOTIFY);

    assertThat(exists).isFalse();
  }

  @Test
  @DisplayName("findAllByArtistExternalIds() should return correct entities if they exist")
  void find_all_by_artist_external_ids_should_return_correct_entities() {
    List<ArtistEntity> artistEntities = artistRepository.findAllByExternalIdIn(List.of("0", "1", "2"));

    assertThat(artistEntities).hasSize(2);

    for (int i = 0; i < artistEntities.size(); i++) {
      ArtistEntity entity = artistEntities.get(i);
      assertThat(entity.getArtistName()).isEqualTo(String.valueOf(i + 1));
      assertThat(entity.getExternalId()).isEqualTo(String.valueOf(i + 1));
      assertThat(entity.getThumb()).isNull();
    }
  }

  @ParameterizedTest(name = "[{index}] => ExternalIds <{0}>")
  @MethodSource("inputProviderExternalIds")
  @DisplayName("findAllByArtistExternalIds() should return correct entities if they exist")
  void find_all_by_artist_external_ids_should_return_correct_entities_parametrized(Collection<String> externalIds) {
    List<ArtistEntity> artistEntities = artistRepository.findAllByExternalIdIn(externalIds);

    assertThat(artistEntities).hasSize(externalIds.size());

    for (int i = 0; i < externalIds.size(); i++) {
      ArtistEntity entity = artistEntities.get(i);
      assertThat(entity.getArtistName()).isEqualTo(String.valueOf(i + 1));
      assertThat(entity.getExternalId()).isEqualTo(String.valueOf(i + 1));
      assertThat(entity.getThumb()).isNull();
    }
  }

  private static Stream<Arguments> artistDetailsProvider() {
    return Stream.of(
            Arguments.of("1", SPOTIFY),
            Arguments.of("2", SPOTIFY),
            Arguments.of("3", DISCOGS)
    );
  }

  private static Stream<Arguments> inputProviderExternalIds() {
    return Stream.of(
        Arguments.of(List.of("2", "1")),
        Arguments.of(List.of("1")),
        Arguments.of(Collections.emptyList())
    );
  }
}