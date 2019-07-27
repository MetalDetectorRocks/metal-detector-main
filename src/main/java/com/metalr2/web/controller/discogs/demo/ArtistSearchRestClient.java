package com.metalr2.web.controller.discogs.demo;

import com.metalr2.config.misc.DiscogsConfig;
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

  private static final String ARTIST_SEARCH_URL_FRAGMENT = "/database/search?type=artist&q={artistQueryString}&page={page}&per_page={size}";

  @Autowired
  public ArtistSearchRestClient(RestTemplate restTemplate, DiscogsConfig discogsConfig) {
    super(restTemplate, discogsConfig);
  }

  public Optional<ResponseEntity<ArtistSearchResults>> searchForArtist(String artistQueryString, int page, int size) {

    if (artistQueryString.isEmpty() || size == 0){
      return Optional.empty();
    }

    ResponseEntity<ArtistSearchResults> responseEntity = restTemplate.getForEntity(discogsConfig.getRestBaseUrl() + ARTIST_SEARCH_URL_FRAGMENT,
                                                                                        ArtistSearchResults.class,
                                                                                        artistQueryString,
                                                                                        page,
                                                                                        size);

    return responseEntity.getBody() == null || !responseEntity.getStatusCode().equals(HttpStatus.OK)
                                            ||  responseEntity.getBody().getResults().isEmpty()
                                                                                        ? Optional.empty()
                                                                                        : Optional.of(responseEntity);
  }

}
