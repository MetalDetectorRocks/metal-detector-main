package rocks.metaldetector.telegram.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestOperations;
import rocks.metaldetector.support.DefaultRequestLoggingInterceptor;
import rocks.metaldetector.support.PostHeaderInterceptor;

import java.util.List;

@Configuration
public class TelegramModuleConfig {

  @Bean
  public RestOperations telegramRestOperations(MappingJackson2HttpMessageConverter jackson2HttpMessageConverter,
                                               StringHttpMessageConverter stringHttpMessageConverter,
                                               FormHttpMessageConverter formHttpMessageConverter,
                                               HttpComponentsClientHttpRequestFactory clientHttpRequestFactory,
                                               RestTemplateBuilder restTemplateBuilder,
                                               ResponseErrorHandler customClientErrorHandler) {
    return restTemplateBuilder
        .requestFactory(() -> clientHttpRequestFactory)
        .errorHandler(customClientErrorHandler)
        .interceptors(new DefaultRequestLoggingInterceptor(), new PostHeaderInterceptor())
        .messageConverters(List.of(jackson2HttpMessageConverter, stringHttpMessageConverter, formHttpMessageConverter))
        .build();
  }
}
