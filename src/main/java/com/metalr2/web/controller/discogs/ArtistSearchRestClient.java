package com.metalr2.web.controller.discogs;

import com.metalr2.config.misc.DiscogsConfig;
import com.metalr2.web.dto.discogs.artist.Artist;
import com.metalr2.web.dto.discogs.search.ArtistSearchResults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Slf4j
@Service
public class ArtistSearchRestClient extends AbstractDiscogsRestClient {

  private static final String ARTIST_NAME_SEARCH_URL_FRAGMENT = "/database/search?type=artist&q={artistQueryString}&page={page}&per_page={size}";
  private static final String ARTIST_ID_URL_FRAGMENT = "/artists/{artistId}";

  @Autowired
  public ArtistSearchRestClient(RestTemplate restTemplate, DiscogsConfig discogsConfig) {
    super(restTemplate, discogsConfig);
  }

  public Optional<ArtistSearchResults> searchForArtistByName(String artistQueryString, int page, int size) {
    log.debug("Searched artist: {}; page: {}; size: {}", artistQueryString, page, size);

    if (artistQueryString.isEmpty() || size == 0) {
      return Optional.empty();
    }

    ResponseEntity<ArtistSearchResults> responseEntity = restTemplate.getForEntity(discogsConfig.getRestBaseUrl() + ARTIST_NAME_SEARCH_URL_FRAGMENT,
            ArtistSearchResults.class,
            artistQueryString,
            page,
            size);

    if (responseEntity.getBody() == null || !responseEntity.getStatusCode().equals(HttpStatus.OK)
            || responseEntity.getBody().getResults().isEmpty()) {
      return Optional.empty();
    }
    return Optional.of(responseEntity.getBody());
  }

  public Optional<Artist> searchForArtistById(long artistId) {
    log.debug("Searched artist id: {}", artistId);

    ResponseEntity<Artist> responseEntity = restTemplate.getForEntity(discogsConfig.getRestBaseUrl() + ARTIST_ID_URL_FRAGMENT,
            Artist.class,
            artistId);

    if (responseEntity.getBody() == null || !responseEntity.getStatusCode().equals(HttpStatus.OK)) {
      return Optional.empty();
    }
    return Optional.of(responseEntity.getBody());
  }
}
