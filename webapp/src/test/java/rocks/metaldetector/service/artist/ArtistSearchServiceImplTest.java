package rocks.metaldetector.service.artist;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import rocks.metaldetector.discogs.facade.DiscogsService;
import rocks.metaldetector.persistence.domain.user.UserEntity;
import rocks.metaldetector.persistence.domain.user.UserRepository;
import rocks.metaldetector.security.CurrentPublicUserIdSupplier;
import rocks.metaldetector.spotify.facade.SpotifyService;
import rocks.metaldetector.support.exceptions.ResourceNotFoundException;
import rocks.metaldetector.testutil.DtoFactory;
import rocks.metaldetector.web.api.response.ArtistSearchResponse;
import rocks.metaldetector.web.api.response.ArtistSearchResponseEntryDto;
import rocks.metaldetector.web.transformer.ArtistSearchResponseTransformer;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ArtistSearchServiceImplTest implements WithAssertions {

    @Mock
    private CurrentPublicUserIdSupplier currentPublicUserIdSupplier;

    @Mock
    private UserEntity userEntityMock;

    @Mock
    private UserRepository userRepository;

    @Mock
    private DiscogsService discogsService;

    @Mock
    private SpotifyService spotifyService;

    @Mock FollowArtistService followArtistService;

    @Mock
    private ArtistSearchResponseTransformer searchResponseTransformer;

    @InjectMocks
    private ArtistSearchServiceImpl underTest;

    @BeforeEach
    void setup() {
        lenient().doReturn(UUID.randomUUID().toString()).when(userEntityMock).getPublicId();
    }

    @AfterEach
    void tearDown() {
        reset(currentPublicUserIdSupplier, userRepository, discogsService, spotifyService, searchResponseTransformer);
    }

    @Nested
    @DisplayName("Test discogs searching")
    class DiscogsSearchTest {

        @Test
        @DisplayName("Should pass provided arguments to discogs service")
        void should_pass_arguments() {
            // given
            var artistQueryString = "the query";
            var pageable = PageRequest.of(1, 10);
            doReturn(Optional.of(userEntityMock)).when(userRepository).findByPublicId(any());
            doReturn(DtoFactory.DiscogsArtistSearchResultDtoFactory.createDefault()).when(discogsService).searchArtistByName(any(), anyInt(), anyInt());
            doReturn(DtoFactory.ArtistSearchResponseFactory.discogs()).when(searchResponseTransformer).transformDiscogs(any());

            // when
            underTest.searchDiscogsByName(artistQueryString, pageable);

            // then
            verify(discogsService, times(1)).searchArtistByName(artistQueryString, pageable.getPageNumber(), pageable.getPageSize());
        }

        @Test
        @DisplayName("Should call searchResultTransformer with discogs result")
        void should_call_result_transformer() {
            // given
            var expectedSearchResults = DtoFactory.DiscogsArtistSearchResultDtoFactory.createDefault();
            doReturn(Optional.of(userEntityMock)).when(userRepository).findByPublicId(any());
            doReturn(expectedSearchResults).when(discogsService).searchArtistByName(any(), anyInt(), anyInt());
            doReturn(DtoFactory.ArtistSearchResponseFactory.discogs()).when(searchResponseTransformer).transformDiscogs(any());

            // when
            underTest.searchDiscogsByName("the query", PageRequest.of(1, 10));

            // then
            verify(searchResponseTransformer, times(1)).transformDiscogs(expectedSearchResults);
        }

        @Test
        @DisplayName("Should return searchResultTransformer's result")
        void should_return_results() {
            // given
            var expectedSearchResult = DtoFactory.ArtistSearchResponseFactory.discogs();
            doReturn(Optional.of(userEntityMock)).when(userRepository).findByPublicId(any());
            doReturn(DtoFactory.DiscogsArtistSearchResultDtoFactory.createDefault()).when(discogsService).searchArtistByName(any(), anyInt(), anyInt());
            doReturn(expectedSearchResult).when(searchResponseTransformer).transformDiscogs(any());

            // when
            ArtistSearchResponse result = underTest.searchDiscogsByName("the query", PageRequest.of(1, 10));

            // then
            assertThat(result).isEqualTo(expectedSearchResult);
        }

        @Test
        @DisplayName("Should call FollowArtistService for each artist to check if the user is already following the artist")
        void should_call_follow_artist_service() {
            // given
            var discogssearchresults = DtoFactory.ArtistSearchResponseFactory.discogs();
            discogssearchresults.setSearchResults(createListOfSearchResultEntries(List.of("1", "2", "3")));
            doReturn(Optional.of(userEntityMock)).when(userRepository).findByPublicId(any());
            doReturn(discogssearchresults).when(searchResponseTransformer).transformDiscogs(any());

            // when
            underTest.searchDiscogsByName("the query", PageRequest.of(1, 10));

            // then
            verify(followArtistService, times(1)).isFollowing(eq(userEntityMock.getPublicId()), eq("1"));
            verify(followArtistService, times(1)).isFollowing(eq(userEntityMock.getPublicId()), eq("2"));
            verify(followArtistService, times(1)).isFollowing(eq(userEntityMock.getPublicId()), eq("3"));
        }

        @Test
        @DisplayName("Should mark all already followed artists")
        void should_mark_already_followed_artists() {
            // given
            var discogssearchresults = DtoFactory.ArtistSearchResponseFactory.discogs();
            discogssearchresults.setSearchResults(createListOfSearchResultEntries(List.of("1", "2", "3")));
            doReturn(Optional.of(userEntityMock)).when(userRepository).findByPublicId(any());
            doReturn(true, false, true).when(followArtistService).isFollowing(anyString(), anyString());
            doReturn(discogssearchresults).when(searchResponseTransformer).transformDiscogs(any());

            // when
            var searchResults = underTest.searchDiscogsByName("the query", PageRequest.of(1, 10));

            // then
            assertThat(searchResults.getSearchResults().get(0).isFollowed()).isTrue();
            assertThat(searchResults.getSearchResults().get(1).isFollowed()).isFalse();
            assertThat(searchResults.getSearchResults().get(2).isFollowed()).isTrue();
        }

        @Test
        @DisplayName("Should get current user")
        void should_get_user() {
            // given
            var artistQueryString = "the query";
            var pageable = PageRequest.of(1, 10);
            var userId = "userId";
            doReturn(userId).when(currentPublicUserIdSupplier).get();
            doReturn(Optional.of(userEntityMock)).when(userRepository).findByPublicId(anyString());
            doReturn(DtoFactory.DiscogsArtistSearchResultDtoFactory.createDefault()).when(discogsService).searchArtistByName(any(), anyInt(), anyInt());
            doReturn(DtoFactory.ArtistSearchResponseFactory.discogs()).when(searchResponseTransformer).transformDiscogs(any());

            // when
            underTest.searchDiscogsByName(artistQueryString, pageable);

            // then
            verify(currentPublicUserIdSupplier, times(1)).get();
            verify(userRepository, times(1)).findByPublicId(userId);
        }

        @Test
        @DisplayName("Should throw exception when user not found")
        void should_throw_exception() {
            // given
            var artistQueryString = "the query";
            var pageable = PageRequest.of(1, 10);
            var userId = "userId";
            doThrow(new ResourceNotFoundException(userId)).when(userRepository).findByPublicId(any());

            // when
            var throwable = catchThrowable(() -> underTest.searchDiscogsByName(artistQueryString, pageable));

            // then
            assertThat(throwable).isInstanceOf(ResourceNotFoundException.class);
            assertThat(throwable).hasMessageContaining(userId);
        }

        private List<ArtistSearchResponseEntryDto> createListOfSearchResultEntries(List<String> externalIds) {
            return List.of(
                    DtoFactory.ArtistSearchResponseEntryDtoFactory.withId(externalIds.get(0)),
                    DtoFactory.ArtistSearchResponseEntryDtoFactory.withId(externalIds.get(1)),
                    DtoFactory.ArtistSearchResponseEntryDtoFactory.withId(externalIds.get(2))
            );
        }
    }

    @Nested
    @DisplayName("Test spotify searching")
    class SpotifySearchTest {

        @Test
        @DisplayName("Should pass provided arguments to spotify service")
        void should_pass_arguments() {
            // given
            var artistQueryString = "the query";
            var pageable = PageRequest.of(1, 10);
            doReturn(Optional.of(userEntityMock)).when(userRepository).findByPublicId(any());
            doReturn(DtoFactory.SpotifyArtistSearchResultDtoFactory.createDefault()).when(spotifyService).searchArtistByName(any(), anyInt(), anyInt());
            doReturn(DtoFactory.ArtistSearchResponseFactory.spotify()).when(searchResponseTransformer).transformSpotify(any());

            // when
            underTest.searchSpotifyByName(artistQueryString, pageable);

            // then
            verify(spotifyService, times(1)).searchArtistByName(artistQueryString, pageable.getPageNumber(), pageable.getPageSize());
        }

        @Test
        @DisplayName("Should call searchResultTransformer with spotify result")
        void should_call_result_transformer() {
            // given
            var expectedSearchResults = DtoFactory.SpotifyArtistSearchResultDtoFactory.createDefault();
            doReturn(Optional.of(userEntityMock)).when(userRepository).findByPublicId(any());
            doReturn(expectedSearchResults).when(spotifyService).searchArtistByName(any(), anyInt(), anyInt());
            doReturn(DtoFactory.ArtistSearchResponseFactory.spotify()).when(searchResponseTransformer).transformSpotify(any());

            // when
            underTest.searchSpotifyByName("the query", PageRequest.of(1, 10));

            // then
            verify(searchResponseTransformer, times(1)).transformSpotify(expectedSearchResults);
        }

        @Test
        @DisplayName("Should return searchResultTransformer's result")
        void should_return_results() {
            // given
            var expectedSearchResult = DtoFactory.ArtistSearchResponseFactory.spotify();
            doReturn(Optional.of(userEntityMock)).when(userRepository).findByPublicId(any());
            doReturn(DtoFactory.SpotifyArtistSearchResultDtoFactory.createDefault()).when(spotifyService).searchArtistByName(any(), anyInt(), anyInt());
            doReturn(expectedSearchResult).when(searchResponseTransformer).transformSpotify(any());

            // when
            ArtistSearchResponse result = underTest.searchSpotifyByName("the query", PageRequest.of(1, 10));

            // then
            assertThat(result).isEqualTo(expectedSearchResult);
        }

        @Test
        @DisplayName("Should call FollowArtistService for each artist to check if the user is already following the artist")
        void should_call_follow_artist_service() {
            // given
            var spotifySearchResults = DtoFactory.ArtistSearchResponseFactory.spotify();
            spotifySearchResults.setSearchResults(createListOfSearchResultEntries(List.of("1", "2", "3")));
            doReturn(Optional.of(userEntityMock)).when(userRepository).findByPublicId(any());
            doReturn(spotifySearchResults).when(searchResponseTransformer).transformSpotify(any());

            // when
            underTest.searchSpotifyByName("the query", PageRequest.of(1, 10));

            // then
            verify(followArtistService, times(1)).isFollowing(eq(userEntityMock.getPublicId()), eq("1"));
            verify(followArtistService, times(1)).isFollowing(eq(userEntityMock.getPublicId()), eq("2"));
            verify(followArtistService, times(1)).isFollowing(eq(userEntityMock.getPublicId()), eq("3"));
        }

        @Test
        @DisplayName("Should mark all already followed artists")
        void should_mark_already_followed_artists() {
            // given
            var spotifySearchResults = DtoFactory.ArtistSearchResponseFactory.spotify();
            spotifySearchResults.setSearchResults(createListOfSearchResultEntries(List.of("1", "2", "3")));
            doReturn(Optional.of(userEntityMock)).when(userRepository).findByPublicId(any());
            doReturn(true, false, true).when(followArtistService).isFollowing(anyString(), anyString());
            doReturn(spotifySearchResults).when(searchResponseTransformer).transformSpotify(any());

            // when
            var searchResults = underTest.searchSpotifyByName("the query", PageRequest.of(1, 10));

            // then
            assertThat(searchResults.getSearchResults().get(0).isFollowed()).isTrue();
            assertThat(searchResults.getSearchResults().get(1).isFollowed()).isFalse();
            assertThat(searchResults.getSearchResults().get(2).isFollowed()).isTrue();
        }

        @Test
        @DisplayName("Should get current user")
        void should_get_user() {
            // given
            var artistQueryString = "the query";
            var pageable = PageRequest.of(1, 10);
            var userId = "userId";
            doReturn(userId).when(currentPublicUserIdSupplier).get();
            doReturn(Optional.of(userEntityMock)).when(userRepository).findByPublicId(anyString());
            doReturn(DtoFactory.SpotifyArtistSearchResultDtoFactory.createDefault()).when(spotifyService).searchArtistByName(any(), anyInt(), anyInt());
            doReturn(DtoFactory.ArtistSearchResponseFactory.spotify()).when(searchResponseTransformer).transformSpotify(any());

            // when
            underTest.searchSpotifyByName(artistQueryString, pageable);

            // then
            verify(currentPublicUserIdSupplier, times(1)).get();
            verify(userRepository, times(1)).findByPublicId(userId);
        }

        @Test
        @DisplayName("Should throw exception when user not found")
        void should_throw_exception() {
            // given
            var artistQueryString = "the query";
            var pageable = PageRequest.of(1, 10);
            var userId = "userId";
            doThrow(new ResourceNotFoundException(userId)).when(userRepository).findByPublicId(any());

            // when
            var throwable = catchThrowable(() -> underTest.searchSpotifyByName(artistQueryString, pageable));

            // then
            assertThat(throwable).isInstanceOf(ResourceNotFoundException.class);
            assertThat(throwable).hasMessageContaining(userId);
        }

        private List<ArtistSearchResponseEntryDto> createListOfSearchResultEntries(List<String> externalIds) {
            return List.of(
                    DtoFactory.ArtistSearchResponseEntryDtoFactory.withId(externalIds.get(0)),
                    DtoFactory.ArtistSearchResponseEntryDtoFactory.withId(externalIds.get(1)),
                    DtoFactory.ArtistSearchResponseEntryDtoFactory.withId(externalIds.get(2))
            );
        }
    }
}
