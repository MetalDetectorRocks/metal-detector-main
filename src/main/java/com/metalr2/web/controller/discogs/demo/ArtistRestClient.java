package com.metalr2.web.controller.discogs.demo;

import com.metalr2.config.misc.DiscogsConfig;
import com.metalr2.web.dto.discogs.artist.Artist;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class ArtistRestClient extends AbstractDiscogsRestClient {

  private static final String ARTIST_URL_FRAGMENT = "/artists/{artistId}";

  public ArtistRestClient(RestTemplate restTemplate, DiscogsConfig discogsConfig) {
    super(restTemplate, discogsConfig);
    // getArtist(125246);
  }

  public void getArtist(long artistId) {
    ResponseEntity<Artist> responseEntity = restTemplate.getForEntity(discogsConfig.getRestBaseUrl() + ARTIST_URL_FRAGMENT,
                                                                      Artist.class,
                                                                      artistId);

    log.info("Status code value: " + responseEntity.getStatusCodeValue());
    log.info("HTTP Header 'ContentType': " + responseEntity.getHeaders().getContentType());

    Artist artist = responseEntity.getBody();
    assert artist != null;
    log.info("ID: {}", artist.getId());
    log.info("Profile: {}", artist.getProfile());
    log.info("Release URL: {}", artist.getReleasesUrl());
    log.info("Resource URL: {}", artist.getResourceUrl());
    log.info("URI: {}", artist.getUri());
    log.info("URLs: {}", artist.getUrls());
    log.info("Members: {}", artist.getMembers());
  }

}
