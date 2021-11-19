package rocks.metaldetector.discogs.config;

import lombok.AllArgsConstructor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import rocks.metaldetector.support.DefaultRequestLoggingInterceptor;
import rocks.metaldetector.support.GetHeaderInterceptor;
import rocks.metaldetector.support.infrastructure.CustomClientErrorHandler;

import java.util.List;

@Configuration
@AllArgsConstructor
public class DiscogsModuleConfig {

  private final DiscogsConfig discogsConfig;
  private final MappingJackson2HttpMessageConverter jackson2HttpMessageConverter;
  private final StringHttpMessageConverter stringHttpMessageConverter;
  private final HttpComponentsClientHttpRequestFactory clientHttpRequestFactory;

  @Bean
  public RestTemplate discogsRestTemplate(RestTemplateBuilder restTemplateBuilder) {
    return restTemplateBuilder
            .requestFactory(() -> clientHttpRequestFactory)
            .errorHandler(new CustomClientErrorHandler())
            .interceptors(new DiscogsRequestInterceptor(discogsConfig), new DefaultRequestLoggingInterceptor(), new GetHeaderInterceptor())
            .messageConverters(List.of(jackson2HttpMessageConverter, stringHttpMessageConverter))
            .build();
  }
}
