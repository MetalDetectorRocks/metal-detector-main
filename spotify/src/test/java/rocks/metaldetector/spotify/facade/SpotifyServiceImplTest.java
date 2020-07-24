package rocks.metaldetector.spotify.facade;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.metaldetector.spotify.api.search.SpotifyArtistSearchResultContainer;
import rocks.metaldetector.spotify.client.SpotifyArtistSearchClient;
import rocks.metaldetector.spotify.client.SpotifyAuthenticationClient;
import rocks.metaldetector.spotify.client.transformer.SpotifyArtistSearchResultTransformer;
import rocks.metaldetector.spotify.facade.dto.SpotifyArtistSearchResultDto;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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

  @InjectMocks
  private SpotifyServiceImpl underTest;

  @AfterEach
  void tearDown() {
    reset(searchClient, authenticationClient, resultTransformer);
  }

  @Test
  @DisplayName("searching calls authenticationClient")
  void test_authentication_client_called() {
    // when
    underTest.searchArtists("query", 1, 10);

    // then
    verify(authenticationClient, times(1)).getAuthenticationToken();
  }

  @Test
  @DisplayName("searchClient is called with token")
  void test_search_client_token() {
    // given
    var token = "token";
    doReturn(token).when(authenticationClient).getAuthenticationToken();

    // when
    underTest.searchArtists("query", 1, 10);

    // then
    verify(searchClient, times(1)).searchByName(eq(token), any(), anyInt(), anyInt());
  }

  @Test
  @DisplayName("searchClient is called with query")
  void test_search_client_query() {
    // given
    var query = "query";

    // when
    underTest.searchArtists(query, 1, 10);

    // then
    verify(searchClient, times(1)).searchByName(any(), eq(query), anyInt(), anyInt());
  }

  @Test
  @DisplayName("searchClient is called with pageNumber")
  void test_search_client_page_number() {
    // given
    var pageNumber = 1;

    // when
    underTest.searchArtists("query", pageNumber, 10);

    // then
    verify(searchClient, times(1)).searchByName(any(), any(), eq(pageNumber), anyInt());
  }

  @Test
  @DisplayName("searchClient is called with pageSize")
  void test_search_client_page_size() {
    // given
    var pageSize = 10;

    // when
    underTest.searchArtists("query", 1, pageSize);

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
    underTest.searchArtists("query", 1, 10);

    // then
    verify(resultTransformer, times(1)).transform(resultContainer);
  }

  @Test
  @DisplayName("responseTrafo is called and result returned")
  void test_response_transformer() {
    // given
    SpotifyArtistSearchResultDto resultMock = SpotifyArtistSearchResultDtoFactory.createDefault();
    doReturn(resultMock).when(resultTransformer).transform(any());

    // when
    SpotifyArtistSearchResultDto result = underTest.searchArtists("query", 1, 10);

    // then
    assertThat(result).isEqualTo(resultMock);
  }
}