package rocks.metaldetector.service.artist;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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
import rocks.metaldetector.persistence.domain.artist.FollowActionEntity;
import rocks.metaldetector.persistence.domain.artist.FollowActionRepository;
import rocks.metaldetector.persistence.domain.user.UserEntity;
import rocks.metaldetector.persistence.domain.user.UserRepository;
import rocks.metaldetector.security.CurrentPublicUserIdSupplier;
import rocks.metaldetector.spotify.facade.SpotifyService;
import rocks.metaldetector.support.exceptions.ResourceNotFoundException;
import rocks.metaldetector.testutil.DtoFactory.ArtistDtoFactory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
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

  private static final String PUBLIC_USER_ID = UUID.randomUUID().toString();
  private static final String EXTERNAL_ID = "252211";
  private static final ArtistSource ARTIST_SOURCE = DISCOGS;

  @Mock
  private UserRepository userRepository;

  @Mock
  private ArtistRepository artistRepository;

  @Mock
  private FollowActionRepository followActionRepository;

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
    when(artistRepository.existsByExternalIdAndSource(anyString(), any())).thenReturn(true);
    when(artistRepository.findByExternalIdAndSource(anyString(), any())).thenReturn(Optional.of(ArtistEntityFactory.withExternalId(EXTERNAL_ID)));
    when(currentPublicUserIdSupplier.get()).thenReturn(PUBLIC_USER_ID);
    when(userRepository.findByPublicId(anyString())).thenReturn(Optional.of(userEntity));

    // when
    underTest.follow(EXTERNAL_ID, ARTIST_SOURCE);

    // then
    verify(currentPublicUserIdSupplier, times(1)).get();
    verify(userRepository, times(1)).findByPublicId(PUBLIC_USER_ID);
  }

  @Test
  @DisplayName("Exception is thrown on follow when user not found")
  void follow_should_throw_exception() {
    // given\
    when(artistRepository.existsByExternalIdAndSource(anyString(), any())).thenReturn(true);
    when(artistRepository.findByExternalIdAndSource(anyString(), any())).thenReturn(Optional.of(ArtistEntityFactory.withExternalId(EXTERNAL_ID)));
    when(currentPublicUserIdSupplier.get()).thenReturn(PUBLIC_USER_ID);
    when(userRepository.findByPublicId(anyString())).thenThrow(new ResourceNotFoundException(PUBLIC_USER_ID));

    // when
    Throwable throwable = catchThrowable(() -> underTest.follow("1", ARTIST_SOURCE));

    // then
    assertThat(throwable).isInstanceOf(ResourceNotFoundException.class);
    assertThat(throwable).hasMessageContaining(PUBLIC_USER_ID);
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
  @DisplayName("FollowAction is saved on follow")
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
    FollowActionEntity followAction = FollowActionEntity.builder().user(userEntity).artist(artist).build();
    verify(followActionRepository, times(1)).save(eq(followAction));
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
    when(artistRepository.findByExternalIdAndSource(anyString(), any())).thenReturn(Optional.of(ArtistEntityFactory.withExternalId(EXTERNAL_ID)));
    when(currentPublicUserIdSupplier.get()).thenReturn(PUBLIC_USER_ID);
    when(userRepository.findByPublicId(anyString())).thenReturn(Optional.of(userEntity));

    // when
    underTest.unfollow(EXTERNAL_ID, ARTIST_SOURCE);

    // then
    verify(currentPublicUserIdSupplier, times(1)).get();
    verify(userRepository, times(1)).findByPublicId(PUBLIC_USER_ID);
  }

  @Test
  @DisplayName("Exception is thrown on unfollow when user not found")
  void unfollow_should_throw_exception() {
    // given
    when(artistRepository.findByExternalIdAndSource(anyString(), any())).thenReturn(Optional.of(ArtistEntityFactory.withExternalId(EXTERNAL_ID)));
    when(currentPublicUserIdSupplier.get()).thenReturn(PUBLIC_USER_ID);
    when(userRepository.findByPublicId(anyString())).thenThrow(new ResourceNotFoundException(PUBLIC_USER_ID));

    // when
    Throwable throwable = catchThrowable(() -> underTest.unfollow(EXTERNAL_ID, ARTIST_SOURCE));

    // then
    assertThat(throwable).isInstanceOf(ResourceNotFoundException.class);
    assertThat(throwable).hasMessageContaining(PUBLIC_USER_ID);
  }

  @Test
  @DisplayName("FollowAction is removed on unfollow")
  void unfollow_should_remove_artist_from_user() {
    // given
    ArtistEntity artist = ArtistEntityFactory.withExternalId(EXTERNAL_ID);
    when(artistRepository.findByExternalIdAndSource(anyString(), any())).thenReturn(Optional.of(artist));
    when(currentPublicUserIdSupplier.get()).thenReturn(UUID.randomUUID().toString());
    when(userRepository.findByPublicId(anyString())).thenReturn(Optional.of(userEntity));

    // when
    underTest.unfollow(EXTERNAL_ID, ARTIST_SOURCE);

    // then
    verify(followActionRepository, times(1)).deleteByUserAndArtist(eq(userEntity), eq(artist));
  }

  @Test
  @DisplayName("isCurrentUserFollowing(): should fetch user entity")
  void isCurrentUserFollowing_should_fetch_user_entity() {
    // given
    doReturn(PUBLIC_USER_ID).when(currentPublicUserIdSupplier).get();
    doReturn(Optional.of(userEntity)).when(userRepository).findByPublicId(any());

    // when
    underTest.isCurrentUserFollowing(EXTERNAL_ID, ARTIST_SOURCE);

    // then
    verify(currentPublicUserIdSupplier, times(1)).get();
    verify(userRepository, times(1)).findByPublicId(PUBLIC_USER_ID);
  }

  @Test
  @DisplayName("isCurrentUserFollowing(): should fetch user entity")
  void isCurrentUserFollowing_should_throw_exception() {
    // given
    doReturn(PUBLIC_USER_ID).when(currentPublicUserIdSupplier).get();
    doThrow(new ResourceNotFoundException(PUBLIC_USER_ID)).when(userRepository).findByPublicId(any());

    // when
    Throwable throwable = catchThrowable(() -> underTest.getFollowedArtistsOfCurrentUser());

    // then
    assertThat(throwable).isInstanceOf(ResourceNotFoundException.class);
    assertThat(throwable).hasMessageContaining(PUBLIC_USER_ID);
  }

  @Test
  @DisplayName("isCurrentUserFollowing(): should fetch artist entity")
  void isCurrentUserFollowing_should_fetch_artist_entity() {
    doReturn(PUBLIC_USER_ID).when(currentPublicUserIdSupplier).get();
    doReturn(Optional.of(userEntity)).when(userRepository).findByPublicId(any());

    // when
    underTest.isCurrentUserFollowing(EXTERNAL_ID, ARTIST_SOURCE);

    // then
    verify(artistRepository, times(1)).findByExternalIdAndSource(EXTERNAL_ID, ARTIST_SOURCE);
  }

  @Test
  @DisplayName("isCurrentUserFollowing(): should return false if artist entity does not exist")
  void isCurrentUserFollowing_should_return_false() {
    doReturn(PUBLIC_USER_ID).when(currentPublicUserIdSupplier).get();
    doReturn(Optional.of(userEntity)).when(userRepository).findByPublicId(any());
    doReturn(Optional.empty()).when(artistRepository).findByExternalIdAndSource(any(), any());

    // when
    boolean result = underTest.isCurrentUserFollowing(EXTERNAL_ID, ARTIST_SOURCE);

    // then
    assertThat(result).isFalse();
  }

  @Test
  @DisplayName("isCurrentUserFollowing(): should call FollowActionRepository if artist entity exists")
  void isCurrentUserFollowing_should_call_follow_action_repository() {
    ArtistEntity artistEntity = mock(ArtistEntity.class);
    doReturn(1L).when(artistEntity).getId();
    doReturn(PUBLIC_USER_ID).when(currentPublicUserIdSupplier).get();
    doReturn(Optional.of(userEntity)).when(userRepository).findByPublicId(any());
    doReturn(Optional.of(artistEntity)).when(artistRepository).findByExternalIdAndSource(any(), any());

    // when
    underTest.isCurrentUserFollowing(EXTERNAL_ID, ARTIST_SOURCE);

    // then
    verify(followActionRepository, times(1)).existsByUserIdAndArtistId(userEntity.getId(), artistEntity.getId());
  }

  @ParameterizedTest(name = "should return {0}")
  @ValueSource(booleans = {true, false})
  @DisplayName("isCurrentUserFollowing(): should return result from FollowActionRepository")
  void isCurrentUserFollowing_should_result_from_follow_action_repository(boolean existsByUserIdAndArtistId) {
    doReturn(PUBLIC_USER_ID).when(currentPublicUserIdSupplier).get();
    doReturn(Optional.of(userEntity)).when(userRepository).findByPublicId(any());
    doReturn(Optional.of(mock(ArtistEntity.class))).when(artistRepository).findByExternalIdAndSource(any(), any());
    doReturn(existsByUserIdAndArtistId).when(followActionRepository).existsByUserIdAndArtistId(any(), any());

    // when
    boolean result = underTest.isCurrentUserFollowing(EXTERNAL_ID, ARTIST_SOURCE);

    // then
    assertThat(result).isEqualTo(existsByUserIdAndArtistId);
  }

  @Test
  @DisplayName("Getting followed artists should get current user")
  void get_followed_should_call_user_supplier() {
    // given
    when(currentPublicUserIdSupplier.get()).thenReturn(PUBLIC_USER_ID);
    when(userRepository.findByPublicId(anyString())).thenReturn(Optional.of(userEntity));

    // when
    underTest.getFollowedArtistsOfCurrentUser();

    // then
    verify(currentPublicUserIdSupplier, times(1)).get();
    verify(userRepository, times(1)).findByPublicId(eq(PUBLIC_USER_ID));
  }

  @Test
  @DisplayName("Getting followed artists throws Exception when user not found")
  void get_followed_should_throw_exception() {
    // given
    when(currentPublicUserIdSupplier.get()).thenReturn(PUBLIC_USER_ID);
    when(userRepository.findByPublicId(anyString())).thenThrow(new ResourceNotFoundException(PUBLIC_USER_ID));

    // when
    Throwable throwable = catchThrowable(() -> underTest.getFollowedArtistsOfCurrentUser());

    // then
    assertThat(throwable).isInstanceOf(ResourceNotFoundException.class);
    assertThat(throwable).hasMessageContaining(PUBLIC_USER_ID);
  }

  @Test
  @DisplayName("Getting followed artists should call FollowActionRepository to fetch all follow actions")
  void get_followed_should_call_follow_action_repository() {
    // given
    when(currentPublicUserIdSupplier.get()).thenReturn(PUBLIC_USER_ID);
    when(userRepository.findByPublicId(anyString())).thenReturn(Optional.of(userEntity));

    // when
    underTest.getFollowedArtistsOfCurrentUser();

    // then
    verify(followActionRepository, times(1)).findAllByUser(eq(userEntity));
  }

  @Test
  @DisplayName("Getting followed artists calls artist transformer for every artist")
  void get_followed_should_call_artist_transformer() {
    // given
    ArtistEntity artist1 = mock(ArtistEntity.class);
    ArtistEntity artist2 = mock(ArtistEntity.class);
    when(currentPublicUserIdSupplier.get()).thenReturn(PUBLIC_USER_ID);
    when(userRepository.findByPublicId(anyString())).thenReturn(Optional.of(userEntity));
    when(followActionRepository.findAllByUser(any())).thenReturn(List.of(
            FollowActionEntity.builder().user(userEntity).artist(artist1).build(),
            FollowActionEntity.builder().user(userEntity).artist(artist2).build()
    ));
    when(artistTransformer.transform(any(ArtistEntity.class))).thenReturn(ArtistDtoFactory.createDefault());

    // when
    underTest.getFollowedArtistsOfCurrentUser();

    // then
    verify(artistTransformer, times(1)).transform(eq(artist1));
    verify(artistTransformer, times(1)).transform(eq(artist2));
  }

  @Test
  @DisplayName("Getting followed artists returns a sorted list of artist dtos")
  void get_followed_should_return_sorted_artist_dtos() {
    // given
    ArtistEntity artistEntity1 = mock(ArtistEntity.class);
    ArtistEntity artistEntity2 = mock(ArtistEntity.class);
    ArtistEntity artistEntity3 = mock(ArtistEntity.class);
    ArtistDto artistDto1 = ArtistDtoFactory.withName("Darkthrone");
    ArtistDto artistDto2 = ArtistDtoFactory.withName("Borknagar");
    ArtistDto artistDto3 = ArtistDtoFactory.withName("Alcest");

    when(userRepository.findByPublicId(any())).thenReturn(Optional.of(userEntity));
    when(followActionRepository.findAllByUser(any())).thenReturn(List.of(
            FollowActionEntity.builder().user(userEntity).artist(artistEntity1).build(),
            FollowActionEntity.builder().user(userEntity).artist(artistEntity2).build(),
            FollowActionEntity.builder().user(userEntity).artist(artistEntity3).build()
    ));
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
