package rocks.metaldetector.service.follow;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.metaldetector.discogs.facade.DiscogsService;
import rocks.metaldetector.discogs.facade.dto.DiscogsArtistDto;
import rocks.metaldetector.persistence.domain.artist.ArtistEntity;
import rocks.metaldetector.persistence.domain.artist.ArtistRepository;
import rocks.metaldetector.persistence.domain.user.UserEntity;
import rocks.metaldetector.persistence.domain.user.UserRepository;
import rocks.metaldetector.security.CurrentUserSupplier;
import rocks.metaldetector.service.artist.ArtistEntityFactory;
import rocks.metaldetector.service.user.UserEntityFactory;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static rocks.metaldetector.testutil.DtoFactory.DiscogsArtistDtoFactory;

@ExtendWith(MockitoExtension.class)
class FollowArtistServiceImplTest implements WithAssertions {

  private static final long ARTIST_ID = 252211L;

  @Mock
  private UserRepository userRepository;

  @Mock
  private ArtistRepository artistRepository;

  @Mock
  private DiscogsService discogsService;

  @Mock
  private CurrentUserSupplier currentUserSupplier;

  @InjectMocks
  private FollowArtistServiceImpl underTest;

  private UserEntity userEntity;

  @BeforeEach
  void setup() {
    userEntity = UserEntityFactory.createUser("user", "email");
  }

  @AfterEach
  void tearDown() {
    reset(userRepository, artistRepository, currentUserSupplier, discogsService);
  }

  @Test
  @DisplayName("Artist is fetched from repository if it already exists on follow")
  void follow_should_get_artist() {
    // given
    when(artistRepository.existsByArtistDiscogsId(anyLong())).thenReturn(true);
    when(artistRepository.findByArtistDiscogsId(anyLong())).thenReturn(Optional.of(ArtistEntityFactory.withDiscogsId(ARTIST_ID)));
    when(currentUserSupplier.get()).thenReturn(userEntity);

    // when
    underTest.follow(ARTIST_ID);

    // then
    InOrder inOrderVerifier = inOrder(artistRepository);
    inOrderVerifier.verify(artistRepository, times(1)).existsByArtistDiscogsId(ARTIST_ID);
    inOrderVerifier.verify(artistRepository, times(1)).findByArtistDiscogsId(ARTIST_ID);
  }

  @Test
  @DisplayName("Current user is fetched on follow")
  void follow_should_get_current_user() {
    // given
    when(artistRepository.existsByArtistDiscogsId(anyLong())).thenReturn(true);
    when(artistRepository.findByArtistDiscogsId(anyLong())).thenReturn(Optional.of(ArtistEntityFactory.withDiscogsId(ARTIST_ID)));
    when(currentUserSupplier.get()).thenReturn(userEntity);

    // when
    underTest.follow(ARTIST_ID);

    // then
    verify(currentUserSupplier, times(1)).get();
  }

  @Test
  @DisplayName("Artist is searched on discogs if it does not yet exist on follow")
  void follow_should_search_discogs() {
    // given
    when(discogsService.searchArtistById(anyLong())).thenReturn(DiscogsArtistDtoFactory.createDefault());
    when(currentUserSupplier.get()).thenReturn(userEntity);
    when(artistRepository.save(any())).thenReturn(ArtistEntityFactory.withDiscogsId(ARTIST_ID));

    // when
    underTest.follow(ARTIST_ID);

    // then
    verify(discogsService, times(1)).searchArtistById(ARTIST_ID);
  }

  @Test
  @DisplayName("Discogs is not searched if artist already exists")
  void follow_should_not_search_discogs() {
    // given
    when(artistRepository.existsByArtistDiscogsId(anyLong())).thenReturn(true);
    when(artistRepository.findByArtistDiscogsId(anyLong())).thenReturn(Optional.of(ArtistEntityFactory.withDiscogsId(ARTIST_ID)));
    when(currentUserSupplier.get()).thenReturn(userEntity);

    // when
    underTest.follow(ARTIST_ID);

    // then
    verifyNoInteractions(discogsService);
  }

  @Test
  @DisplayName("Artist is saved in repository if it does not yet exist on follow")
  void follow_should_save_artist() {
    // given
    ArgumentCaptor<ArtistEntity> argumentCaptor = ArgumentCaptor.forClass(ArtistEntity.class);
    DiscogsArtistDto discogsArtist = DiscogsArtistDtoFactory.createDefault();
    when(discogsService.searchArtistById(anyLong())).thenReturn(discogsArtist);
    when(currentUserSupplier.get()).thenReturn(userEntity);
    when(artistRepository.save(any())).thenReturn(ArtistEntityFactory.withDiscogsId(ARTIST_ID));

    // when
    underTest.follow(ARTIST_ID);

    // then
    verify(artistRepository, times(1)).save(argumentCaptor.capture());

    ArtistEntity artistEntity = argumentCaptor.getValue();
    assertThat(artistEntity.getArtistName()).isEqualTo(discogsArtist.getName());
    assertThat(artistEntity.getArtistDiscogsId()).isEqualTo(discogsArtist.getId());
    assertThat(artistEntity.getThumb()).isEqualTo(discogsArtist.getImageUrl());
  }

  @Test
  @DisplayName("User is updated with artist on follow")
  void follow_should_add_artist_to_user() {
    // given
    ArgumentCaptor<UserEntity> argumentCaptor = ArgumentCaptor.forClass(UserEntity.class);
    ArtistEntity artist = ArtistEntityFactory.withDiscogsId(ARTIST_ID);
    when(artistRepository.existsByArtistDiscogsId(anyLong())).thenReturn(true);
    when(artistRepository.findByArtistDiscogsId(anyLong())).thenReturn(Optional.of(artist));
    when(currentUserSupplier.get()).thenReturn(userEntity);

    // when
    underTest.follow(ARTIST_ID);

    // then
    verify(userRepository, times(1)).save(argumentCaptor.capture());

    UserEntity updatedUser = argumentCaptor.getValue();
    assertThat(updatedUser.getFollowedArtists()).containsOnly(artist);
  }

  @Test
  @DisplayName("Artist is fetched from repository on follow")
  void unfollow_should_fetch_artist_from_repository() {
    // given
    when(artistRepository.findByArtistDiscogsId(anyLong())).thenReturn(Optional.of(ArtistEntityFactory.withDiscogsId(ARTIST_ID)));
    when(currentUserSupplier.get()).thenReturn(userEntity);

    // when
    underTest.unfollow(ARTIST_ID);

    // then
    verify(artistRepository, times(1)).findByArtistDiscogsId(ARTIST_ID);
  }

  @Test
  @DisplayName("Current user is fetched on unfollow")
  void unfollow_should_fetch_current_user() {
    // given
    when(artistRepository.findByArtistDiscogsId(anyLong())).thenReturn(Optional.of(ArtistEntityFactory.withDiscogsId(ARTIST_ID)));
    when(currentUserSupplier.get()).thenReturn(userEntity);

    // when
    underTest.unfollow(ARTIST_ID);

    // then
    verify(currentUserSupplier, times(1)).get();
  }

  @Test
  @DisplayName("Artist is removed from user on unfollow")
  void unfollow_should_remove_artist_from_user() {
    // given
    ArgumentCaptor<UserEntity> argumentCaptor = ArgumentCaptor.forClass(UserEntity.class);
    ArtistEntity artist = ArtistEntityFactory.withDiscogsId(ARTIST_ID);
    userEntity.addFollowedArtist(artist);
    when(artistRepository.findByArtistDiscogsId(anyLong())).thenReturn(Optional.of(artist));
    when(currentUserSupplier.get()).thenReturn(userEntity);

    // when
    underTest.unfollow(ARTIST_ID);

    // then
    verify(userRepository, times(1)).save(argumentCaptor.capture());

    UserEntity updatedUser = argumentCaptor.getValue();
    assertThat(updatedUser.getFollowedArtists()).doesNotContain(artist);
  }
}
