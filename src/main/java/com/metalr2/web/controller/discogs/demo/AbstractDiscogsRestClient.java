package com.metalr2.web.controller.discogs.demo;

import com.metalr2.config.misc.DiscogsConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public abstract class AbstractDiscogsRestClient {

  final RestTemplate restTemplate;
  final DiscogsConfig discogsConfig;

  @Autowired
  public AbstractDiscogsRestClient(RestTemplate restTemplate, DiscogsConfig discogsConfig) {
    this.restTemplate = restTemplate;
    this.discogsConfig = discogsConfig;
  }

}
