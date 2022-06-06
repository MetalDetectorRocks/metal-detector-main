package rocks.metaldetector.security;

import org.assertj.core.api.Condition;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import rocks.metaldetector.support.Endpoints;
import rocks.metaldetector.testutil.BaseSpringBootTest;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static rocks.metaldetector.support.Endpoints.AdminArea.INDEX;
import static rocks.metaldetector.support.Endpoints.Rest.SEARCH_ARTIST;

@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:test-prod;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE"
})
@ExtendWith(MockitoExtension.class)
@SpringBootTest
class RedirectionHandlerInterceptorIntegrationTest extends BaseSpringBootTest implements WithAssertions {

  @Autowired
  private ApplicationContext applicationContext;

  @ParameterizedTest(name = "[{index}] => Endpoint <{0}>")
  @MethodSource("inputProviderWithHandler")
  @DisplayName("Test that interceptor is added for endpoints for anonymous users")
  void test_interceptor_is_active(String endpoint) throws Exception {
    // given
    RequestMappingHandlerMapping mapping = (RequestMappingHandlerMapping) applicationContext.getBean("requestMappingHandlerMapping");
    MockHttpServletRequest request = new MockHttpServletRequest("GET", endpoint);

    // when
    HandlerExecutionChain chain = mapping.getHandler(request);

    // then
    assertThat(chain).isNotNull();

    List<HandlerInterceptor> interceptors = Arrays.asList(Objects.requireNonNull(chain.getInterceptors()));
    boolean redirectionInterceptorPresent = interceptors.stream().anyMatch(interceptor -> interceptor instanceof RedirectionHandlerInterceptor);

    assertThat(redirectionInterceptorPresent).isTrue();
  }

  @ParameterizedTest(name = "[{index}] => Endpoint <{0}>")
  @MethodSource("inputProviderWithoutHandler")
  @DisplayName("Test that interceptor is not added for endpoints for anonymous users")
  void test_interceptor_is_not_active(String endpoint) throws Exception {
    // given
    Predicate<HandlerExecutionChain> predicate = chain -> chain == null ||
                                                          Arrays.stream(Objects.requireNonNull(chain.getInterceptors()))
                                                              .noneMatch(interceptor -> interceptor instanceof RedirectionHandlerInterceptor);
    Condition<HandlerExecutionChain> nullOrNotContains = new Condition<>(predicate, "Is null or does not contain RedirectionHandlerInterceptor");
    RequestMappingHandlerMapping mapping = (RequestMappingHandlerMapping) applicationContext.getBean("requestMappingHandlerMapping");

    // when
    MockHttpServletRequest request = new MockHttpServletRequest("GET", endpoint);
    HandlerExecutionChain chain = mapping.getHandler(request);

    // then
    assertThat(chain).is(nullOrNotContains);
  }

  private static Stream<Arguments> inputProviderWithHandler() {
    return Arrays.stream(Endpoints.AntPattern.GUEST_ONLY_PAGES).map(Arguments::of);
  }

  private static Stream<Arguments> inputProviderWithoutHandler() {
    return Arrays.stream(new String[] {SEARCH_ARTIST, INDEX}).map(Arguments::of);
  }
}
