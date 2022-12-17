package rocks.metaldetector.support.infrastructure;

import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.ConnectionKeepAliveStrategy;
import org.apache.hc.client5.http.HttpRoute;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Iterator;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;

@Configuration
@Slf4j
public class ApacheHttpClientConfig {

  private static final int MAX_ROUTE_CONNECTIONS = 40;
  private static final int MAX_TOTAL_CONNECTIONS = 40;
  private static final int MAX_LOCALHOST_CONNECTIONS = 80;
  private static final String KEEP_ALIVE_HEADER_NAME = "Keep-alive";

  private final int port;

  public ApacheHttpClientConfig(@Value("${server.port}") int port) {
    this.port = port;
  }

  @Bean
  public PoolingHttpClientConnectionManager poolingConnectionManager() {
    PoolingHttpClientConnectionManager poolingConnectionManager = new PoolingHttpClientConnectionManager();

    // set total amount of connections across all HTTP routes
    poolingConnectionManager.setMaxTotal(MAX_TOTAL_CONNECTIONS);

    // set maximum amount of connections for each http route in pool
    poolingConnectionManager.setDefaultMaxPerRoute(MAX_ROUTE_CONNECTIONS);

    // increase the amounts of connections if host is localhost
    HttpHost localhost = new HttpHost("http://localhost", port);
    poolingConnectionManager.setMaxPerRoute(new HttpRoute(localhost), MAX_LOCALHOST_CONNECTIONS);

    return poolingConnectionManager;
  }

  @Bean
  public ConnectionKeepAliveStrategy connectionKeepAliveStrategy() {
    return (HttpResponse httpResponse, HttpContext httpContext) -> {
      Iterator<Header> headerIterator = httpResponse.headerIterator(KEEP_ALIVE_HEADER_NAME);

      while (headerIterator.hasNext()) {
        Header element = headerIterator.next();
        String param = element.getName();
        String value = element.getValue();
        if (value != null && param.equalsIgnoreCase("timeout")) {
          return TimeValue.of(Long.parseLong(value) * 1000, MILLISECONDS); // convert to ms
        }
      }

      return TimeValue.of(20, SECONDS);
    };
  }

  @Bean
  public Runnable idleConnectionMonitor(PoolingHttpClientConnectionManager pool) {
    return new Runnable() {
      @Override
      @Scheduled(fixedDelay = 60000L)
      public void run() {
        // only if connection pool is initialised
        if (pool != null) {
          pool.closeExpired();
          pool.closeIdle(TimeValue.of(10, MINUTES));
        }
      }
    };
  }

  @Bean
  public CloseableHttpClient httpClient() {
    RequestConfig requestConfig = RequestConfig.custom()
        .setConnectTimeout(Timeout.of(20, SECONDS)) // the time for waiting until a connection is established
        .setConnectionRequestTimeout(Timeout.of(20, SECONDS)) // the time for waiting for a connection from connection pool
        .setResponseTimeout(Timeout.of(90, SECONDS)) // the time for waiting for data
        .build();

    return HttpClients.custom()
        .setDefaultRequestConfig(requestConfig)
        .setConnectionManager(poolingConnectionManager())
        .setKeepAliveStrategy(connectionKeepAliveStrategy())
        .build();
  }
}
