package rocks.metaldetector.service.summary;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.metaldetector.persistence.domain.artist.ArtistEntity;
import rocks.metaldetector.persistence.domain.artist.ArtistRepository;
import rocks.metaldetector.persistence.domain.artist.FollowActionEntity;
import rocks.metaldetector.persistence.domain.artist.FollowActionRepository;
import rocks.metaldetector.persistence.domain.artist.TopArtist;
import rocks.metaldetector.persistence.domain.user.UserEntity;
import rocks.metaldetector.persistence.domain.user.UserRepository;
import rocks.metaldetector.security.CurrentPublicUserIdSupplier;
import rocks.metaldetector.service.artist.ArtistEntityFactory;
import rocks.metaldetector.service.artist.ArtistTransformer;
import rocks.metaldetector.service.user.UserEntityFactory;
import rocks.metaldetector.support.exceptions.ResourceNotFoundException;
import rocks.metaldetector.testutil.DtoFactory.ArtistDtoFactory;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static rocks.metaldetector.service.summary.SummaryServiceImpl.RESULT_LIMIT;

@ExtendWith(MockitoExtension.class)
class ArtistCollectorTest implements WithAssertions {

  @Mock
  private ArtistRepository artistRepository;

  @Mock
  private ArtistTransformer artistTransformer;

  @Mock
  private CurrentPublicUserIdSupplier currentPublicUserIdSupplier;

  @Mock
  private UserRepository userRepository;

  @Mock
  private FollowActionRepository followActionRepository;

  @InjectMocks
  private ArtistCollector underTest;

  private final UserEntity userEntity = UserEntityFactory.createUser("user", "user@mail.com");

  @AfterEach
  void tearDown() {
    reset(artistRepository, artistTransformer, currentPublicUserIdSupplier, userRepository, followActionRepository);
  }

  @Test
  @DisplayName("collectTopFollowedArtists: artistRepository is called to get top artists")
  void test_artist_repository_is_called_for_top_artists() {
    // when
    underTest.collectTopFollowedArtists();

    // then
    verify(artistRepository, times(1)).findTopArtists(RESULT_LIMIT);
  }

  @Test
  @DisplayName("collectTopFollowedArtists: artistTransformer is called for each artist")
  void test_artist_transformer_is_called_for_artists() {
    // given
    var topArtists = List.of(mock(TopArtist.class), mock(TopArtist.class));
    doReturn(topArtists).when(artistRepository).findTopArtists(anyInt());

    // when
    underTest.collectTopFollowedArtists();

    // then
    verify(artistTransformer, times(1)).transform(topArtists.get(0));
    verify(artistTransformer, times(1)).transform(topArtists.get(1));
  }

  @Test
  @DisplayName("collectTopFollowedArtists: artist dtos are returned")
  void test_artist_dtos_are_returned() {
    // given
    var artistEntities = List.of(mock(TopArtist.class), mock(TopArtist.class));
    doReturn(artistEntities).when(artistRepository).findTopArtists(anyInt());
    var expectedArtistDtos = List.of(ArtistDtoFactory.withName("A"), ArtistDtoFactory.withName("B"));
    doReturn(expectedArtistDtos.get(0)).when(artistTransformer).transform(artistEntities.get(0));
    doReturn(expectedArtistDtos.get(1)).when(artistTransformer).transform(artistEntities.get(1));

    // when
    var result = underTest.collectTopFollowedArtists();

    // then
    assertThat(result).isEqualTo(expectedArtistDtos);
  }

  @Test
  @DisplayName("collectRecentlyFollowedArtists: currentPublicUserIdSupplier is called")
  void test_current_user_id_supplier_called() {
    // given
    doReturn(Optional.of(userEntity)).when(userRepository).findByPublicId(any());

    // when
    underTest.collectRecentlyFollowedArtists();

    // then
    verify(currentPublicUserIdSupplier, times(1)).get();
  }

  @Test
  @DisplayName("collectTopFollowedArtists: userRepository is called")
  void test_user_repository_called() {
    // given
    var userId = "userId";
    doReturn(userId).when(currentPublicUserIdSupplier).get();
    doReturn(Optional.of(userEntity)).when(userRepository).findByPublicId(any());

    // when
    underTest.collectRecentlyFollowedArtists();

    // then
    verify(userRepository, times(1)).findByPublicId(userId);
  }

  @Test
  @DisplayName("collectTopFollowedArtists: userRepository throws exception when publicUserId not found")
  void test_user_repository_throws_exception() {
    // given
    var publicUserId = "publicUserId";
    doThrow(new ResourceNotFoundException(publicUserId)).when(userRepository).findByPublicId(publicUserId);

    // when
    Throwable throwable = catchThrowable(() -> underTest.collectRecentlyFollowedArtists());

    // then
    assertThat(throwable).isInstanceOf(ResourceNotFoundException.class);
    assertThat(throwable).hasMessageContaining(publicUserId);
  }

  @Test
  @DisplayName("collectTopFollowedArtists: followActionRepository is called with user")
  void test_follow_action_repository_called() {
    // given
    doReturn(Optional.of(userEntity)).when(userRepository).findByPublicId(any());

    // when
    underTest.collectRecentlyFollowedArtists();

    // then
    verify(followActionRepository, times(1)).findAllByUser(userEntity);
  }

  @Test
  @DisplayName("collectTopFollowedArtists: artistTransformer is called for every followActionEntity")
  void test_artist_transformer_called() {
    // given
    ArtistEntity artist1 = ArtistEntityFactory.withExternalId("1");
    ArtistEntity artist2 = ArtistEntityFactory.withExternalId("2");
    FollowActionEntity userFollowsArtist1 = FollowActionEntity.builder().user(userEntity).artist(artist1).build();
    FollowActionEntity userFollowsArtist2 = FollowActionEntity.builder().user(userEntity).artist(artist2).build();
    userFollowsArtist1.setCreatedDateTime(Date.from(Instant.now()));
    userFollowsArtist2.setCreatedDateTime(Date.from(Instant.now()));
    var followActionEntities = List.of(userFollowsArtist1, userFollowsArtist2);
    doReturn(Optional.of(userEntity)).when(userRepository).findByPublicId(any());
    doReturn(followActionEntities).when(followActionRepository).findAllByUser(any());
    doReturn(ArtistDtoFactory.createDefault()).when(artistTransformer).transform(userFollowsArtist1);
    doReturn(ArtistDtoFactory.createDefault()).when(artistTransformer).transform(userFollowsArtist2);

    // when
    underTest.collectRecentlyFollowedArtists();

    // then
    verify(artistTransformer, times(1)).transform(eq(userFollowsArtist1));
    verify(artistTransformer, times(1)).transform(eq(userFollowsArtist2));
  }
}
