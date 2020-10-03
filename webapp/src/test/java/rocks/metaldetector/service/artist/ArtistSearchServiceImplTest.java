package rocks.metaldetector.service.artist;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
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
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    @AfterEach
    void tearDown() {
        reset(currentPublicUserIdSupplier, userRepository, discogsService, spotifyService, searchResponseTransformer);
    }

    @Nested
    @DisplayName("Test discogs searching")
    class DiscogsSearchTest {

        @Test
        @DisplayName("Should pass provided arguments to discogs service")
        void searchDiscogsByName_should_pass_arguments() {
            // given
            var artistQueryString = "the query";
            var pageable = PageRequest.of(1, 10);
            doReturn(UUID.randomUUID().toString()).when(currentPublicUserIdSupplier).get();
            doReturn(Optional.of(userEntityMock)).when(userRepository).findByPublicId(anyString());
            doReturn(DtoFactory.DiscogsArtistSearchResultDtoFactory.createDefault()).when(discogsService).searchArtistByName(any(), anyInt(), anyInt());
            doReturn(DtoFactory.ArtistSearchResponseFactory.discogs()).when(searchResponseTransformer).transformDiscogs(any());

            // when
            underTest.searchDiscogsByName(artistQueryString, pageable);

            // then
            verify(discogsService, times(1)).searchArtistByName(artistQueryString, pageable.getPageNumber(), pageable.getPageSize());
        }

        @Test
        @DisplayName("Should call searchResultTransformer with discogs result")
        void searchDiscogsByName_should_call_result_transformer() {
            // given
            var expectedSearchResults = DtoFactory.DiscogsArtistSearchResultDtoFactory.createDefault();
            doReturn(UUID.randomUUID().toString()).when(currentPublicUserIdSupplier).get();
            doReturn(Optional.of(userEntityMock)).when(userRepository).findByPublicId(anyString());
            doReturn(expectedSearchResults).when(discogsService).searchArtistByName(any(), anyInt(), anyInt());
            doReturn(DtoFactory.ArtistSearchResponseFactory.discogs()).when(searchResponseTransformer).transformDiscogs(any());

            // when
            underTest.searchDiscogsByName("the query", PageRequest.of(1, 10));

            // then
            verify(searchResponseTransformer, times(1)).transformDiscogs(expectedSearchResults);
        }

        @Test
        @DisplayName("Should return searchResultTransformer's result")
        void searchDiscogsByName_should_return_results() {
            // given
            var expectedSearchResult = DtoFactory.ArtistSearchResponseFactory.discogs();
            doReturn(UUID.randomUUID().toString()).when(currentPublicUserIdSupplier).get();
            doReturn(Optional.of(userEntityMock)).when(userRepository).findByPublicId(anyString());
            doReturn(DtoFactory.DiscogsArtistSearchResultDtoFactory.createDefault()).when(discogsService).searchArtistByName(any(), anyInt(), anyInt());
            doReturn(expectedSearchResult).when(searchResponseTransformer).transformDiscogs(any());

            // when
            ArtistSearchResponse result = underTest.searchDiscogsByName("the query", PageRequest.of(1, 10));

            // then
            assertThat(result).isEqualTo(expectedSearchResult);
        }

        @Test
        @DisplayName("Should mark all already followed artists")
        void searchDiscogsByName_should_mark_already_followed_artists() {
            // given
            var discogssearchresults = DtoFactory.ArtistSearchResponseFactory.discogs();
            discogssearchresults.setSearchResults(createListOfSearchResultEntries(List.of("1", "2", "3")));
            doReturn(UUID.randomUUID().toString()).when(currentPublicUserIdSupplier).get();
            doReturn(Optional.of(userEntityMock)).when(userRepository).findByPublicId(anyString());
            when(followArtistService.isFollowing(anyString(), anyString())).then(invocationOnMock -> {
                String artistId = invocationOnMock.getArgument(1);
                return artistId.equals("1") || artistId.equals("3");
            });
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
        void searchDiscogsByName_should_get_user() {
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
        void searchDiscogsByName_should_throw_exception() {
            // given
            var artistQueryString = "the query";
            var pageable = PageRequest.of(1, 10);
            var userId = "userId";
            doReturn(userId).when(currentPublicUserIdSupplier).get();
            doThrow(new ResourceNotFoundException(userId)).when(userRepository).findByPublicId(anyString());

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
        void searchSpotifyByName_should_pass_arguments() {
            // given
            var artistQueryString = "the query";
            var pageable = PageRequest.of(1, 10);
            doReturn(UUID.randomUUID().toString()).when(currentPublicUserIdSupplier).get();
            doReturn(Optional.of(userEntityMock)).when(userRepository).findByPublicId(anyString());
            doReturn(DtoFactory.SpotifyArtistSearchResultDtoFactory.createDefault()).when(spotifyService).searchArtistByName(any(), anyInt(), anyInt());
            doReturn(DtoFactory.ArtistSearchResponseFactory.spotify()).when(searchResponseTransformer).transformSpotify(any());

            // when
            underTest.searchSpotifyByName(artistQueryString, pageable);

            // then
            verify(spotifyService, times(1)).searchArtistByName(artistQueryString, pageable.getPageNumber(), pageable.getPageSize());
        }

        @Test
        @DisplayName("Should call searchResultTransformer with spotify result")
        void searchSpotifyByName_should_call_result_transformer() {
            // given
            var expectedSearchResults = DtoFactory.SpotifyArtistSearchResultDtoFactory.createDefault();
            doReturn(UUID.randomUUID().toString()).when(currentPublicUserIdSupplier).get();
            doReturn(Optional.of(userEntityMock)).when(userRepository).findByPublicId(anyString());
            doReturn(expectedSearchResults).when(spotifyService).searchArtistByName(any(), anyInt(), anyInt());
            doReturn(DtoFactory.ArtistSearchResponseFactory.spotify()).when(searchResponseTransformer).transformSpotify(any());

            // when
            underTest.searchSpotifyByName("the query", PageRequest.of(1, 10));

            // then
            verify(searchResponseTransformer, times(1)).transformSpotify(expectedSearchResults);
        }

        @Test
        @DisplayName("Should return searchResultTransformer's result")
        void searchSpotifyByName_should_return_results() {
            // given
            var expectedSearchResult = DtoFactory.ArtistSearchResponseFactory.spotify();
            doReturn(UUID.randomUUID().toString()).when(currentPublicUserIdSupplier).get();
            doReturn(Optional.of(userEntityMock)).when(userRepository).findByPublicId(anyString());
            doReturn(DtoFactory.SpotifyArtistSearchResultDtoFactory.createDefault()).when(spotifyService).searchArtistByName(any(), anyInt(), anyInt());
            doReturn(expectedSearchResult).when(searchResponseTransformer).transformSpotify(any());

            // when
            ArtistSearchResponse result = underTest.searchSpotifyByName("the query", PageRequest.of(1, 10));

            // then
            assertThat(result).isEqualTo(expectedSearchResult);
        }

        @Test
        @DisplayName("Should mark all already followed artists")
        void searchSpotifyByName_should_mark_already_followed_artists() {
            // given
            var spotifySearchResults = DtoFactory.ArtistSearchResponseFactory.spotify();
            spotifySearchResults.setSearchResults(createListOfSearchResultEntries(List.of("1", "2", "3")));
            doReturn(UUID.randomUUID().toString()).when(currentPublicUserIdSupplier).get();
            doReturn(Optional.of(userEntityMock)).when(userRepository).findByPublicId(anyString());
            when(followArtistService.isFollowing(anyString(), anyString())).then(invocationOnMock -> {
                String artistId = invocationOnMock.getArgument(1);
                return artistId.equals("1") || artistId.equals("3");
            });
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
        void searchSpotifyByName_should_get_user() {
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
        void searchSpotifyByName_should_throw_exception() {
            // given
            var artistQueryString = "the query";
            var pageable = PageRequest.of(1, 10);
            var userId = "userId";
            doReturn(userId).when(currentPublicUserIdSupplier).get();
            doThrow(new ResourceNotFoundException(userId)).when(userRepository).findByPublicId(anyString());

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
