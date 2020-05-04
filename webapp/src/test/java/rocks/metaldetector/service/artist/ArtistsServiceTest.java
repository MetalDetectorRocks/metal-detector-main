package rocks.metaldetector.service.artist;

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
import org.springframework.data.domain.PageRequest;
import rocks.metaldetector.discogs.facade.DiscogsService;
import rocks.metaldetector.discogs.facade.dto.DiscogsArtistSearchResultEntryDto;
import rocks.metaldetector.persistence.domain.artist.ArtistEntity;
import rocks.metaldetector.persistence.domain.artist.ArtistRepository;
import rocks.metaldetector.persistence.domain.user.UserEntity;
import rocks.metaldetector.security.CurrentUserSupplier;
import rocks.metaldetector.testutil.DtoFactory;
import rocks.metaldetector.testutil.DtoFactory.DiscogsArtistSearchResultDtoFactory;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ArtistsServiceTest implements WithAssertions {

  private static final long DISCOGS_ID    = 252211L;
  private static final String ARTIST_NAME = "Darkthrone";
  private static final String USER_ID     = "TestId";

  @Mock
  private CurrentUserSupplier currentUserSupplier;

  @Mock
  private UserEntity userEntityMock;

  @Mock
  private ArtistRepository artistRepository;

  @Mock
  private DiscogsService discogsService;

  @InjectMocks
  private ArtistsServiceImpl underTest;

  private ArtistEntity artistEntity;
  private ArtistDto artistDto;

  @AfterEach
  void tearDown() {
    reset(discogsService, artistRepository, currentUserSupplier);
  }

  @BeforeEach
  void setUp() {
    artistEntity = new ArtistEntity(DISCOGS_ID, ARTIST_NAME, null);
    artistDto    = new ArtistDto(DISCOGS_ID, ARTIST_NAME, null);
  }

  @Test
  @DisplayName("findArtistByDiscogsId() should return the correct artist if it exists")
  void find_by_discogs_id_should_return_correct_artist() {
    // given
    when(artistRepository.findByArtistDiscogsId(DISCOGS_ID)).thenReturn(Optional.of(artistEntity));

    // when
    Optional<ArtistDto> artistOptional = underTest.findArtistByDiscogsId(DISCOGS_ID);

    // then
    verify(artistRepository, times(1)).findByArtistDiscogsId(DISCOGS_ID);
    assertThat(artistOptional).isPresent();
    assertThat(artistOptional.get()).isEqualTo(artistDto);
  }

  @Test
  @DisplayName("findArtistByDiscogsId() should return an empty optional if artist does not exist")
  void find_by_discogs_id_should_return_empty_optional() {
    // given
    when(artistRepository.findByArtistDiscogsId(DISCOGS_ID)).thenReturn(Optional.empty());

    // when
    Optional<ArtistDto> artistOptional = underTest.findArtistByDiscogsId(DISCOGS_ID);

    // then
    verify(artistRepository, times(1)).findByArtistDiscogsId(DISCOGS_ID);
    assertThat(artistOptional).isEmpty();
  }

  @Test
  @DisplayName("findAllByArtistDiscogsIdIn() should return all given entities that exist")
  void find_all_by_discogs_ids_should_return_all_entities_that_exist() {
    // given
    when(artistRepository.findAllByArtistDiscogsIdIn(DISCOGS_ID, 0L)).thenReturn(List.of(artistEntity));

    // when
    List<ArtistDto> artists = underTest.findAllArtistsByDiscogsIds(DISCOGS_ID, 0L);

    // then
    verify(artistRepository, times(1)).findAllByArtistDiscogsIdIn(DISCOGS_ID, 0L);
    assertThat(artists).hasSize(1);
    assertThat(artists.get(0)).isEqualTo(artistDto);
  }

  @Test
  @DisplayName("existsArtistByDiscogsId() should return true if given entity exists")
  void exists_by_discogs_id_should_return_true() {
    // given
    when(artistRepository.existsByArtistDiscogsId(DISCOGS_ID)).thenReturn(true);

    // when
    boolean exists = underTest.existsArtistByDiscogsId(DISCOGS_ID);

    // then
    verify(artistRepository, times(1)).existsByArtistDiscogsId(DISCOGS_ID);
    assertThat(exists).isTrue();
  }

  @Test
  @DisplayName("existsArtistByDiscogsId() should return false if given entity does not exist")
  void exists_by_discogs_id_should_return_false() {
    // given
    when(artistRepository.existsByArtistDiscogsId(DISCOGS_ID)).thenReturn(false);

    // when
    boolean exists = underTest.existsArtistByDiscogsId(DISCOGS_ID);

    // then
    verify(artistRepository, times(1)).existsByArtistDiscogsId(DISCOGS_ID);
    assertThat(exists).isFalse();
  }

  @Test
  @DisplayName("fetchAndSaveArtist() should call dicogs service with provided discogs id if artist is not already saved")
  void fetchAndSaveArtist_should_call_discogs_service() {
    // given
    var artistDto = DtoFactory.DiscogsArtistDtoFactory.createDefault();
    doReturn(false).when(artistRepository).existsByArtistDiscogsId(DISCOGS_ID);
    doReturn(artistDto).when(discogsService).searchArtistById(DISCOGS_ID);

    // when
    underTest.fetchAndSaveArtist(DISCOGS_ID);

    // then
    InOrder inOrderVerifier = inOrder(artistRepository, discogsService);
    inOrderVerifier.verify(artistRepository, times(1)).existsByArtistDiscogsId(DISCOGS_ID);
    inOrderVerifier.verify(discogsService, times(1)).searchArtistById(DISCOGS_ID);
  }

  @Test
  @DisplayName("fetchAndSaveArtist() should not call dicogs service and save artist if artist is already saved")
  void fetchAndSaveArtist_should_not_call_services() {
    // given
    doReturn(true).when(artistRepository).existsByArtistDiscogsId(DISCOGS_ID);

    // when
    underTest.fetchAndSaveArtist(DISCOGS_ID);

    // then
    verify(artistRepository, times(1)).existsByArtistDiscogsId(DISCOGS_ID);
    verifyNoMoreInteractions(discogsService);
    verifyNoMoreInteractions(artistRepository);
  }

  @Test
  @DisplayName("fetchAndSaveArtist() should save artist entity after retrieving artist dto from discogs service")
  void fetchAndSaveArtist_should_save_artist_entity() {
    // given
    var artistDto = DtoFactory.DiscogsArtistDtoFactory.createDefault();
    doReturn(false).when(artistRepository).existsByArtistDiscogsId(DISCOGS_ID);
    doReturn(artistDto).when(discogsService).searchArtistById(DISCOGS_ID);
    ArgumentCaptor<ArtistEntity> argumentCaptor = ArgumentCaptor.forClass(ArtistEntity.class);

    // when
    underTest.fetchAndSaveArtist(DISCOGS_ID);

    // then
    InOrder inOrderVerifier = inOrder(discogsService, artistRepository);
    inOrderVerifier.verify(discogsService, times(1)).searchArtistById(DISCOGS_ID);
    inOrderVerifier.verify(artistRepository, times(1)).save(argumentCaptor.capture());

    assertThat(argumentCaptor.getValue().getArtistDiscogsId()).isEqualTo(artistDto.getId());
    assertThat(argumentCaptor.getValue().getArtistName()).isEqualTo(artistDto.getName());
    assertThat(argumentCaptor.getValue().getThumb()).isEqualTo(artistDto.getImageUrl());
  }

  // TODO: 04.05.20 Tests verschieben 
//  @Test
//  @DisplayName("No follow entity should be saved if the artist is already followed")
//  void followArtist_already_followed_artist() {
//    // given
//    doReturn(true).when(followedArtistRepository).existsByPublicUserIdAndDiscogsId(USER_ID, DISCOGS_ID);
//    doReturn(USER_ID).when(userEntityMock).getPublicId();
//    doReturn(userEntityMock).when(currentUserSupplier).get();
//
//    // when
//    underTest.followArtist(DISCOGS_ID);
//
//    // then
//    verify(followedArtistRepository, times(1)).existsByPublicUserIdAndDiscogsId(USER_ID, DISCOGS_ID);
//    verifyNoMoreInteractions(followedArtistRepository);
//  }
//
//  @Test
//  @DisplayName("Only an artist not already followed can be followed")
//  void followArtist_not_already_followed_artist() {
//    // given
//    ArgumentCaptor<FollowedArtistEntity> argumentCaptor = ArgumentCaptor.forClass(FollowedArtistEntity.class);
//    doReturn(false).when(followedArtistRepository).existsByPublicUserIdAndDiscogsId(USER_ID, DISCOGS_ID);
//    doReturn(true).when(artistRepository).existsByArtistDiscogsId(DISCOGS_ID);
//    doReturn(USER_ID).when(userEntityMock).getPublicId();
//    doReturn(userEntityMock).when(currentUserSupplier).get();
//
//    // when
//    underTest.followArtist(DISCOGS_ID);
//
//    // then
//    verify(followedArtistRepository, times(1)).save(argumentCaptor.capture());
//    assertThat(argumentCaptor.getValue().getPublicUserId()).isEqualTo(userEntityMock.getPublicId());
//    assertThat(argumentCaptor.getValue().getDiscogsId()).isEqualTo(DISCOGS_ID);
//  }
//
//  @Test
//  @DisplayName("Should not delete the follow entity if artist is not followed")
//  void unfollow_not_followed_artist() {
//    // given
//    doReturn(false).when(followedArtistRepository).existsByPublicUserIdAndDiscogsId(USER_ID, DISCOGS_ID);
//    doReturn(USER_ID).when(userEntityMock).getPublicId();
//    doReturn(userEntityMock).when(currentUserSupplier).get();
//
//    // when
//    underTest.unfollowArtist(DISCOGS_ID);
//
//    // then
//    verify(followedArtistRepository, times(1)).existsByPublicUserIdAndDiscogsId(USER_ID, DISCOGS_ID);
//    verifyNoMoreInteractions(followedArtistRepository);
//  }
//
//  @Test
//  @DisplayName("Should delete the follow entity if artist is followed")
//  void unfollow_existing_artist_should_return_true() {
//    // given
//    doReturn(true).when(followedArtistRepository).existsByPublicUserIdAndDiscogsId(USER_ID, DISCOGS_ID);
//    doReturn(USER_ID).when(userEntityMock).getPublicId();
//    doReturn(userEntityMock).when(currentUserSupplier).get();
//
//    // when
//    underTest.unfollowArtist(DISCOGS_ID);
//
//    // then
//    verify(followedArtistRepository, times(1)).deleteByPublicUserIdAndDiscogsId(USER_ID, DISCOGS_ID);
//  }
//
//  @Test
//  @DisplayName("isFollowedByCurrentUser() should return true if the given combination from user id and artist discogs id exists")
//  void is_followed_should_return_true_for_existing_entity() {
//    // given
//    when(followedArtistRepository.existsByPublicUserIdAndDiscogsId(anyString(), anyLong())).thenReturn(true);
//    doReturn(USER_ID).when(userEntityMock).getPublicId();
//    doReturn(userEntityMock).when(currentUserSupplier).get();
//
//    // when
//    boolean result = underTest.isFollowedByCurrentUser(DISCOGS_ID);
//
//    // then
//    assertThat(result).isTrue();
//    verify(followedArtistRepository, times(1)).existsByPublicUserIdAndDiscogsId(USER_ID, DISCOGS_ID);
//  }
//
//  @Test
//  @DisplayName("isFollowedByCurrentUser() should return false if the given combination from user id and artist discogs id does not exist")
//  void is_followed_should_return_false_for_not_existing_entity() {
//    // given
//    when(followedArtistRepository.existsByPublicUserIdAndDiscogsId(anyString(), anyLong())).thenReturn(false);
//    doReturn(USER_ID).when(userEntityMock).getPublicId();
//    doReturn(userEntityMock).when(currentUserSupplier).get();
//
//    // when
//    boolean result = underTest.isFollowedByCurrentUser(DISCOGS_ID);
//
//    // then
//    assertThat(result).isFalse();
//    verify(followedArtistRepository, times(1)).existsByPublicUserIdAndDiscogsId(USER_ID, DISCOGS_ID);
//  }
//
//  @Test
//  @DisplayName("findFollowedArtistsPerUser() finds the correct entities for a given user id if it exists")
//  void find_per_user_finds_correct_entities() {
//    // given
//    when(followedArtistRepository.findByPublicUserId(anyString())).thenReturn(Collections.singletonList(new FollowedArtistEntity(USER_ID, DISCOGS_ID)));
//    when(artistRepository.findAllByArtistDiscogsIdIn(any())).thenReturn(Collections.singletonList(new ArtistEntity(DISCOGS_ID, ARTIST_NAME, null)));
//
//    // when
//    List<ArtistDto> myArtists = underTest.findFollowedArtistsPerUser(USER_ID);
//
//    // then
//    assertThat(myArtists).isNotEmpty();
//    assertThat(myArtists.get(0).getDiscogsId()).isEqualTo(DISCOGS_ID);
//    verify(followedArtistRepository, times(1)).findByPublicUserId(USER_ID);
//  }
//
//  @ParameterizedTest(name = "[{index}] => MockedFollowedArtists <{0}> | PageRequest<{1}> | Offset <{2}> | MockedEntities <{3}>")
//  @MethodSource(value = "inputProviderPagination")
//  @DisplayName("findFollowedArtistsPerUser() finds the correct entities with pagination for a given user id if it exists")
//  void find_per_user_finds_correct_entities_pagination(List<FollowedArtistEntity> followedArtists, PageRequest pageRequest, int offset, List<ArtistEntity> mockedArtistEntities) {
//    // given
//    when(followedArtistRepository.findByPublicUserId(USER_ID, pageRequest)).thenReturn(followedArtists);
//    when(artistRepository.findAllByArtistDiscogsIdIn(any())).thenReturn(mockedArtistEntities);
//
//    // when
//    List<ArtistDto> myArtists = underTest.findFollowedArtistsPerUser(USER_ID, pageRequest);
//
//    // then
//    assertThat(myArtists).isNotEmpty();
//
//    for (int i = 0; i < myArtists.size(); i++) {
//      ArtistDto artist = myArtists.get(i);
//      int expectedId = offset + i + 1;
//      assertThat(artist.getDiscogsId()).isEqualTo(expectedId);
//      assertThat(artist.getArtistName()).isEqualTo(String.valueOf(expectedId));
//      assertThat(artist.getThumb()).isNull();
//    }
//
//    verify(followedArtistRepository, times(1)).findByPublicUserId(USER_ID, pageRequest);
//    verify(artistRepository, times(1)).findAllByArtistDiscogsIdIn(any());
//  }
//
//  private static Stream<Arguments> inputProviderPagination() {
//    List<FollowedArtistEntity> mockedFollowedArtists1 = IntStream.range(1, 7).mapToObj(entity -> new FollowedArtistEntity(String.valueOf(entity), entity)).collect(Collectors.toList());
//    List<FollowedArtistEntity> mockedFollowedArtists2 = IntStream.range(1, 21).mapToObj(entity -> new FollowedArtistEntity(String.valueOf(entity), entity)).collect(Collectors.toList());
//    List<ArtistEntity> mockedArtistEntities1 = IntStream.range(4, 7).mapToObj(entity -> new ArtistEntity(entity, String.valueOf(entity), null)).collect(Collectors.toList());
//    List<ArtistEntity> mockedArtistEntities2 = IntStream.range(11, 13).mapToObj(entity -> new ArtistEntity(entity, String.valueOf(entity), null)).collect(Collectors.toList());
//    return Stream.of(
//            Arguments.of(mockedFollowedArtists1, PageRequest.of(2, 3), 3, mockedArtistEntities1),
//            Arguments.of(mockedFollowedArtists2, PageRequest.of(10, 2), 10, mockedArtistEntities2)
//    );
//  }
//
//  @Test
//  @DisplayName("findFollowedArtistsPerUser() returns empty list if a given user id does not exists")
//  void find_per_user_returns_empty_list() {
//    // given
//    when(followedArtistRepository.findByPublicUserId(anyString())).thenReturn(Collections.emptyList());
//
//    // when
//    List<ArtistDto> myArtists = underTest.findFollowedArtistsPerUser(USER_ID);
//
//    // then
//    assertThat(myArtists).isEmpty();
//    verify(followedArtistRepository, times(1)).findByPublicUserId(USER_ID);
//  }
//
//  @Test
//  @DisplayName("findFollowedArtistsForCurrentUser() calls findFollowedArtistsPerUser() with current user id")
//  void find_for_current_user() {
//    // given
//    when(followedArtistRepository.findByPublicUserId(USER_ID)).thenReturn(Collections.singletonList(new FollowedArtistEntity(USER_ID, DISCOGS_ID)));
//    when(artistRepository.findAllByArtistDiscogsIdIn(DISCOGS_ID)).thenReturn(Collections.singletonList(new ArtistEntity(DISCOGS_ID, ARTIST_NAME, null)));
//    doReturn(USER_ID).when(userEntityMock).getPublicId();
//    doReturn(userEntityMock).when(currentUserSupplier).get();
//
//    // when
//    List<ArtistDto> myArtists = underTest.findFollowedArtistsForCurrentUser();
//
//    // then
//    assertThat(myArtists).hasSize(1);
//    assertThat(myArtists.get(0).getDiscogsId()).isEqualTo(DISCOGS_ID);
//    verify(currentUserSupplier, times(1)).get();
//  }
//
//  @Test
//  @DisplayName("findFollowedArtistsForCurrentUser() calls findFollowedArtistsPerUser() with current user id and page request")
//  void find_for_current_user_pageable() {
//    // given
//    PageRequest pageRequest = PageRequest.of(1, 1);
//    when(followedArtistRepository.findByPublicUserId(USER_ID, pageRequest)).thenReturn(Collections.singletonList(new FollowedArtistEntity(USER_ID, DISCOGS_ID)));
//    when(artistRepository.findAllByArtistDiscogsIdIn(DISCOGS_ID)).thenReturn(Collections.singletonList(new ArtistEntity(DISCOGS_ID, ARTIST_NAME, null)));
//    doReturn(USER_ID).when(userEntityMock).getPublicId();
//    doReturn(userEntityMock).when(currentUserSupplier).get();
//
//    // when
//    List<ArtistDto> myArtists = underTest.findFollowedArtistsForCurrentUser(pageRequest);
//
//    // then
//    assertThat(myArtists).hasSize(1);
//    assertThat(myArtists.get(0).getDiscogsId()).isEqualTo(DISCOGS_ID);
//
//    verify(currentUserSupplier, times(1)).get();
//  }
//
//  @Test
//  @DisplayName("countFollowedArtistsPerUser() counts correct number of entities")
//  void count_per_user() {
//    // given
//    when(followedArtistRepository.countByPublicUserId(USER_ID)).thenReturn(10L);
//
//    // when
//    long numberOfEntities = underTest.countFollowedArtistsPerUser(USER_ID);
//
//    // then
//    assertThat(numberOfEntities).isEqualTo(10);
//    verify(followedArtistRepository, times(1)).countByPublicUserId(USER_ID);
//  }
//
//  @Test
//  @DisplayName("countFollowedArtistsForCurrentUser() calls findFollowedArtistsPerUser() with current user id")
//  void count_for_current_user() {
//    // given
//    when(followedArtistRepository.countByPublicUserId(USER_ID)).thenReturn(10L);
//    doReturn(USER_ID).when(userEntityMock).getPublicId();
//    doReturn(userEntityMock).when(currentUserSupplier).get();
//
//    // when
//    long numberOfEntities = underTest.countFollowedArtistsForCurrentUser();
//
//    // then
//    assertThat(numberOfEntities).isEqualTo(10);
//    verify(currentUserSupplier, times(1)).get();
//  }

  @Test
  @DisplayName("Should pass provided arguments to discogs service")
  void searchDiscogsByName_should_pass_arguments() {
    // given
    var artistQueryString = "the query";
    var pageable = PageRequest.of(1, 10);
    doReturn(USER_ID).when(userEntityMock).getPublicId();
    doReturn(userEntityMock).when(currentUserSupplier).get();
    doReturn(DiscogsArtistSearchResultDtoFactory.createDefault()).when(discogsService).searchArtistByName(any(), anyInt(), anyInt());

    // when
    underTest.searchDiscogsByName(artistQueryString, pageable);

    // then
    verify(discogsService, times(1)).searchArtistByName(artistQueryString, pageable.getPageNumber(), pageable.getPageSize());
  }

  @Test
  @DisplayName("Should return search results from discogs")
  void searchDiscogsByName_should_return_search_results() {
    // given
    var expectedSearchResults = DiscogsArtistSearchResultDtoFactory.createDefault();
    doReturn(USER_ID).when(userEntityMock).getPublicId();
    doReturn(userEntityMock).when(currentUserSupplier).get();
    doReturn(expectedSearchResults).when(discogsService).searchArtistByName(any(), anyInt(), anyInt());

    // when
    var searchResults = underTest.searchDiscogsByName("the query", PageRequest.of(1, 10));

    // then
    assertThat(searchResults).isEqualTo(expectedSearchResults);
  }

  @Test
  @DisplayName("Should mark all already followed artists")
  void searchDiscogsByName_should_mark_already_followed_artists() {
    // given
    var discogsSearchResults = DiscogsArtistSearchResultDtoFactory.createDefault();
    discogsSearchResults.setSearchResults(createListOfSearchResultEntries(1, 2, 3));
    doReturn(USER_ID).when(userEntityMock).getPublicId();
    doReturn(userEntityMock).when(currentUserSupplier).get();
    doReturn(discogsSearchResults).when(discogsService).searchArtistByName(any(), anyInt(), anyInt());
    doReturn(createListOfArtistEntities(1, 3)).when(artistRepository).findAllByArtistDiscogsIdIn(any());

    // when
    var searchResults = underTest.searchDiscogsByName("the query", PageRequest.of(1, 10));

    // then
    assertThat(searchResults.getSearchResults().get(0).isFollowed()).isTrue();
    assertThat(searchResults.getSearchResults().get(1).isFollowed()).isFalse();
    assertThat(searchResults.getSearchResults().get(2).isFollowed()).isTrue();
  }

  private List<DiscogsArtistSearchResultEntryDto> createListOfSearchResultEntries(long... artistIds) {
    return List.of(
            DtoFactory.DiscogsArtistSearchResultEntryDtoFactory.withId(artistIds[0]),
            DtoFactory.DiscogsArtistSearchResultEntryDtoFactory.withId(artistIds[1]),
            DtoFactory.DiscogsArtistSearchResultEntryDtoFactory.withId(artistIds[2])
    );
  }

  private List<ArtistEntity> createListOfArtistEntities(long... artistIds) {
    return List.of(
            ArtistFactory.withDiscogsId(artistIds[0]),
            ArtistFactory.withDiscogsId(artistIds[1])
    );
  }
}
