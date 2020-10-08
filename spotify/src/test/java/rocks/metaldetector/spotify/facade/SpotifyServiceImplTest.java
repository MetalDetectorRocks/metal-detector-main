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
import rocks.metaldetector.spotify.api.search.SpotifyArtistSearchResultContainer;
import rocks.metaldetector.spotify.client.SpotifyArtistSearchClient;
import rocks.metaldetector.spotify.client.SpotifyAuthenticationClient;
import rocks.metaldetector.spotify.client.transformer.SpotifyArtistSearchResultTransformer;
import rocks.metaldetector.spotify.client.transformer.SpotifyArtistTransformer;
import rocks.metaldetector.spotify.config.SpotifyProperties;
import rocks.metaldetector.spotify.facade.dto.SpotifyArtistSearchResultDto;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static rocks.metaldetector.spotify.client.SpotifyDtoFactory.SpotfiyArtistFatory;
import static rocks.metaldetector.spotify.client.SpotifyDtoFactory.SpotifyArtistDtoFactory;
import static rocks.metaldetector.spotify.client.SpotifyDtoFactory.SpotifyArtistSearchResultContainerFactory;
import static rocks.metaldetector.spotify.client.SpotifyDtoFactory.SpotifyArtistSearchResultDtoFactory;

@ExtendWith(MockitoExtension.class)
class SpotifyServiceImplTest implements WithAssertions {

  @Mock
  private SpotifyArtistSearchClient searchClient;

  @Mock
  private SpotifyAuthenticationClient authenticationClient;

  @Mock
  private SpotifyArtistSearchResultTransformer resultTransformer;

  @Mock
  private SpotifyArtistTransformer artistTransformer;

  @Mock
  private SpotifyProperties spotifyProperties;

  @InjectMocks
  private SpotifyServiceImpl underTest;

  @AfterEach
  void tearDown() {
    reset(searchClient, authenticationClient, resultTransformer, artistTransformer, spotifyProperties);
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
      verify(authenticationClient, times(1)).getAuthenticationToken();
    }

    @Test
    @DisplayName("Should call search client with token")
    void test_search_client_token() {
      // given
      var token = "token";
      doReturn(token).when(authenticationClient).getAuthenticationToken();

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
      verify(authenticationClient, times(1)).getAuthenticationToken();
    }

    @Test
    @DisplayName("Should call search client with token")
    void test_search_client_token() {
      // given
      var token = "token";
      doReturn(token).when(authenticationClient).getAuthenticationToken();

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
      var artist = SpotfiyArtistFatory.withArtistName("Slayer");
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
      var artist = SpotfiyArtistFatory.withArtistName("Slayer");
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
  @DisplayName("Tests for method getSpotifyAuthorizationUrl")
  class AuthorizationUrlTest {

    @Test
    @DisplayName("authorization url with necessary parameters is returned")
    void test_correct_url_returned() {
      // given
      var host = "host";
      var clientId = "clientId";
      var baseUrl = "baseUrl";
      var encodedRedirectUrl = host + "%2Fprofile%2Fspotify-callback";
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
}
