package com.metalr2.service.artist;

import com.metalr2.model.artist.ArtistEntity;
import com.metalr2.model.artist.ArtistsRepository;
import com.metalr2.model.artist.FollowedArtistEntity;
import com.metalr2.model.artist.FollowedArtistsRepository;
import com.metalr2.model.user.UserEntity;
import com.metalr2.security.CurrentUserSupplier;
import com.metalr2.service.discogs.DiscogsArtistSearchRestClient;
import com.metalr2.web.dto.ArtistDto;
import com.metalr2.web.dto.response.ArtistDetailsResponse;
import com.metalr2.web.dto.response.ArtistNameSearchResponse;
import com.metalr2.web.dto.response.MyArtistsResponse;
import com.metalr2.web.dto.response.Pagination;
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

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static com.metalr2.web.DtoFactory.ArtistEntityFactory;
import static com.metalr2.web.DtoFactory.ArtistFactory;
import static com.metalr2.web.DtoFactory.DiscogsArtistSearchResultFactory;
import static com.metalr2.web.DtoFactory.FollowArtistFactory;
import static com.metalr2.web.dto.response.MyArtistsResponse.Artist;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
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
  private ArtistsRepository artistsRepository;

  @Mock
  private FollowedArtistsRepository followedArtistsRepository;

  @Mock
  private DiscogsArtistSearchRestClient artistSearchClient;

  @InjectMocks
  private ArtistsServiceImpl artistsService;

  private ArtistEntity artistEntity;
  private ArtistDto artistDto;

  @AfterEach
  void tearDown() {
    reset(artistSearchClient, followedArtistsRepository, artistsRepository);
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
      when(artistsRepository.findByArtistDiscogsId(DISCOGS_ID)).thenReturn(Optional.of(artistEntity));

      // when
      Optional<ArtistDto> artistOptional = artistsService.findArtistByDiscogsId(DISCOGS_ID);

      // then
      verify(artistsRepository, times(1)).findByArtistDiscogsId(DISCOGS_ID);

      assertThat(artistOptional).isPresent();
      assertThat(artistOptional.get()).isEqualTo(artistDto);
    }

    @Test
    @DisplayName("findArtistByDiscogsId() should return an empty optional if artist does not exist")
    void find_by_discogs_id_should_return_empty_optional() {
      // given
      when(artistsRepository.findByArtistDiscogsId(0L)).thenReturn(Optional.empty());

      // when
      Optional<ArtistDto> artistOptional = artistsService.findArtistByDiscogsId(0L);

      // then
      verify(artistsRepository, times(1)).findByArtistDiscogsId(0L);

      assertThat(artistOptional).isEmpty();
    }

    @Test
    @DisplayName("findAllByArtistDiscogsIdIn() should return all given entities that exist")
    void find_all_by_discogs_ids_should_return_all_entities_that_exist() {
      // given
      when(artistsRepository.findAllByArtistDiscogsIdIn(DISCOGS_ID, 0L)).thenReturn(List.of(artistEntity));

      // when
      List<ArtistDto> artists = artistsService.findAllArtistsByDiscogsIds(DISCOGS_ID, 0L);

      // then
      verify(artistsRepository, times(1)).findAllByArtistDiscogsIdIn(DISCOGS_ID, 0L);

      assertThat(artists).hasSize(1);
      assertThat(artists.get(0)).isEqualTo(artistDto);
    }

    @Test
    @DisplayName("existsArtistByDiscogsId() should return true if given entity exists")
    void exists_by_discogs_id_should_return_true() {
      // given
      when(artistsRepository.existsByArtistDiscogsId(DISCOGS_ID)).thenReturn(true);

      // when
      boolean exists = artistsService.existsArtistByDiscogsId(DISCOGS_ID);

      // then
      verify(artistsRepository, times(1)).existsByArtistDiscogsId(DISCOGS_ID);

      assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("existsArtistByDiscogsId() should return false if given entity does not exist")
    void exists_by_discogs_id_should_return_false() {
      // given
      when(artistsRepository.existsByArtistDiscogsId(0L)).thenReturn(false);

      // when
      boolean exists = artistsService.existsArtistByDiscogsId(0L);

      // then
      verify(artistsRepository, times(1)).existsByArtistDiscogsId(0L);

      assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("fetchAndSaveArtist() should return true if new artist is saved")
    void fetch_and_save_artist_should_return_true() {
      //given
      when(artistSearchClient.searchById(artistEntity.getArtistDiscogsId())).thenReturn(Optional.of(ArtistFactory.createTestArtist()));
      when(artistsRepository.save(any(ArtistEntity.class))).thenReturn(artistEntity);
      ArgumentCaptor<ArtistEntity> argumentCaptor = ArgumentCaptor.forClass(ArtistEntity.class);

      // when
      boolean saved = artistsService.fetchAndSaveArtist(artistEntity.getArtistDiscogsId());

      // then
      verify(artistsRepository, times(1)).existsByArtistDiscogsId(artistEntity.getArtistDiscogsId());
      verify(artistSearchClient, times(1)).searchById(artistEntity.getArtistDiscogsId());
      verify(artistsRepository, times(1)).save(argumentCaptor.capture());

      assertThat(saved).isTrue();

      ArtistEntity resultEntity = argumentCaptor.getValue();
      assertThat(resultEntity).isEqualTo(artistEntity);
    }

    @Test
    @DisplayName("fetchAndSaveArtist() should return true if artist already exists")
    void fetch_and_save_artist_should_return_true_for_existing() {
      //given
      when(artistsRepository.existsByArtistDiscogsId(artistEntity.getArtistDiscogsId())).thenReturn(true);

      // when
      boolean saved = artistsService.fetchAndSaveArtist(artistEntity.getArtistDiscogsId());

      // then
      verify(artistsRepository, times(1)).existsByArtistDiscogsId(artistEntity.getArtistDiscogsId());
      verify(artistSearchClient, times(0)).searchById(anyLong());
      verify(artistsRepository, times(0)).save(any(ArtistEntity.class));

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
      verify(artistsRepository, times(1)).existsByArtistDiscogsId(artistEntity.getArtistDiscogsId());
      verify(artistSearchClient, times(1)).searchById(artistEntity.getArtistDiscogsId());
      verify(artistsRepository, times(0)).save(any(ArtistEntity.class));

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
    void follow_artist_succeeds(){
      // given
      ArgumentCaptor<FollowedArtistEntity> followArtistEntityCaptor = ArgumentCaptor.forClass(FollowedArtistEntity.class);

      when(artistSearchClient.searchById(DISCOGS_ID)).thenReturn(Optional.of(ArtistFactory.createTestArtist()));
      when(followedArtistsRepository.save(any(FollowedArtistEntity.class))).thenReturn(new FollowedArtistEntity(USER_ID, DISCOGS_ID));
      when(currentUserSupplier.get()).thenReturn(userEntity);
      when(userEntity.getPublicId()).thenReturn(USER_ID);

      // when
      boolean result = artistsService.followArtist(DISCOGS_ID);

      // then
      assertThat(result).isTrue();

      verify(followedArtistsRepository, times(1)).save(followArtistEntityCaptor.capture());

      FollowedArtistEntity entity = followArtistEntityCaptor.getValue();

      assertThat(entity.getPublicUserId()).isEqualTo(USER_ID);
      assertThat(entity.getArtistDiscogsId()).isEqualTo(DISCOGS_ID);
    }

    @Test
    @DisplayName("Following an artist for a given user id can fail")
    void follow_artist_fails(){
      // given
      when(artistSearchClient.searchById(DISCOGS_ID)).thenReturn(Optional.empty());

      // when
      boolean result = artistsService.followArtist(DISCOGS_ID);

      // then
      assertThat(result).isFalse();
      verify(followedArtistsRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("Unfollowing a combination of artist and user which exist should return true")
    void unfollow_existing_artist_should_return_true(){
      // given
      when(followedArtistsRepository.findByPublicUserIdAndArtistDiscogsId(anyString(), anyLong())).thenReturn(Optional.of(new FollowedArtistEntity(USER_ID, DISCOGS_ID)));
      when(currentUserSupplier.get()).thenReturn(userEntity);
      when(userEntity.getPublicId()).thenReturn(USER_ID);

      // when
      boolean result = artistsService.unfollowArtist(DISCOGS_ID);

      // then
      assertThat(result).isTrue();

      verify(followedArtistsRepository, times(1)).findByPublicUserIdAndArtistDiscogsId(USER_ID, DISCOGS_ID);
      verify(followedArtistsRepository, times(1)).delete(new FollowedArtistEntity(USER_ID, DISCOGS_ID));
    }

    @Test
    @DisplayName("Unfollowing a combination of artist and user which do not exist should return false")
    void unfollow_not_existing_artist_should_return_false(){
      // given
      when(followedArtistsRepository.findByPublicUserIdAndArtistDiscogsId(anyString(), anyLong())).thenReturn(Optional.empty());
      when(currentUserSupplier.get()).thenReturn(userEntity);
      when(userEntity.getPublicId()).thenReturn(USER_ID);

      // when
      boolean result = artistsService.unfollowArtist(DISCOGS_ID);

      // then
      assertThat(result).isFalse();

      verify(followedArtistsRepository, times(1)).findByPublicUserIdAndArtistDiscogsId(USER_ID, DISCOGS_ID);
      verify(followedArtistsRepository, times(0)).delete(new FollowedArtistEntity(USER_ID, DISCOGS_ID));
    }

    @Test
    @DisplayName("isFollowed() should return true if the given combination from user id and artist discogs id exists")
    void is_followed_should_return_true_for_existing_entity(){
      // given
      when(followedArtistsRepository.existsByPublicUserIdAndArtistDiscogsId(anyString(), anyLong())).thenReturn(true);
      when(currentUserSupplier.get()).thenReturn(userEntity);
      when(userEntity.getPublicId()).thenReturn(USER_ID);

      // when
      boolean result = artistsService.isFollowed(DISCOGS_ID);

      // then
      assertThat(result).isTrue();
      verify(followedArtistsRepository, times(1)).existsByPublicUserIdAndArtistDiscogsId(USER_ID, DISCOGS_ID);
    }

    @Test
    @DisplayName("isFollowed() should return false if the given combination from user id and artist discogs id does not exist")
    void is_followed_should_return_false_for_not_existing_entity(){
      // given
      when(followedArtistsRepository.existsByPublicUserIdAndArtistDiscogsId(anyString(), anyLong())).thenReturn(false);
      when(currentUserSupplier.get()).thenReturn(userEntity);
      when(userEntity.getPublicId()).thenReturn(USER_ID);

      // when
      boolean result = artistsService.isFollowed(DISCOGS_ID);

      // then
      assertThat(result).isFalse();
      verify(followedArtistsRepository, times(1)).existsByPublicUserIdAndArtistDiscogsId(USER_ID, DISCOGS_ID);
    }

    @Test
    @DisplayName("findFollowedArtistsPerUser() finds the correct entities for a given user id if it exists")
    void find_per_user_finds_correct_entities(){
      // given
      when(followedArtistsRepository.findAllByPublicUserId(anyString())).thenReturn(Collections.singletonList(new FollowedArtistEntity(USER_ID, DISCOGS_ID)));
      when(artistsRepository.findAllByArtistDiscogsIdIn(any())).thenReturn(Collections.singletonList(new ArtistEntity(DISCOGS_ID, ARTIST_NAME, null)));

      // when
      MyArtistsResponse myArtists = artistsService.findFollowedArtistsPerUser(USER_ID);

      // then
      assertThat(myArtists.getMyArtists()).isNotEmpty();
      assertThat(myArtists.getMyArtists().get(0).getDiscogsId()).isEqualTo(DISCOGS_ID);

      verify(followedArtistsRepository, times(1)).findAllByPublicUserId(USER_ID);
    }

    @ParameterizedTest(name = "[{index}] => FollowedArtists <{0}> | Page <{1}> | Size <{2}> | Offset <{3}>")
    @MethodSource(value = "inputProviderPagination")
    @DisplayName("findFollowedArtistsPerUser() finds the correct entities with pagination for a given user id if it exists")
    void find_per_user_finds_correct_entities_pagination(int followedArtists, int page, int size, int offset){
      // given
      when(followedArtistsRepository.findAllByPublicUserId(USER_ID)).thenReturn(FollowArtistFactory.createFollowArtistEntities(followedArtists));
      when(artistsRepository.findAllByArtistDiscogsIdIn(any())).thenReturn(ArtistEntityFactory.createArtistEntities(size, offset));

      // when
      MyArtistsResponse myArtists = artistsService.findFollowedArtistsPerUser(USER_ID, page, size);

      // then
      assertThat(myArtists.getMyArtists()).isNotEmpty();

      List<Artist> myArtistEntities = myArtists.getMyArtists();

      for (int i = 0; i < myArtistEntities.size(); i++) {
        Artist artist = myArtistEntities.get(i);
        int expectedId = offset + i + 1;
        assertThat(artist.getDiscogsId()).isEqualTo(expectedId);
        assertThat(artist.getArtistName()).isEqualTo(String.valueOf(expectedId));
        assertThat(artist.getThumb()).isNull();
      }

      verify(followedArtistsRepository, times(1)).findAllByPublicUserId(USER_ID);
      verify(artistsRepository, times(1)).findAllByArtistDiscogsIdIn(any());
    }

    @ParameterizedTest(name = "[{index}] => FollowedArtists <{0}> | Page <{1}> | Size <{2}> | Offset <{3}> | PagesExpected <{4}>")
    @MethodSource(value = "inputProviderPagination")
    @DisplayName("findFollowedArtistsPerUser() finds the correct entities with pagination for a given user id if it exists")
    void find_per_user_finds_correct_entities_pagination_test(int followedArtists, int page, int size, int offset, int totalPagesExpected){
      // given
      when(followedArtistsRepository.findAllByPublicUserId(USER_ID)).thenReturn(FollowArtistFactory.createFollowArtistEntities(followedArtists));
      when(artistsRepository.findAllByArtistDiscogsIdIn(any())).thenReturn(ArtistEntityFactory.createArtistEntities(size, offset));

      // when
      MyArtistsResponse myArtists = artistsService.findFollowedArtistsPerUser(USER_ID, page, size);

      // then
      Pagination pagination = myArtists.getPagination();

      assertThat(pagination).isNotNull();
      assertThat(pagination.getCurrentPage()).isEqualTo(page);
      assertThat(pagination.getItemsPerPage()).isEqualTo(size);
      assertThat(pagination.getTotalPages()).isEqualTo(totalPagesExpected);

      verify(followedArtistsRepository, times(1)).findAllByPublicUserId(USER_ID);
      verify(artistsRepository, times(1)).findAllByArtistDiscogsIdIn(any());
    }

    private Stream<Arguments> inputProviderPagination() {
      return Stream.of(
          Arguments.of(6, 2, 3, 3, 2),
          Arguments.of(20,  10, 2, 10, 10)
      );
    }

    @Test
    @DisplayName("findFollowedArtistsPerUser() returns empty list if a given user id does not exists")
    void find_per_user_returns_empty_list(){
      // given
      when(followedArtistsRepository.findAllByPublicUserId(anyString())).thenReturn(Collections.emptyList());

      // when
      MyArtistsResponse myArtists = artistsService.findFollowedArtistsPerUser(USER_ID);

      // then
      assertThat(myArtists.getMyArtists()).isEmpty();
      verify(followedArtistsRepository, times(1)).findAllByPublicUserId(USER_ID);
    }

    @Test
    @DisplayName("findFollowedArtistsForCurrentUser() calls findFollowedArtistsPerUser() with current user id")
    void find_for_current_user() {
      // given
      when(currentUserSupplier.get()).thenReturn(userEntity);
      when(userEntity.getPublicId()).thenReturn(USER_ID);
      when(followedArtistsRepository.findAllByPublicUserId(USER_ID)).thenReturn(Collections.singletonList(new FollowedArtistEntity(USER_ID, DISCOGS_ID)));
      when(artistsRepository.findAllByArtistDiscogsIdIn(DISCOGS_ID)).thenReturn(Collections.singletonList(new ArtistEntity(DISCOGS_ID, ARTIST_NAME, null)));

      // when
      MyArtistsResponse myArtists = artistsService.findFollowedArtistsForCurrentUser();

      // then
      assertThat(myArtists.getMyArtists()).hasSize(1);
      assertThat(myArtists.getMyArtists().get(0).getDiscogsId()).isEqualTo(DISCOGS_ID);

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
    private static final int TOTAL_PAGES  = 2;

    @BeforeEach
    void setUp() {
    }

    @Test
    @DisplayName("searchDiscogsByName() returns retuns a valid result")
    void search_by_name_returns_valid_result(){
      // given
      when(artistSearchClient.searchByName(ARTIST_NAME, PAGE, SIZE)).thenReturn(Optional.of(DiscogsArtistSearchResultFactory.withOneCertainResult()));
      when(currentUserSupplier.get()).thenReturn(userEntity);
      when(userEntity.getPublicId()).thenReturn(USER_ID);

      // when
      Optional<ArtistNameSearchResponse> responseOptional = artistsService.searchDiscogsByName(ARTIST_NAME, PAGE, SIZE);

      // then
      assertThat(responseOptional).isPresent();

      ArtistNameSearchResponse response = responseOptional.get();

      assertThat(response.getArtistSearchResults()).isNotNull().hasSize(1);

      ArtistNameSearchResponse.ArtistSearchResult artistSearchResult = response.getArtistSearchResults().get(0);
      assertThat(artistSearchResult).isEqualTo(new ArtistNameSearchResponse.ArtistSearchResult(null, DISCOGS_ID, ARTIST_NAME, false));

      Pagination pagination = response.getPagination();
      assertThat(pagination).isEqualTo(new Pagination(TOTAL_PAGES, PAGE, SIZE));

      verify(artistSearchClient,times(1)).searchByName(ARTIST_NAME, PAGE, SIZE);
    }

    @Test
    @DisplayName("searchDiscogsByName() returns returns empty result")
    void search_by_name_returns_empy_result(){
      // given
      when(artistSearchClient.searchByName(anyString(), anyInt(), anyInt())).thenReturn(Optional.empty());

      //when
      Optional<ArtistNameSearchResponse> responseOptional = artistsService.searchDiscogsByName(ARTIST_NAME, PAGE, SIZE);

      // then
      assertThat(responseOptional).isEmpty();
      verify(artistSearchClient, times(1)).searchByName(ARTIST_NAME, PAGE, SIZE);
    }

    @Test
    @DisplayName("searchDiscogsById() returns returns a valid result")
    void search_by_id_returns_valid_result(){
      // given
      when(artistSearchClient.searchById(DISCOGS_ID)).thenReturn(Optional.of(ArtistFactory.createTestArtist()));
      when(currentUserSupplier.get()).thenReturn(userEntity);
      when(userEntity.getPublicId()).thenReturn(USER_ID);

      // when
      Optional<ArtistDetailsResponse> responseOptional = artistsService.searchDiscogsById(DISCOGS_ID);

      // then
      assertThat(responseOptional).isPresent();

      assertThat(responseOptional.get().getArtistId()).isEqualTo(DISCOGS_ID);
      verify(artistSearchClient, times(1)).searchById(DISCOGS_ID);
    }

    @Test
    @DisplayName("searchDiscogsById() returns empty result")
    void search_by_id_returns_empty_result(){
      // given
      when(artistSearchClient.searchById(anyLong())).thenReturn(Optional.empty());

      // when
      Optional<ArtistDetailsResponse> responseOptional = artistsService.searchDiscogsById(DISCOGS_ID);

      // then
      assertThat(responseOptional).isEmpty();
      verify(artistSearchClient, times(1)).searchById(DISCOGS_ID);
    }

  }

}
