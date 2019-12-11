package com.metalr2.service.artist;

import com.metalr2.model.artist.ArtistEntity;
import com.metalr2.model.artist.ArtistsRepository;
import com.metalr2.model.artist.FollowedArtistEntity;
import com.metalr2.web.dto.ArtistDto;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArtistsServiceTest implements WithAssertions {

  private static final String artistName    = "Darkthrone";
  private static final long artistDiscogsId = 252211L;
  private static final String thumb         = "thumb";

  @Mock
  private ArtistsRepository artistsRepository;

  @InjectMocks
  private ArtistsServiceImpl artistsService;

  ArtistEntity artistEntity;


  @BeforeEach
  void setUp() {
    this.artistEntity = new ArtistEntity(artistDiscogsId, artistName, thumb);
  }

  @AfterEach
  void tearDown() {
    reset(artistsRepository);
  }

  @Test
  @DisplayName("findByArtistDiscogsId() should return the correct artist if it exists")
  void find_by_artist_discogs_id_should_return_correct_artist() {
    // given
    when(artistsRepository.findByArtistDiscogsId(artistDiscogsId)).thenReturn(Optional.of(artistEntity));

    // when
    Optional<ArtistDto> artistOptional = artistsService.findByArtistDiscogsId(artistDiscogsId);

    // then
    verify(artistsRepository,times(1)).findByArtistDiscogsId(artistDiscogsId);

    assertThat(artistOptional).isPresent();
    assertThat(artistOptional.get().getArtistDiscogsId()).isEqualTo(artistDiscogsId);
    assertThat(artistOptional.get().getArtistName()).isEqualTo(artistName);
    assertThat(artistOptional.get().getThumb()).isEqualTo(thumb);
  }

  @Test
  @DisplayName("findByArtistDiscogsId() should return an empty optional if artist does not exist")
  void find_by_artist_discogs_id_should_return_empty_optional() {
    // given
    when(artistsRepository.findByArtistDiscogsId(0L)).thenReturn(Optional.empty());

    // when
    Optional<ArtistDto> artistOptional = artistsService.findByArtistDiscogsId(0L);

    // then
    verify(artistsRepository,times(1)).findByArtistDiscogsId(0L);

    assertThat(artistOptional).isEmpty();
  }

  @Test
  @DisplayName("findAllByArtistDiscogsIds() should return all given entities that exist")
  void find_all_by_artist_discogs_ids_should_return_all_entities_that_exist() {
    // given
    when(artistsRepository.findAllByArtistDiscogsIds(artistDiscogsId,0L)).thenReturn(List.of(artistEntity));

    // when
    List<ArtistDto> artists = artistsService.findAllByArtistDiscogsIds(artistDiscogsId,0L);

    // then
    verify(artistsRepository,times(1)).findAllByArtistDiscogsIds(artistDiscogsId,0L);

    assertThat(artists).hasSize(1);
    assertThat(artists.get(0).getArtistDiscogsId()).isEqualTo(artistDiscogsId);
    assertThat(artists.get(0).getArtistName()).isEqualTo(artistName);
    assertThat(artists.get(0).getThumb()).isEqualTo(thumb);
  }

  @Test
  @DisplayName("existsByArtistDiscogsId() should return true if given entity exists")
  void exists_by_artist_discogs_id_should_return_true() {
    // given
    when(artistsRepository.existsByArtistDiscogsId(artistDiscogsId)).thenReturn(true);

    // when
    boolean exists = artistsService.existsByArtistDiscogsId(artistDiscogsId);

    // then
    verify(artistsRepository,times(1)).existsByArtistDiscogsId(artistDiscogsId);

    assertThat(exists).isTrue();
  }

  @Test
  @DisplayName("existsByArtistDiscogsId() should return false if given entity does not exist")
  void exists_by_artist_discogs_id_should_return_false() {
    // given
    when(artistsRepository.existsByArtistDiscogsId(0L)).thenReturn(false);

    // when
    boolean exists = artistsService.existsByArtistDiscogsId(0L);

    // then
    verify(artistsRepository,times(1)).existsByArtistDiscogsId(0L);

    assertThat(exists).isFalse();
  }

}