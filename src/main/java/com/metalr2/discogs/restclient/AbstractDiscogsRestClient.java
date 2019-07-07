package com.metalr2.discogs.restclient;

import com.metalr2.discogs.config.DiscogsConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public abstract class AbstractDiscogsRestClient {

  protected final RestTemplate restTemplate;
  protected final DiscogsConfig discogsConfig;

  @Autowired
  public AbstractDiscogsRestClient(RestTemplate restTemplate, DiscogsConfig discogsConfig) {
    this.restTemplate = restTemplate;
    this.discogsConfig = discogsConfig;
  }

}
