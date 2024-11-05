package rocks.metaldetector.discogs.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import rocks.metaldetector.support.DefaultRequestLoggingInterceptor;
import rocks.metaldetector.support.GetHeaderInterceptor;

import java.util.List;

@Configuration
public class DiscogsModuleConfig {

  @Bean
  public RestTemplate discogsRestTemplate(RestTemplateBuilder restTemplateBuilder,
                                          ResponseErrorHandler customClientErrorHandler,
                                          DiscogsConfig discogsConfig,
                                          MappingJackson2HttpMessageConverter jackson2HttpMessageConverter,
                                          StringHttpMessageConverter stringHttpMessageConverter,
                                          HttpComponentsClientHttpRequestFactory clientHttpRequestFactory) {
    return restTemplateBuilder
            .requestFactory(() -> clientHttpRequestFactory)
            .errorHandler(customClientErrorHandler)
            .interceptors(new DiscogsRequestInterceptor(discogsConfig), new DefaultRequestLoggingInterceptor(), new GetHeaderInterceptor())
            .messageConverters(List.of(jackson2HttpMessageConverter, stringHttpMessageConverter))
            .build();
  }
}
