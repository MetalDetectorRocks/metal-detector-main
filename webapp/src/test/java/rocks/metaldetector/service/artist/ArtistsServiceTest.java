package rocks.metaldetector.service.artist;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import rocks.metaldetector.discogs.domain.DiscogsArtistSearchRestClient;
import rocks.metaldetector.persistence.domain.artist.ArtistEntity;
import rocks.metaldetector.persistence.domain.artist.ArtistRepository;
import rocks.metaldetector.persistence.domain.artist.FollowedArtistEntity;
import rocks.metaldetector.persistence.domain.artist.FollowedArtistRepository;
import rocks.metaldetector.persistence.domain.user.UserEntity;
import rocks.metaldetector.security.CurrentUserSupplier;
import rocks.metaldetector.web.DtoFactory.DiscogsArtistFactory;
import rocks.metaldetector.web.dto.ArtistDto;
import rocks.metaldetector.web.dto.response.SearchResponse;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ArtistsServiceTest implements WithAssertions {

  private static final long DISCOGS_ID    = 252211L;
  private static final String ARTIST_NAME = "Darkthrone";
  private static final String USER_ID     = "TestId";

  @Mock
  private CurrentUserSupplier currentUserSupplier;

  @Mock
  private UserEntity userEntity;

  @Mock
  private ArtistRepository artistRepository;

  @Mock
  private FollowedArtistRepository followedArtistRepository;

  @Mock
  private DiscogsArtistSearchRestClient artistSearchClient;

  @InjectMocks
  private ArtistsServiceImpl artistsService;

  private ArtistEntity artistEntity;
  private ArtistDto artistDto;

  @AfterEach
  void tearDown() {
    reset(artistSearchClient, followedArtistRepository, artistRepository, currentUserSupplier, userEntity);
  }

  @Nested
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  @DisplayName("Testing artist repository")
  class ArtistRepositoryTest {

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
      Optional<ArtistDto> artistOptional = artistsService.findArtistByDiscogsId(DISCOGS_ID);

      // then
      verify(artistRepository, times(1)).findByArtistDiscogsId(DISCOGS_ID);

      assertThat(artistOptional).isPresent();
      assertThat(artistOptional.get()).isEqualTo(artistDto);
    }

    @Test
    @DisplayName("findArtistByDiscogsId() should return an empty optional if artist does not exist")
    void find_by_discogs_id_should_return_empty_optional() {
      // given
      when(artistRepository.findByArtistDiscogsId(0L)).thenReturn(Optional.empty());

      // when
      Optional<ArtistDto> artistOptional = artistsService.findArtistByDiscogsId(0L);

      // then
      verify(artistRepository, times(1)).findByArtistDiscogsId(0L);

      assertThat(artistOptional).isEmpty();
    }

    @Test
    @DisplayName("findAllByArtistDiscogsIdIn() should return all given entities that exist")
    void find_all_by_discogs_ids_should_return_all_entities_that_exist() {
      // given
      when(artistRepository.findAllByArtistDiscogsIdIn(DISCOGS_ID, 0L)).thenReturn(List.of(artistEntity));

      // when
      List<ArtistDto> artists = artistsService.findAllArtistsByDiscogsIds(DISCOGS_ID, 0L);

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
      boolean exists = artistsService.existsArtistByDiscogsId(DISCOGS_ID);

      // then
      verify(artistRepository, times(1)).existsByArtistDiscogsId(DISCOGS_ID);

      assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("existsArtistByDiscogsId() should return false if given entity does not exist")
    void exists_by_discogs_id_should_return_false() {
      // given
      when(artistRepository.existsByArtistDiscogsId(0L)).thenReturn(false);

      // when
      boolean exists = artistsService.existsArtistByDiscogsId(0L);

      // then
      verify(artistRepository, times(1)).existsByArtistDiscogsId(0L);

      assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("fetchAndSaveArtist() should return true if new artist is saved")
    void fetch_and_save_artist_should_return_true() {
      //given
      when(artistSearchClient.searchById(artistEntity.getArtistDiscogsId())).thenReturn(Optional.of(DiscogsArtistFactory.createTestArtist()));
      when(artistRepository.save(any(ArtistEntity.class))).thenReturn(artistEntity);
      ArgumentCaptor<ArtistEntity> argumentCaptor = ArgumentCaptor.forClass(ArtistEntity.class);

      // when
      boolean saved = artistsService.fetchAndSaveArtist(artistEntity.getArtistDiscogsId());

      // then
      verify(artistRepository, times(1)).existsByArtistDiscogsId(artistEntity.getArtistDiscogsId());
      verify(artistSearchClient, times(1)).searchById(artistEntity.getArtistDiscogsId());
      verify(artistRepository, times(1)).save(argumentCaptor.capture());

      assertThat(saved).isTrue();

      ArtistEntity resultEntity = argumentCaptor.getValue();
      assertThat(resultEntity).isEqualTo(artistEntity);
    }

    @Test
    @DisplayName("fetchAndSaveArtist() should return true if artist already exists")
    void fetch_and_save_artist_should_return_true_for_existing() {
      //given
      when(artistRepository.existsByArtistDiscogsId(artistEntity.getArtistDiscogsId())).thenReturn(true);

      // when
      boolean saved = artistsService.fetchAndSaveArtist(artistEntity.getArtistDiscogsId());

      // then
      verify(artistRepository, times(1)).existsByArtistDiscogsId(artistEntity.getArtistDiscogsId());
      verify(artistSearchClient, never()).searchById(anyLong());
      verify(artistRepository, never()).save(any(ArtistEntity.class));

      assertThat(saved).isTrue();
    }

    @Test
    @DisplayName("fetchAndSaveArtist() should return false if the artist is not found on discogs")
    void fetch_and_save_artist_should_return_false_if_not_found() {
      //given
      when(artistSearchClient.searchById(artistEntity.getArtistDiscogsId())).thenReturn(Optional.empty());

      // when
      boolean saved = artistsService.fetchAndSaveArtist(artistEntity.getArtistDiscogsId());

      // then
      verify(artistRepository, times(1)).existsByArtistDiscogsId(artistEntity.getArtistDiscogsId());
      verify(artistSearchClient, times(1)).searchById(artistEntity.getArtistDiscogsId());
      verify(artistRepository, never()).save(any(ArtistEntity.class));

      assertThat(saved).isFalse();
    }
  }

  @Nested
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  @DisplayName("Testing follow artist repository")
  class FollowRepositoryTest {

    @BeforeEach
    void setUp() {
    }

    @Test
    @DisplayName("Following an artist for a given user id should work")
    void follow_artist_succeeds() {
      // given
      ArgumentCaptor<FollowedArtistEntity> followArtistEntityCaptor = ArgumentCaptor.forClass(FollowedArtistEntity.class);

      when(artistSearchClient.searchById(DISCOGS_ID)).thenReturn(Optional.of(DiscogsArtistFactory.createTestArtist()));
      when(followedArtistRepository.save(any(FollowedArtistEntity.class))).thenReturn(new FollowedArtistEntity(USER_ID, DISCOGS_ID));
      when(currentUserSupplier.get()).thenReturn(userEntity);
      when(userEntity.getPublicId()).thenReturn(USER_ID);

      // when
      boolean result = artistsService.followArtist(DISCOGS_ID);

      // then
      assertThat(result).isTrue();

      verify(followedArtistRepository, times(1)).save(followArtistEntityCaptor.capture());

      FollowedArtistEntity entity = followArtistEntityCaptor.getValue();

      assertThat(entity.getPublicUserId()).isEqualTo(USER_ID);
      assertThat(entity.getDiscogsId()).isEqualTo(DISCOGS_ID);
    }

    @Test
    @DisplayName("Following an artist for a given user id can fail")
    void follow_artist_fails() {
      // given
      when(artistSearchClient.searchById(DISCOGS_ID)).thenReturn(Optional.empty());

      // when
      boolean result = artistsService.followArtist(DISCOGS_ID);

      // then
      assertThat(result).isFalse();
      verify(followedArtistRepository, never()).save(any());
    }

    @Test
    @DisplayName("Unfollowing a combination of artist and user which exist should return true")
    void unfollow_existing_artist_should_return_true() {
      // given
      when(followedArtistRepository.findByPublicUserIdAndDiscogsId(anyString(), anyLong())).thenReturn(Optional.of(new FollowedArtistEntity(USER_ID, DISCOGS_ID)));
      when(currentUserSupplier.get()).thenReturn(userEntity);
      when(userEntity.getPublicId()).thenReturn(USER_ID);

      // when
      boolean result = artistsService.unfollowArtist(DISCOGS_ID);

      // then
      assertThat(result).isTrue();

      verify(followedArtistRepository, times(1)).findByPublicUserIdAndDiscogsId(USER_ID, DISCOGS_ID);
      verify(followedArtistRepository, times(1)).delete(new FollowedArtistEntity(USER_ID, DISCOGS_ID));
    }

    @Test
    @DisplayName("Unfollowing a combination of artist and user which do not exist should return false")
    void unfollow_not_existing_artist_should_return_false() {
      // given
      when(followedArtistRepository.findByPublicUserIdAndDiscogsId(anyString(), anyLong())).thenReturn(Optional.empty());
      when(currentUserSupplier.get()).thenReturn(userEntity);
      when(userEntity.getPublicId()).thenReturn(USER_ID);

      // when
      boolean result = artistsService.unfollowArtist(DISCOGS_ID);

      // then
      assertThat(result).isFalse();

      verify(followedArtistRepository, times(1)).findByPublicUserIdAndDiscogsId(USER_ID, DISCOGS_ID);
      verify(followedArtistRepository, never()).delete(new FollowedArtistEntity(USER_ID, DISCOGS_ID));
    }

    @Test
    @DisplayName("isFollowed() should return true if the given combination from user id and artist discogs id exists")
    void is_followed_should_return_true_for_existing_entity() {
      // given
      when(followedArtistRepository.existsByPublicUserIdAndDiscogsId(anyString(), anyLong())).thenReturn(true);
      when(currentUserSupplier.get()).thenReturn(userEntity);
      when(userEntity.getPublicId()).thenReturn(USER_ID);

      // when
      boolean result = artistsService.isFollowed(DISCOGS_ID);

      // then
      assertThat(result).isTrue();
      verify(followedArtistRepository, times(1)).existsByPublicUserIdAndDiscogsId(USER_ID, DISCOGS_ID);
    }

    @Test
    @DisplayName("isFollowed() should return false if the given combination from user id and artist discogs id does not exist")
    void is_followed_should_return_false_for_not_existing_entity() {
      // given
      when(followedArtistRepository.existsByPublicUserIdAndDiscogsId(anyString(), anyLong())).thenReturn(false);
      when(currentUserSupplier.get()).thenReturn(userEntity);
      when(userEntity.getPublicId()).thenReturn(USER_ID);

      // when
      boolean result = artistsService.isFollowed(DISCOGS_ID);

      // then
      assertThat(result).isFalse();
      verify(followedArtistRepository, times(1)).existsByPublicUserIdAndDiscogsId(USER_ID, DISCOGS_ID);
    }

    @Test
    @DisplayName("findFollowedArtistsPerUser() finds the correct entities for a given user id if it exists")
    void find_per_user_finds_correct_entities() {
      // given
      when(followedArtistRepository.findByPublicUserId(anyString())).thenReturn(Collections.singletonList(new FollowedArtistEntity(USER_ID, DISCOGS_ID)));
      when(artistRepository.findAllByArtistDiscogsIdIn(any())).thenReturn(Collections.singletonList(new ArtistEntity(DISCOGS_ID, ARTIST_NAME, null)));

      // when
      List<ArtistDto> myArtists = artistsService.findFollowedArtistsPerUser(USER_ID);

      // then
      assertThat(myArtists).isNotEmpty();
      assertThat(myArtists.get(0).getDiscogsId()).isEqualTo(DISCOGS_ID);

      verify(followedArtistRepository, times(1)).findByPublicUserId(USER_ID);
    }

    @ParameterizedTest(name = "[{index}] => MockedFollowedArtists <{0}> | PageRequest<{1}> | Offset <{2}> | MockedEntities <{3}>")
    @MethodSource(value = "inputProviderPagination")
    @DisplayName("findFollowedArtistsPerUser() finds the correct entities with pagination for a given user id if it exists")
    void find_per_user_finds_correct_entities_pagination(List<FollowedArtistEntity> followedArtists, PageRequest pageRequest, int offset, List<ArtistEntity> mockedArtistEntities) {
      // given
      when(followedArtistRepository.findByPublicUserId(USER_ID, pageRequest)).thenReturn(followedArtists);
      when(artistRepository.findAllByArtistDiscogsIdIn(any())).thenReturn(mockedArtistEntities);

      // when
      List<ArtistDto> myArtists = artistsService.findFollowedArtistsPerUser(USER_ID, pageRequest);

      // then
      assertThat(myArtists).isNotEmpty();

      for (int i = 0; i < myArtists.size(); i++) {
        ArtistDto artist = myArtists.get(i);
        int expectedId = offset + i + 1;
        assertThat(artist.getDiscogsId()).isEqualTo(expectedId);
        assertThat(artist.getArtistName()).isEqualTo(String.valueOf(expectedId));
        assertThat(artist.getThumb()).isNull();
      }

      verify(followedArtistRepository, times(1)).findByPublicUserId(USER_ID, pageRequest);
      verify(artistRepository, times(1)).findAllByArtistDiscogsIdIn(any());
    }

    private Stream<Arguments> inputProviderPagination() {
      List<FollowedArtistEntity> mockedFollowedArtists1 = IntStream.range(1, 7).mapToObj(entity -> new FollowedArtistEntity(String.valueOf(entity), entity)).collect(Collectors.toList());
      List<FollowedArtistEntity> mockedFollowedArtists2 = IntStream.range(1, 21).mapToObj(entity -> new FollowedArtistEntity(String.valueOf(entity), entity)).collect(Collectors.toList());
      List<ArtistEntity> mockedArtistEntities1 = IntStream.range(4, 7).mapToObj(entity -> new ArtistEntity(entity, String.valueOf(entity), null)).collect(Collectors.toList());
      List<ArtistEntity> mockedArtistEntities2 = IntStream.range(11, 13).mapToObj(entity -> new ArtistEntity(entity, String.valueOf(entity), null)).collect(Collectors.toList());
      return Stream.of(
          Arguments.of(mockedFollowedArtists1, PageRequest.of(2, 3), 3, mockedArtistEntities1),
          Arguments.of(mockedFollowedArtists2, PageRequest.of(10, 2), 10, mockedArtistEntities2)
      );
    }

    @Test
    @DisplayName("findFollowedArtistsPerUser() returns empty list if a given user id does not exists")
    void find_per_user_returns_empty_list() {
      // given
      when(followedArtistRepository.findByPublicUserId(anyString())).thenReturn(Collections.emptyList());

      // when
      List<ArtistDto> myArtists = artistsService.findFollowedArtistsPerUser(USER_ID);

      // then
      assertThat(myArtists).isEmpty();
      verify(followedArtistRepository, times(1)).findByPublicUserId(USER_ID);
    }

    @Test
    @DisplayName("findFollowedArtistsForCurrentUser() calls findFollowedArtistsPerUser() with current user id")
    void find_for_current_user() {
      // given
      when(currentUserSupplier.get()).thenReturn(userEntity);
      when(userEntity.getPublicId()).thenReturn(USER_ID);
      when(followedArtistRepository.findByPublicUserId(USER_ID)).thenReturn(Collections.singletonList(new FollowedArtistEntity(USER_ID, DISCOGS_ID)));
      when(artistRepository.findAllByArtistDiscogsIdIn(DISCOGS_ID)).thenReturn(Collections.singletonList(new ArtistEntity(DISCOGS_ID, ARTIST_NAME, null)));

      // when
      List<ArtistDto> myArtists = artistsService.findFollowedArtistsForCurrentUser();

      // then
      assertThat(myArtists).hasSize(1);
      assertThat(myArtists.get(0).getDiscogsId()).isEqualTo(DISCOGS_ID);

      verify(currentUserSupplier, times(1)).get();
      String publicId = verify(userEntity, times(1)).getPublicId();
    }

    @Test
    @DisplayName("findFollowedArtistsForCurrentUser() calls findFollowedArtistsPerUser() with current user id and page request")
    void find_for_current_user_pageable() {
      // given
      PageRequest pageRequest = PageRequest.of(1, 1);
      when(currentUserSupplier.get()).thenReturn(userEntity);
      when(userEntity.getPublicId()).thenReturn(USER_ID);
      when(followedArtistRepository.findByPublicUserId(USER_ID, pageRequest)).thenReturn(Collections.singletonList(new FollowedArtistEntity(USER_ID, DISCOGS_ID)));
      when(artistRepository.findAllByArtistDiscogsIdIn(DISCOGS_ID)).thenReturn(Collections.singletonList(new ArtistEntity(DISCOGS_ID, ARTIST_NAME, null)));

      // when
      List<ArtistDto> myArtists = artistsService.findFollowedArtistsForCurrentUser(pageRequest);

      // then
      assertThat(myArtists).hasSize(1);
      assertThat(myArtists.get(0).getDiscogsId()).isEqualTo(DISCOGS_ID);

      verify(currentUserSupplier, times(1)).get();
      String publicId = verify(userEntity, times(1)).getPublicId();
    }

    @Test
    @DisplayName("countFollowedArtistsPerUser() counts correct number of entities")
    void count_per_user() {
      // given
      when(followedArtistRepository.countByPublicUserId(USER_ID)).thenReturn(10L);

      // when
      long numberOfEntities = artistsService.countFollowedArtistsPerUser(USER_ID);

      // then
      assertThat(numberOfEntities).isEqualTo(10);
      verify(followedArtistRepository, times(1)).countByPublicUserId(USER_ID);
    }

    @Test
    @DisplayName("countFollowedArtistsForCurrentUser() calls findFollowedArtistsPerUser() with current user id")
    void count_for_current_user() {
      // given
      when(followedArtistRepository.countByPublicUserId(USER_ID)).thenReturn(10L);
      when(currentUserSupplier.get()).thenReturn(userEntity);
      when(userEntity.getPublicId()).thenReturn(USER_ID);

      // when
      long numberOfEntities = artistsService.countFollowedArtistsForCurrentUser();

      // then
      assertThat(numberOfEntities).isEqualTo(10);
      verify(currentUserSupplier, times(1)).get();
      String publicId = verify(userEntity, times(1)).getPublicId();
    }
  }

  @Nested
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  @DisplayName("Testing name and id search")
  class SearchTest {

    private static final int PAGE         = 1;
    private static final int SIZE         = 10;
    private static final int TOTAL_PAGES  = 1;

    @BeforeEach
    void setUp() {
    }

    // ToDo DanielW: Repair tests
//    @Test
//    @DisplayName("searchDiscogsByName() returns retuns a valid result")
//    void search_by_name_returns_valid_result() {
//      // given
//      when(artistSearchClient.searchByName(ARTIST_NAME, PAGE, SIZE)).thenReturn(Optional.of(DiscogsArtistSearchResultFactory.withOneCertainResult()));
//      when(currentUserSupplier.get()).thenReturn(userEntity);
//      when(userEntity.getPublicId()).thenReturn(USER_ID);
//
//      // when
//      Optional<SearchResponse> responseOptional = artistsService.searchDiscogsByName(ARTIST_NAME, PageRequest.of(PAGE, SIZE));
//
//      // then
//      assertThat(responseOptional).isPresent();
//
//      SearchResponse response = responseOptional.get();
//
//      assertThat(response.getSearchResults()).isNotNull().hasSize(1);
//
//      SearchResponse.SearchResult searchResult = response.getSearchResults().get(0);
//      assertThat(searchResult).isEqualTo(new SearchResponse.SearchResult(null, DISCOGS_ID, ARTIST_NAME, false));
//
//      Pagination pagination = response.getPagination();
//      assertThat(pagination).isEqualTo(new Pagination(TOTAL_PAGES, PAGE - 1, SIZE));
//
//      verify(artistSearchClient, times(1)).searchByName(ARTIST_NAME, PAGE, SIZE);
//    }

    @Test
    @DisplayName("searchDiscogsByName() returns returns empty result")
    void search_by_name_returns_empy_result() {
      // given
      when(artistSearchClient.searchByName(ARTIST_NAME, PAGE, SIZE)).thenReturn(Optional.empty());

      //when
      Optional<SearchResponse> responseOptional = artistsService.searchDiscogsByName(ARTIST_NAME, PageRequest.of(PAGE, SIZE));

      // then
      assertThat(responseOptional).isEmpty();
      verify(artistSearchClient, times(1)).searchByName(ARTIST_NAME, PAGE, SIZE);
    }
  }
}
