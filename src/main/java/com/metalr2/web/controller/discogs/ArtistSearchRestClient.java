package com.metalr2.web.controller.discogs;

import com.metalr2.config.misc.DiscogsConfig;
import com.metalr2.web.dto.discogs.artist.Artist;
import com.metalr2.web.dto.discogs.search.ArtistSearchResultContainer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Slf4j
@Service
public class ArtistSearchRestClient extends AbstractDiscogsRestClient {

  static final String ARTIST_NAME_SEARCH_URL_FRAGMENT = "/database/search?type=artist&q={artistQueryString}&page={page}&per_page={size}";
  static final String ARTIST_ID_SEARCH_URL_FRAGMENT = "/artists/{artistId}";

  @Autowired
  public ArtistSearchRestClient(RestTemplate restTemplate, DiscogsConfig discogsConfig) {
    super(restTemplate, discogsConfig);
  }

  public Optional<ArtistSearchResultContainer> searchByName(String artistQueryString, int page, int size) {
    if (StringUtils.isEmpty(artistQueryString) || size == 0) {
      return Optional.empty();
    }

    ResponseEntity<ArtistSearchResultContainer> responseEntity = restTemplate.getForEntity(discogsConfig.getRestBaseUrl() + ARTIST_NAME_SEARCH_URL_FRAGMENT,
            ArtistSearchResultContainer.class,
            artistQueryString,
            page,
            size);

    ArtistSearchResultContainer resultContainer = responseEntity.getBody();
    if (resultContainer == null || responseEntity.getStatusCode() != HttpStatus.OK || resultContainer.getResults().isEmpty()) {
      return Optional.empty();
    }

    return Optional.of(responseEntity.getBody());
  }

  public Optional<Artist> searchById(long artistId) {
    if (artistId <= 0) {
      return Optional.empty();
    }

    ResponseEntity<Artist> responseEntity = restTemplate.getForEntity(discogsConfig.getRestBaseUrl() + ARTIST_ID_SEARCH_URL_FRAGMENT,
            Artist.class,
            artistId);

    if (responseEntity.getBody() == null || responseEntity.getStatusCode() != HttpStatus.OK) {
      return Optional.empty();
    }

    return Optional.of(responseEntity.getBody());
  }
}
