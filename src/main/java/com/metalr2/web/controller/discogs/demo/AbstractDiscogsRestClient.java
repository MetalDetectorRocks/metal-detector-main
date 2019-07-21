package com.metalr2.web.controller.discogs.demo;

import com.metalr2.config.misc.DiscogsConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

public abstract class AbstractDiscogsRestClient {

  final RestTemplate restTemplate;
  final DiscogsConfig discogsConfig;

  @Autowired
  public AbstractDiscogsRestClient(RestTemplate restTemplate, DiscogsConfig discogsConfig) {
    this.restTemplate = restTemplate;
    this.discogsConfig = discogsConfig;
  }

}
