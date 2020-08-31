package rocks.metaldetector.service.artist;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
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
import rocks.metaldetector.persistence.domain.artist.ArtistSource;
import rocks.metaldetector.persistence.domain.user.UserEntity;
import rocks.metaldetector.persistence.domain.user.UserRepository;
import rocks.metaldetector.security.CurrentPublicUserIdSupplier;
import rocks.metaldetector.spotify.facade.SpotifyService;
import rocks.metaldetector.support.exceptions.ResourceNotFoundException;
import rocks.metaldetector.testutil.DtoFactory.ArtistDtoFactory;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static rocks.metaldetector.persistence.domain.artist.ArtistSource.DISCOGS;
import static rocks.metaldetector.persistence.domain.artist.ArtistSource.SPOTIFY;
import static rocks.metaldetector.testutil.DtoFactory.DiscogsArtistDtoFactory;
import static rocks.metaldetector.testutil.DtoFactory.SpotifyArtistDtoFactory;

@ExtendWith(MockitoExtension.class)
class FollowArtistServiceImplTest implements WithAssertions {

  private static final String EXTERNAL_ID = "252211";
  private static final ArtistSource ARTIST_SOURCE = DISCOGS;

  @Mock
  private UserRepository userRepository;

  @Mock
  private ArtistRepository artistRepository;

  @Mock
  private SpotifyService spotifyService;

  @Mock
  private DiscogsService discogsService;

  @Mock
  private ArtistTransformer artistTransformer;

  @Mock
  private CurrentPublicUserIdSupplier currentPublicUserIdSupplier;

  @InjectMocks
  private FollowArtistServiceImpl underTest;

  @Mock
  private UserEntity userEntity;

  @AfterEach
  void tearDown() {
    reset(userRepository, artistRepository, currentPublicUserIdSupplier, discogsService, artistTransformer, userEntity, spotifyService);
  }

  @Test
  @DisplayName("Artist is fetched from repository if it already exists on follow")
  void follow_should_get_artist() {
    // given
    when(artistRepository.existsByExternalIdAndSource(anyString(), any())).thenReturn(true);
    when(artistRepository.findByExternalIdAndSource(anyString(), any())).thenReturn(Optional.of(ArtistEntityFactory.withExternalId(EXTERNAL_ID)));
    when(currentPublicUserIdSupplier.get()).thenReturn(UUID.randomUUID().toString());
    when(userRepository.findByPublicId(anyString())).thenReturn(Optional.of(userEntity));

    // when
    underTest.follow(EXTERNAL_ID, ARTIST_SOURCE);

    // then
    InOrder inOrderVerifier = inOrder(artistRepository);
    inOrderVerifier.verify(artistRepository, times(1)).existsByExternalIdAndSource(EXTERNAL_ID, ARTIST_SOURCE);
    inOrderVerifier.verify(artistRepository, times(1)).findByExternalIdAndSource(EXTERNAL_ID, ARTIST_SOURCE);
  }

  @Test
  @DisplayName("Current user is fetched on follow")
  void follow_should_get_current_user() {
    // given
    String publicUserId = UUID.randomUUID().toString();
    when(artistRepository.existsByExternalIdAndSource(anyString(), any())).thenReturn(true);
    when(artistRepository.findByExternalIdAndSource(anyString(), any())).thenReturn(Optional.of(ArtistEntityFactory.withExternalId(EXTERNAL_ID)));
    when(currentPublicUserIdSupplier.get()).thenReturn(publicUserId);
    when(userRepository.findByPublicId(anyString())).thenReturn(Optional.of(userEntity));

    // when
    underTest.follow(EXTERNAL_ID, ARTIST_SOURCE);

    // then
    verify(currentPublicUserIdSupplier, times(1)).get();
    verify(userRepository, times(1)).findByPublicId(publicUserId);
  }

  @Test
  @DisplayName("Exception is thrown on follow when user not found")
  void follow_should_throw_exception() {
    // given
    String userId = "publicUserId";
    when(artistRepository.existsByExternalIdAndSource(anyString(), any())).thenReturn(true);
    when(artistRepository.findByExternalIdAndSource(anyString(), any())).thenReturn(Optional.of(ArtistEntityFactory.withExternalId(EXTERNAL_ID)));
    when(currentPublicUserIdSupplier.get()).thenReturn(userId);
    when(userRepository.findByPublicId(anyString())).thenThrow(new ResourceNotFoundException(userId));

    // when
    Throwable throwable = catchThrowable(() -> underTest.follow("1", ARTIST_SOURCE));

    // then
    assertThat(throwable).isInstanceOf(ResourceNotFoundException.class);
    assertThat(throwable).hasMessageContaining(userId);
  }

  @Test
  @DisplayName("Spotify Artist is only searched on Spotify if it does not yet exist on follow")
  void follow_should_search_spotify() {
    // given
    when(spotifyService.searchArtistById(anyString())).thenReturn(SpotifyArtistDtoFactory.createDefault());
    when(currentPublicUserIdSupplier.get()).thenReturn(UUID.randomUUID().toString());
    when(artistRepository.save(any())).thenReturn(ArtistEntityFactory.withExternalId(EXTERNAL_ID));
    when(userRepository.findByPublicId(anyString())).thenReturn(Optional.of(userEntity));

    // when
    underTest.follow(EXTERNAL_ID, SPOTIFY);

    // then
    verify(spotifyService, times(1)).searchArtistById(EXTERNAL_ID);
    verifyNoInteractions(discogsService);
  }

  @Test
  @DisplayName("Discogs Artist is only searched on Discogs if it does not yet exist on follow")
  void follow_should_search_discogs() {
    // given
    when(discogsService.searchArtistById(anyString())).thenReturn(DiscogsArtistDtoFactory.createDefault());
    when(currentPublicUserIdSupplier.get()).thenReturn(UUID.randomUUID().toString());
    when(artistRepository.save(any())).thenReturn(ArtistEntityFactory.withExternalId(EXTERNAL_ID));
    when(userRepository.findByPublicId(anyString())).thenReturn(Optional.of(userEntity));

    // when
    underTest.follow(EXTERNAL_ID, DISCOGS);

    // then
    verify(discogsService, times(1)).searchArtistById(EXTERNAL_ID);
    verifyNoInteractions(spotifyService);
  }

  @Test
  @DisplayName("Neither Spotify nor Discogs is searched if artist already exists")
  void follow_should_not_search() {
    // given
    when(artistRepository.existsByExternalIdAndSource(anyString(), any())).thenReturn(true);
    when(artistRepository.findByExternalIdAndSource(anyString(), any())).thenReturn(Optional.of(ArtistEntityFactory.withExternalId(EXTERNAL_ID)));
    when(currentPublicUserIdSupplier.get()).thenReturn(UUID.randomUUID().toString());
    when(userRepository.findByPublicId(anyString())).thenReturn(Optional.of(userEntity));

    // when
    underTest.follow(EXTERNAL_ID, ARTIST_SOURCE);

    // then
    verifyNoInteractions(spotifyService);
    verifyNoInteractions(discogsService);
  }

  @Test
  @DisplayName("Artist is saved in repository if it does not yet exist on follow")
  void follow_should_save_artist() {
    // given
    ArgumentCaptor<ArtistEntity> argumentCaptor = ArgumentCaptor.forClass(ArtistEntity.class);
    DiscogsArtistDto discogsArtist = DiscogsArtistDtoFactory.createDefault();
    when(discogsService.searchArtistById(anyString())).thenReturn(discogsArtist);
    when(currentPublicUserIdSupplier.get()).thenReturn(UUID.randomUUID().toString());
    when(userRepository.findByPublicId(anyString())).thenReturn(Optional.of(userEntity));
    when(artistRepository.save(any())).thenReturn(ArtistEntityFactory.withExternalId(EXTERNAL_ID));

    // when
    underTest.follow(EXTERNAL_ID, ARTIST_SOURCE);

    // then
    verify(artistRepository, times(1)).save(argumentCaptor.capture());

    ArtistEntity artistEntity = argumentCaptor.getValue();
    assertThat(artistEntity.getArtistName()).isEqualTo(discogsArtist.getName());
    assertThat(artistEntity.getExternalId()).isEqualTo(discogsArtist.getId());
    assertThat(artistEntity.getThumb()).isEqualTo(discogsArtist.getImageUrl());
    assertThat(artistEntity.getSource()).isEqualTo(ARTIST_SOURCE);
  }

  @Test
  @DisplayName("User is updated with artist on follow")
  void follow_should_add_artist_to_user() {
    // given
    ArtistEntity artist = ArtistEntityFactory.withExternalId(EXTERNAL_ID);
    when(artistRepository.existsByExternalIdAndSource(anyString(), any())).thenReturn(true);
    when(artistRepository.findByExternalIdAndSource(anyString(), any())).thenReturn(Optional.of(artist));
    when(currentPublicUserIdSupplier.get()).thenReturn(UUID.randomUUID().toString());
    when(userRepository.findByPublicId(anyString())).thenReturn(Optional.of(userEntity));

    // when
    underTest.follow(EXTERNAL_ID, ARTIST_SOURCE);

    // then
    verify(userEntity, times(1)).addFollowedArtist(artist);
    verify(userRepository, times(1)).save(userEntity);
  }

  @Test
  @DisplayName("Artist is fetched from repository on unfollow")
  void unfollow_should_fetch_artist_from_repository() {
    // given
    when(artistRepository.findByExternalIdAndSource(anyString(), any())).thenReturn(Optional.of(ArtistEntityFactory.withExternalId(EXTERNAL_ID)));
    when(currentPublicUserIdSupplier.get()).thenReturn(UUID.randomUUID().toString());
    when(userRepository.findByPublicId(anyString())).thenReturn(Optional.of(userEntity));

    // when
    underTest.unfollow(EXTERNAL_ID, ARTIST_SOURCE);

    // then
    verify(artistRepository, times(1)).findByExternalIdAndSource(EXTERNAL_ID, ARTIST_SOURCE);
  }

  @Test
  @DisplayName("Current user is fetched on unfollow")
  void unfollow_should_fetch_current_user() {
    // given
    String publicUserId = UUID.randomUUID().toString();
    when(artistRepository.findByExternalIdAndSource(anyString(), any())).thenReturn(Optional.of(ArtistEntityFactory.withExternalId(EXTERNAL_ID)));
    when(currentPublicUserIdSupplier.get()).thenReturn(publicUserId);
    when(userRepository.findByPublicId(anyString())).thenReturn(Optional.of(userEntity));

    // when
    underTest.unfollow(EXTERNAL_ID, ARTIST_SOURCE);

    // then
    verify(currentPublicUserIdSupplier, times(1)).get();
    verify(userRepository, times(1)).findByPublicId(publicUserId);
  }

  @Test
  @DisplayName("Exception is thrown on unfollow when user not found")
  void unfollow_should_throw_exception() {
    // given
    String userId = "publicUserId";
    when(artistRepository.findByExternalIdAndSource(anyString(), any())).thenReturn(Optional.of(ArtistEntityFactory.withExternalId(EXTERNAL_ID)));
    when(currentPublicUserIdSupplier.get()).thenReturn(userId);
    when(userRepository.findByPublicId(anyString())).thenThrow(new ResourceNotFoundException(userId));

    // when
    Throwable throwable = catchThrowable(() -> underTest.unfollow(EXTERNAL_ID, ARTIST_SOURCE));

    // then
    assertThat(throwable).isInstanceOf(ResourceNotFoundException.class);
    assertThat(throwable).hasMessageContaining(userId);
  }

  @Test
  @DisplayName("Artist is removed from user on unfollow")
  void unfollow_should_remove_artist_from_user() {
    // given
    ArgumentCaptor<UserEntity> argumentCaptor = ArgumentCaptor.forClass(UserEntity.class);
    ArtistEntity artist = ArtistEntityFactory.withExternalId(EXTERNAL_ID);
    userEntity.addFollowedArtist(artist);
    when(artistRepository.findByExternalIdAndSource(anyString(), any())).thenReturn(Optional.of(artist));
    when(currentPublicUserIdSupplier.get()).thenReturn(UUID.randomUUID().toString());
    when(userRepository.findByPublicId(anyString())).thenReturn(Optional.of(userEntity));

    // when
    underTest.unfollow(EXTERNAL_ID, ARTIST_SOURCE);

    // then
    verify(userRepository, times(1)).save(argumentCaptor.capture());

    UserEntity updatedUser = argumentCaptor.getValue();
    assertThat(updatedUser.getFollowedArtists()).doesNotContain(artist);
  }

  @Test
  @DisplayName("Getting followed artists should get current user")
  void get_followed_should_call_user_supplier() {
    // given
    String userId = "publicUserId";
    when(currentPublicUserIdSupplier.get()).thenReturn(userId);
    when(userRepository.findByPublicId(anyString())).thenReturn(Optional.of(userEntity));

    // when
    underTest.getFollowedArtistsOfCurrentUser();

    // then
    verify(currentPublicUserIdSupplier, times(1)).get();
    verify(userRepository, times(1)).findByPublicId(userId);
  }

  @Test
  @DisplayName("Getting followed artists throws Exception when user not found")
  void get_followed_should_throw_exception() {
    // given
    String userId = "publicUserId";
    when(currentPublicUserIdSupplier.get()).thenReturn(userId);
    when(userRepository.findByPublicId(anyString())).thenThrow(new ResourceNotFoundException(userId));

    // when
    Throwable throwable = catchThrowable(() -> underTest.getFollowedArtistsOfCurrentUser());

    // then
    assertThat(throwable).isInstanceOf(ResourceNotFoundException.class);
    assertThat(throwable).hasMessageContaining(userId);
  }

  @Test
  @DisplayName("Getting followed artists calls artist transformer for every artist")
  void get_followed_should_call_artist_transformer() {
    // given
    ArtistEntity artist1 = ArtistEntityFactory.withExternalId("1");
    ArtistEntity artist2 = ArtistEntityFactory.withExternalId("2");
    when(currentPublicUserIdSupplier.get()).thenReturn("userId");
    when(userRepository.findByPublicId(anyString())).thenReturn(Optional.of(userEntity));
    when(userEntity.getFollowedArtists()).thenReturn(Set.of(artist1, artist2));
    when(artistTransformer.transform(any(ArtistEntity.class))).thenReturn(ArtistDtoFactory.createDefault());

    // when
    underTest.getFollowedArtistsOfCurrentUser();

    // then
    verify(artistTransformer, times(1)).transform(artist1);
    verify(artistTransformer, times(1)).transform(artist2);
  }

  @Test
  @DisplayName("Getting followed artists returns a sorted list of artist dtos")
  void get_followed_should_return_sorted_artist_dtos() {
    // given
    ArtistEntity artistEntity1 = ArtistEntityFactory.withExternalId("1");
    ArtistEntity artistEntity2 = ArtistEntityFactory.withExternalId("2");
    ArtistEntity artistEntity3 = ArtistEntityFactory.withExternalId("3");
    ArtistDto artistDto1 = ArtistDtoFactory.withName("Darkthrone");
    ArtistDto artistDto2 = ArtistDtoFactory.withName("Borknagar");
    ArtistDto artistDto3 = ArtistDtoFactory.withName("Alcest");

    when(userRepository.findByPublicId(any())).thenReturn(Optional.of(userEntity));
    when(userEntity.getFollowedArtists()).thenReturn(Set.of(artistEntity1, artistEntity2, artistEntity3));
    when(artistTransformer.transform(artistEntity1)).thenReturn(artistDto1);
    when(artistTransformer.transform(artistEntity2)).thenReturn(artistDto2);
    when(artistTransformer.transform(artistEntity3)).thenReturn(artistDto3);

    // when
    List<ArtistDto> followedArtists = underTest.getFollowedArtistsOfCurrentUser();

    // then
    assertThat(followedArtists).hasSize(3);
    assertThat(followedArtists.get(0)).isEqualTo(artistDto3);
    assertThat(followedArtists.get(1)).isEqualTo(artistDto2);
    assertThat(followedArtists.get(2)).isEqualTo(artistDto1);
  }
}
