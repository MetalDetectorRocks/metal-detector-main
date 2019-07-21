package com.metalr2.web.controller.discogs.demo;

import com.metalr2.config.misc.DiscogsConfig;
import com.metalr2.web.dto.discogs.search.ArtistSearchResult;
import com.metalr2.web.dto.discogs.search.ArtistSearchResults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class ArtistSearchRestClient extends AbstractDiscogsRestClient {

  private static final String ARTIST_SEARCH_URL_FRAGMENT = "/database/search?type=artist&q={artistQueryString}";

  @Autowired
  public ArtistSearchRestClient(RestTemplate restTemplate, DiscogsConfig discogsConfig) {
    super(restTemplate, discogsConfig);
  }

  public List<ArtistSearchResult> searchForArtist(String artistQueryString) {

    ResponseEntity<ArtistSearchResults> responseEntity = restTemplate.getForEntity(discogsConfig.getRestBaseUrl() + ARTIST_SEARCH_URL_FRAGMENT,
                                                                                  ArtistSearchResults.class,
                                                                                  artistQueryString);

    log.info("Status code value: " + responseEntity.getStatusCodeValue());

    return responseEntity.getBody() != null ? responseEntity.getBody().getResults() : Collections.emptyList();
  }


}