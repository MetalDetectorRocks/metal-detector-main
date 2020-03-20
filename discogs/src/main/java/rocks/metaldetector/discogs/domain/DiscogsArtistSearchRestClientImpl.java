package rocks.metaldetector.discogs.domain;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import rocks.metaldetector.discogs.api.DiscogsArtist;
import rocks.metaldetector.discogs.api.DiscogsArtistSearchResultContainer;
import rocks.metaldetector.discogs.config.DiscogsCredentialsConfig;
import rocks.metaldetector.discogs.fascade.dto.DiscogsArtistDto;
import rocks.metaldetector.discogs.fascade.dto.DiscogsArtistSearchResultDto;
import rocks.metaldetector.discogs.fascade.dto.DiscogsSearchResultDto;
import rocks.metaldetector.discogs.domain.transformer.DiscogsArtistSearchResultContainerTransformer;
import rocks.metaldetector.discogs.domain.transformer.DiscogsArtistTransformer;

import java.util.Optional;

@Slf4j
@Service
public class DiscogsArtistSearchRestClientImpl implements DiscogsArtistSearchRestClient {

  static final String ARTIST_NAME_SEARCH_URL_FRAGMENT = "/database/search?type=artist&q={artistQueryString}&page={page}&per_page={size}";
  static final String ARTIST_ID_SEARCH_URL_FRAGMENT = "/artists/{artistId}";

  private final RestTemplate restTemplate;
  private final DiscogsCredentialsConfig discogsCredentialsConfig;
  private final DiscogsArtistTransformer artistTransformer;
  private final DiscogsArtistSearchResultContainerTransformer searchResultTransformer;

  @Autowired
  public DiscogsArtistSearchRestClientImpl(RestTemplate restTemplate, DiscogsCredentialsConfig discogsCredentialsConfig,
                                           DiscogsArtistTransformer artistTransformer, DiscogsArtistSearchResultContainerTransformer searchResultTransformer) {
    this.restTemplate = restTemplate;
    this.discogsCredentialsConfig = discogsCredentialsConfig;
    this.artistTransformer = artistTransformer;
    this.searchResultTransformer = searchResultTransformer;
  }

  @Override
  public Optional<DiscogsSearchResultDto<DiscogsArtistSearchResultDto>> searchByName(String artistQueryString, int pageNumber, int pageSize) {
    if (artistQueryString == null || artistQueryString.isEmpty()) {
      return Optional.empty();
    }

    ResponseEntity<DiscogsArtistSearchResultContainer> responseEntity = restTemplate.getForEntity(
            discogsCredentialsConfig.getRestBaseUrl() + ARTIST_NAME_SEARCH_URL_FRAGMENT,
            DiscogsArtistSearchResultContainer.class,
            artistQueryString,
            pageNumber,
            pageSize
    );

    DiscogsArtistSearchResultContainer resultContainer = responseEntity.getBody();
    if (resultContainer == null || responseEntity.getStatusCode() != HttpStatus.OK || resultContainer.getResults().isEmpty()) {
      return Optional.empty();
    }

    DiscogsSearchResultDto<DiscogsArtistSearchResultDto> searchResultDto = searchResultTransformer.transform(resultContainer);
    return Optional.of(searchResultDto);
  }

  @Override
  public Optional<DiscogsArtistDto> searchById(long artistId) {
    if (artistId <= 0) {
      return Optional.empty();
    }

    ResponseEntity<DiscogsArtist> responseEntity = restTemplate.getForEntity(
            discogsCredentialsConfig.getRestBaseUrl() + ARTIST_ID_SEARCH_URL_FRAGMENT,
            DiscogsArtist.class,
            artistId
    );

    DiscogsArtist discogsArtist = responseEntity.getBody();
    if (discogsArtist == null || responseEntity.getStatusCode() != HttpStatus.OK) {
      return Optional.empty();
    }

    DiscogsArtistDto artistDto = artistTransformer.transform(discogsArtist);
    return Optional.of(artistDto);
  }
}
