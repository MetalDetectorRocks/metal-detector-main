package com.metalr2.discogs.restclient;

import com.metalr2.discogs.config.DiscogsConfig;
import com.metalr2.discogs.model.search.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class ArtistSearchRestClient extends AbstractDiscogsRestClient {

  private static final String ARTIST_SEARCH_URL_FRAGMENT = "/database/search?type=artist&q={artistQueryString}";

  @Autowired
  public ArtistSearchRestClient(RestTemplate restTemplate, DiscogsConfig discogsConfig) {
    super(restTemplate, discogsConfig);
    searchForArtist("Nirvana");
  }

  public void searchForArtist(String artistQueryString) {
    ResponseEntity<ArtistSearchResults> responseEntity = restTemplate.getForEntity(discogsConfig.getRestBaseUrl() + ARTIST_SEARCH_URL_FRAGMENT,
                                                                                  ArtistSearchResults.class,
                                                                                  artistQueryString);

    log.info("Status code value: " + responseEntity.getStatusCodeValue());
    log.info("HTTP Header 'ContentType': " + responseEntity.getHeaders().getContentType());

    for (ArtistSearchResult result : responseEntity.getBody().getResults()) {
      log.info(result.toString());
    }
  }

}
