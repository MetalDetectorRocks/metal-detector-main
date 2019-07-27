package com.metalr2.web.controller.discogs.demo;

import com.metalr2.config.misc.DiscogsConfig;
import com.metalr2.web.dto.discogs.search.ArtistSearchResults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class ArtistSearchRestClient extends AbstractDiscogsRestClient {

  private static final String ARTIST_SEARCH_URL_FRAGMENT = "/database/search?type=artist&q={artistQueryString}&page={page}&per_page={size}";

  @Autowired
  public ArtistSearchRestClient(RestTemplate restTemplate, DiscogsConfig discogsConfig) {
    super(restTemplate, discogsConfig);
  }

  public ResponseEntity<ArtistSearchResults> searchForArtist(String artistQueryString, int page, int size) {
    ResponseEntity<ArtistSearchResults> responseEntity = restTemplate.getForEntity(discogsConfig.getRestBaseUrl() + ARTIST_SEARCH_URL_FRAGMENT,
                                                                                        ArtistSearchResults.class,
                                                                                        artistQueryString,
                                                                                        page,
                                                                                        size);

    log.debug("Status code value: {}", responseEntity.getStatusCodeValue());

    return responseEntity;
  }

}
