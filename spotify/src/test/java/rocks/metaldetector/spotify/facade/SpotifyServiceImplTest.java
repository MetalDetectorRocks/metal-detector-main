package rocks.metaldetector.spotify.facade;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.metaldetector.spotify.api.imports.SpotfiyAlbumImportResult;
import rocks.metaldetector.spotify.api.imports.SpotifyAlbumImportResultItem;
import rocks.metaldetector.spotify.api.search.SpotifyArtistSearchResultContainer;
import rocks.metaldetector.spotify.api.search.SpotifyArtistsContainer;
import rocks.metaldetector.spotify.client.SpotifyArtistSearchClient;
import rocks.metaldetector.spotify.client.SpotifyAuthorizationClient;
import rocks.metaldetector.spotify.client.SpotifyDtoFactory.SpotfiyUserAuthorizationDtoFactory;
import rocks.metaldetector.spotify.client.SpotifyDtoFactory.SpotfiyUserAuthorizationResponseFactory;
import rocks.metaldetector.spotify.client.SpotifyDtoFactory.SpotifyAlbumFactory;
import rocks.metaldetector.spotify.client.SpotifyImportClient;
import rocks.metaldetector.spotify.client.transformer.SpotifyAlbumTransformer;
import rocks.metaldetector.spotify.client.transformer.SpotifyArtistSearchResultTransformer;
import rocks.metaldetector.spotify.client.transformer.SpotifyArtistTransformer;
import rocks.metaldetector.spotify.client.transformer.SpotifyUserAuthorizationTransformer;
import rocks.metaldetector.spotify.config.SpotifyProperties;
import rocks.metaldetector.spotify.facade.dto.SpotifyAlbumDto;
import rocks.metaldetector.spotify.facade.dto.SpotifyArtistSearchResultDto;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static rocks.metaldetector.spotify.client.SpotifyDtoFactory.SpotfiyArtistFactory;
import static rocks.metaldetector.spotify.client.SpotifyDtoFactory.SpotifyArtistDtoFactory;
import static rocks.metaldetector.spotify.client.SpotifyDtoFactory.SpotifyArtistSearchResultContainerFactory;
import static rocks.metaldetector.spotify.client.SpotifyDtoFactory.SpotifyArtistSearchResultDtoFactory;

@ExtendWith(MockitoExtension.class)
class SpotifyServiceImplTest implements WithAssertions {

  @Mock
  private SpotifyArtistSearchClient searchClient;

  @Mock
  private SpotifyAuthorizationClient authenticationClient;

  @Mock
  private SpotifyArtistSearchResultTransformer resultTransformer;

  @Mock
  private SpotifyArtistTransformer artistTransformer;

  @Mock
  private SpotifyProperties spotifyProperties;

  @Mock
  private SpotifyUserAuthorizationTransformer userAuthorizationTransformer;

  @Mock
  private SpotifyAlbumTransformer albumTransformer;

  @Mock
  private SpotifyImportClient importClient;

  @InjectMocks
  private SpotifyServiceImpl underTest;

  @AfterEach
  void tearDown() {
    reset(searchClient, authenticationClient, resultTransformer, artistTransformer, spotifyProperties, userAuthorizationTransformer, importClient, albumTransformer);
  }

  @Nested
  @DisplayName("Tests for method searchArtistByName()")
  class SearchByNameTest {

    @Test
    @DisplayName("Should call authentication client")
    void test_authentication_client_called() {
      // when
      underTest.searchArtistByName("query", 1, 10);

      // then
      verify(authenticationClient, times(1)).getAppAuthorizationToken();
    }

    @Test
    @DisplayName("Should call search client with token")
    void test_search_client_token() {
      // given
      var token = "token";
      doReturn(token).when(authenticationClient).getAppAuthorizationToken();

      // when
      underTest.searchArtistByName("query", 1, 10);

      // then
      verify(searchClient, times(1)).searchByName(eq(token), any(), anyInt(), anyInt());
    }

    @Test
    @DisplayName("searchClient is called with query")
    void test_search_client_query() {
      // given
      var query = "query";

      // when
      underTest.searchArtistByName(query, 1, 10);

      // then
      verify(searchClient, times(1)).searchByName(any(), eq(query), anyInt(), anyInt());
    }

    @Test
    @DisplayName("searchClient is called with pageNumber")
    void test_search_client_page_number() {
      // given
      var pageNumber = 1;

      // when
      underTest.searchArtistByName("query", pageNumber, 10);

      // then
      verify(searchClient, times(1)).searchByName(any(), any(), eq(pageNumber), anyInt());
    }

    @Test
    @DisplayName("searchClient is called with pageSize")
    void test_search_client_page_size() {
      // given
      var pageSize = 10;

      // when
      underTest.searchArtistByName("query", 1, pageSize);

      // then
      verify(searchClient, times(1)).searchByName(any(), any(), anyInt(), eq(pageSize));
    }

    @Test
    @DisplayName("responseTrafo is called with search result")
    void test_call_response_transformer() {
      // given
      SpotifyArtistSearchResultContainer resultContainer = SpotifyArtistSearchResultContainerFactory.createDefault();
      doReturn(resultContainer).when(searchClient).searchByName(any(), any(), anyInt(), anyInt());

      // when
      underTest.searchArtistByName("query", 1, 10);

      // then
      verify(resultTransformer, times(1)).transform(resultContainer);
    }

    @Test
    @DisplayName("Result from responseTrafo is returned")
    void test_response_transformer() {
      // given
      SpotifyArtistSearchResultDto resultMock = SpotifyArtistSearchResultDtoFactory.createDefault();
      doReturn(resultMock).when(resultTransformer).transform(any());

      // when
      SpotifyArtistSearchResultDto result = underTest.searchArtistByName("query", 1, 10);

      // then
      assertThat(result).isEqualTo(resultMock);
    }
  }

  @Nested
  @DisplayName("Tests for method searchArtistById()")
  class SearchByIdTest {

    @Test
    @DisplayName("Should call authentication client")
    void test_authentication_client_called() {
      // when
      underTest.searchArtistById("666");

      // then
      verify(authenticationClient, times(1)).getAppAuthorizationToken();
    }

    @Test
    @DisplayName("Should call search client with token")
    void test_search_client_token() {
      // given
      var token = "token";
      doReturn(token).when(authenticationClient).getAppAuthorizationToken();

      // when
      underTest.searchArtistById("666");

      // then
      verify(searchClient, times(1)).searchById(eq(token), anyString());
    }

    @Test
    @DisplayName("Should pass provided artist id to search client")
    void should_pass_arguments() {
      // given
      var artistId = "666";

      // when
      underTest.searchArtistById(artistId);

      // then
      verify(searchClient, times(1)).searchById(any(), eq(artistId));
    }

    @Test
    @DisplayName("Should transform the result from search client with artist transformer")
    void should_transform_search_results() {
      // given
      var artist = SpotfiyArtistFactory.withArtistName("Slayer");
      doReturn(artist).when(searchClient).searchById(any(), any());

      // when
      underTest.searchArtistById("666");

      // then
      verify(artistTransformer, times(1)).transform(eq(artist));
    }

    @Test
    @DisplayName("Should return the result from artist transformer")
    void should_return_transformed_results() {
      // given
      var artist = SpotfiyArtistFactory.withArtistName("Slayer");
      var transformedSearchResult = SpotifyArtistDtoFactory.withArtistName("Slayer");
      doReturn(artist).when(searchClient).searchById(any(), any());
      doReturn(transformedSearchResult).when(artistTransformer).transform(any());

      // when
      var response = underTest.searchArtistById("666");

      // then
      assertThat(response).isEqualTo(transformedSearchResult);
    }
  }

  @Nested
  @DisplayName("Tests for method searchArtistsByIds()")
  class SearchByIdsTest {

    @Test
    @DisplayName("Should call authentication client")
    void test_authentication_client_called() {
      // given
      doReturn(SpotifyArtistsContainer.builder().artists(Collections.emptyList()).build()).when(searchClient).searchByIds(any(), any());

      // when
      underTest.searchArtistsByIds(List.of("666"));

      // then
      verify(authenticationClient, times(1)).getAppAuthorizationToken();
    }

    @Test
    @DisplayName("Should call search client with token")
    void test_search_client_token() {
      // given
      var token = "token";
      doReturn(token).when(authenticationClient).getAppAuthorizationToken();
      doReturn(SpotifyArtistsContainer.builder().artists(Collections.emptyList()).build()).when(searchClient).searchByIds(any(), any());

      // when
      underTest.searchArtistsByIds(List.of("666"));

      // then
      verify(searchClient, times(1)).searchByIds(eq(token), any());
    }

    @Test
    @DisplayName("Should pass provided artist ids to search client")
    void should_pass_arguments() {
      // given
      var artistIds = List.of("666");
      doReturn(SpotifyArtistsContainer.builder().artists(Collections.emptyList()).build()).when(searchClient).searchByIds(any(), any());

      // when
      underTest.searchArtistsByIds(artistIds);

      // then
      verify(searchClient, times(1)).searchByIds(any(), eq(artistIds));
    }

    @Test
    @DisplayName("Should transform the result from search client with artist transformer")
    void should_transform_search_results() {
      // given
      var artists = SpotifyArtistsContainer.builder().artists(List.of(SpotfiyArtistFactory.withArtistName("Slayer"),
                                                                      SpotfiyArtistFactory.withArtistName("Darkthrone"))).build();
      doReturn(artists).when(searchClient).searchByIds(any(), any());

      // when
      underTest.searchArtistsByIds(List.of("666"));

      // then
      verify(artistTransformer, times(1)).transform(eq(artists.getArtists().get(0)));
      verify(artistTransformer, times(1)).transform(eq(artists.getArtists().get(1)));
    }

    @Test
    @DisplayName("Should return the result from artist transformer")
    void should_return_transformed_results() {
      // given
      var artists = SpotifyArtistsContainer.builder().artists(List.of(SpotfiyArtistFactory.withArtistName("Slayer"))).build();
      var transformedSearchResult = SpotifyArtistDtoFactory.withArtistName("Slayer");
      doReturn(artists).when(searchClient).searchByIds(any(), any());
      doReturn(transformedSearchResult).when(artistTransformer).transform(any());

      // when
      var response = underTest.searchArtistsByIds(List.of("666"));

      // then
      assertThat(response).isEqualTo(List.of(transformedSearchResult));
    }
  }

  @Nested
  @DisplayName("Tests for method getSpotifyAuthorizationUrl()")
  class AuthorizationUrlTest {

    @Test
    @DisplayName("authorization url with necessary parameters is returned")
    void test_correct_url_returned() {
      // given
      var host = "host";
      var clientId = "clientId";
      var baseUrl = "baseUrl";
      var encodedRedirectUrl = host + "/profile/spotify-callback";
      var encodedScopes = "user-library-read+user-follow-read";
      var expectedUrl = baseUrl + "/authorize" + "?client_id=" + clientId + "&response_type=code" +
                        "&redirect_uri=" + encodedRedirectUrl + "&scope=" + encodedScopes + "&state=";
      doReturn(host).when(spotifyProperties).getApplicationHostUrl();
      doReturn(clientId).when(spotifyProperties).getClientId();
      doReturn(baseUrl).when(spotifyProperties).getAuthenticationBaseUrl();

      // when
      var result = underTest.getSpotifyAuthorizationUrl();

      // then
      assertThat(result).isEqualTo(expectedUrl);
    }

    @Test
    @DisplayName("spotifyConfig is called for relevant properties")
    void test_spotify_config_is_called() {
      // when
      underTest.getSpotifyAuthorizationUrl();

      // then
      verify(spotifyProperties, times(1)).getClientId();
      verify(spotifyProperties, times(1)).getAuthenticationBaseUrl();
      verify(spotifyProperties, times(1)).getApplicationHostUrl();
    }
  }

  @Nested
  @DisplayName("Tests for method getAccessToken()")
  class AccessTokenTest {

    @Test
    @DisplayName("spotifyAuthenticationClient is called")
    void test_authentication_client_called() {
      // given
      var code = "code";

      // when
      underTest.getAccessToken(code);

      // then
      verify(authenticationClient, times(1)).getUserAuthorizationToken(code);
    }

    @Test
    @DisplayName("userAuthorizationTransformer is called")
    void test_user_authorization_transformer_called() {
      // given
      var response = SpotfiyUserAuthorizationResponseFactory.createDefault();
      doReturn(response).when(authenticationClient).getUserAuthorizationToken(anyString());

      // when
      underTest.getAccessToken("code");

      // then
      verify(userAuthorizationTransformer, times(1)).transform(response);
    }

    @Test
    @DisplayName("result is returned")
    void test_result_is_returned() {
      // given
      var userAuthorizationDto = SpotfiyUserAuthorizationDtoFactory.createDefault();
      doReturn(userAuthorizationDto).when(userAuthorizationTransformer).transform(any());

      // when
      var result = underTest.getAccessToken("code");

      // then
      assertThat(result).isEqualTo(userAuthorizationDto);
    }
  }

  @Nested
  @DisplayName("Tests for method refreshToken")
  class RefreshAccessTokenTest {

    @Test
    @DisplayName("spotifyAuthenticationClient is called")
    void test_authentication_client_called() {
      // given
      var refreshToken = "refreshToken";

      // when
      underTest.refreshToken(refreshToken);

      // then
      verify(authenticationClient, times(1)).refreshUserAuthorizationToken(refreshToken);
    }

    @Test
    @DisplayName("userAuthorizationTransformer is called")
    void test_user_authorization_transformer_called() {
      // given
      var response = SpotfiyUserAuthorizationResponseFactory.createDefault();
      doReturn(response).when(authenticationClient).refreshUserAuthorizationToken(anyString());

      // when
      underTest.refreshToken("refreshToken");

      // then
      verify(userAuthorizationTransformer, times(1)).transform(response);
    }

    @Test
    @DisplayName("result is returned")
    void test_result_is_returned() {
      // given
      var userAuthorizationDto = SpotfiyUserAuthorizationDtoFactory.createDefault();
      doReturn(userAuthorizationDto).when(userAuthorizationTransformer).transform(any());

      // when
      var result = underTest.refreshToken("refreshToken");

      // then
      assertThat(result).isEqualTo(userAuthorizationDto);
    }
  }

  @Nested
  @DisplayName("Tests for method importAlbums()")
  class ImportAlbumsTest {

    @Test
    @DisplayName("importClient is called with token")
    void test_import_client_called_with_token() {
      // given
      var token = "token";
      var mockResult = SpotfiyAlbumImportResult.builder().items(Collections.emptyList()).build();
      doReturn(mockResult).when(importClient).importAlbums(any(), anyInt());

      // when
      underTest.importAlbums(token);

      // then
      verify(importClient, times(1)).importAlbums(eq(token), anyInt());
    }

    @Test
    @DisplayName("importClient is called with offset increasing by limit taken from result until total is reached")
    void test_offset_increasing() {
      // given
      var mockResult = SpotfiyAlbumImportResult.builder().items(Collections.emptyList()).total(30).limit(10).build();
      doReturn(mockResult).when(importClient).importAlbums(any(), eq(0));
      doReturn(mockResult).when(importClient).importAlbums(any(), eq(10));
      doReturn(mockResult).when(importClient).importAlbums(any(), eq(20));

      // when
      underTest.importAlbums("token");

      // then
      verify(importClient, times(1)).importAlbums(any(), eq(0));
      verify(importClient, times(1)).importAlbums(any(), eq(10));
      verify(importClient, times(1)).importAlbums(any(), eq(20));
    }

    @Test
    @DisplayName("albumTransformer is called for every album returned")
    void test_album_transformer_called() {
      // given
      var firstAlbum = SpotifyAlbumImportResultItem.builder().album(SpotifyAlbumFactory.withName("firstAlbum")).build();
      var secondAlbum = SpotifyAlbumImportResultItem.builder().album(SpotifyAlbumFactory.withName("secondAlbum")).build();
      var resultItems = List.of(firstAlbum, secondAlbum);
      var mockResult = SpotfiyAlbumImportResult.builder().items(resultItems).build();
      doReturn(mockResult).when(importClient).importAlbums(any(), anyInt());

      // when
      underTest.importAlbums("token");

      // then
      verify(albumTransformer, times(1)).transform(firstAlbum.getAlbum());
      verify(albumTransformer, times(1)).transform(secondAlbum.getAlbum());
    }

    @Test
    @DisplayName("transformed albums are returned")
    void test_transformed_albums_returned() {
      // given
      var album = SpotifyAlbumImportResultItem.builder().album(SpotifyAlbumFactory.withName("firstAlbum")).build();
      var mockResult = SpotfiyAlbumImportResult.builder().items(List.of(album)).build();
      var spotifyAlbumDto = SpotifyAlbumDto.builder().build();
      doReturn(mockResult).when(importClient).importAlbums(any(), anyInt());
      doReturn(spotifyAlbumDto).when(albumTransformer).transform(any());

      // when
      var result = underTest.importAlbums("token");

      // then
      assertThat(result).containsExactly(spotifyAlbumDto);
    }
  }
}
