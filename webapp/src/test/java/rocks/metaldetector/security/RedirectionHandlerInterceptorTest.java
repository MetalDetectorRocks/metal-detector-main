package rocks.metaldetector.security;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import rocks.metaldetector.persistence.domain.user.UserEntity;

import java.util.UUID;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RedirectionHandlerInterceptorTest implements WithAssertions {

  @Mock
  private CurrentPublicUserIdSupplier currentPublicUserIdSupplier;

  @Mock
  private UserEntity userEntity;

  private MockHttpServletRequest request;
  private MockHttpServletResponse response;

  @InjectMocks
  private RedirectionHandlerInterceptor redirectionHandlerInterceptor;

  @BeforeEach
  void setup() {
    request = new MockHttpServletRequest();
    response = new MockHttpServletResponse();
  }

  @AfterEach
  void tearDown() {
    reset(currentPublicUserIdSupplier, userEntity);
  }

  @Test
  @DisplayName("preHandle() should give false for logged in user")
  void pre_handle_should_return_false() {
    // given
    when(currentPublicUserIdSupplier.get()).thenReturn(UUID.randomUUID().toString());

    // when
    boolean result = redirectionHandlerInterceptor.preHandle(request, response, null);

    // then
    assertThat(result).isFalse();
  }

  @Test
  @DisplayName("preHandle() should give true for anonymous user")
  void pre_handle_should_return_true() {
    // given
    when(currentPublicUserIdSupplier.get()).thenReturn(null);

    // when
    boolean result = redirectionHandlerInterceptor.preHandle(request, response, null);

    // then
    assertThat(result).isTrue();
  }
}