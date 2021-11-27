package rocks.metaldetector.butler.config;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.mock.http.client.MockClientHttpRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import rocks.metaldetector.support.oauth.OAuth2AccessTokenClient;

import java.io.IOException;
import java.util.List;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@ExtendWith(MockitoExtension.class)
class ButlerOAuth2ClientInterceptorTest implements WithAssertions {

  @Mock
  private OAuth2AccessTokenClient userClient;

  @Mock
  private OAuth2AccessTokenClient adminClient;

  private ButlerOAuth2ClientInterceptor underTest;

  @BeforeEach
  void setup() {
    underTest = new ButlerOAuth2ClientInterceptor(userClient, adminClient);
  }

  @AfterEach
  void tearDown() {
    reset(userClient, adminClient);
  }

  @Test
  @DisplayName("userClient is called for users")
  void test_user_client_called() throws IOException {
    // given
    var securityContextMock = mock(SecurityContext.class);
    var authenticationMock = mock(Authentication.class);
    var authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
    doReturn(authenticationMock).when(securityContextMock).getAuthentication();
    doReturn(authorities).when(authenticationMock).getAuthorities();

    try (MockedStatic<SecurityContextHolder> securityContextHolderMock = Mockito.mockStatic(SecurityContextHolder.class)) {
      // given
      securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContextMock);

      // when
      underTest.intercept(new MockClientHttpRequest(), new byte[0], mock(ClientHttpRequestExecution.class));
    }

    // then
    verify(userClient).getAccessToken();
  }

  @Test
  @DisplayName("adminClient is called for admin")
  void test_admin_client_called() throws IOException {
    // given
    var securityContextMock = mock(SecurityContext.class);
    var authenticationMock = mock(Authentication.class);
    var authorities = List.of(new SimpleGrantedAuthority("ROLE_ADMINISTRATOR"));
    doReturn(authenticationMock).when(securityContextMock).getAuthentication();
    doReturn(authorities).when(authenticationMock).getAuthorities();

    try (MockedStatic<SecurityContextHolder> securityContextHolderMock = Mockito.mockStatic(SecurityContextHolder.class)) {
      // given
      securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContextMock);

      // when
      underTest.intercept(new MockClientHttpRequest(), new byte[0], mock(ClientHttpRequestExecution.class));
    }

    // then
    verify(adminClient).getAccessToken();
  }

  @Test
  @DisplayName("exception is thrown on unknown role is called for admin")
  void test_unknown_role() {
    // given
    var securityContextMock = mock(SecurityContext.class);
    var authenticationMock = mock(Authentication.class);
    var authorities = List.of(new SimpleGrantedAuthority("ROLE_UNKNOWN"));
    var userName = "anon";
    doReturn(authenticationMock).when(securityContextMock).getAuthentication();
    doReturn(authorities).when(authenticationMock).getAuthorities();
    doReturn(userName).when(authenticationMock).getName();

    try (MockedStatic<SecurityContextHolder> securityContextHolderMock = Mockito.mockStatic(SecurityContextHolder.class)) {
      // given
      securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContextMock);

      // when
      var throwable = catchThrowable(() -> underTest.intercept(new MockClientHttpRequest(), new byte[0], mock(ClientHttpRequestExecution.class)));

      // then
      assertThat(throwable).isInstanceOf(AccessDeniedException.class);
      assertThat(throwable).hasMessageContaining(userName);
    }
  }

  @Test
  @DisplayName("tokenValue is set as bearer auth header")
  void test_token_value_set() throws IOException {
    // given
    var securityContextMock = mock(SecurityContext.class);
    var authenticationMock = mock(Authentication.class);
    var authorities = List.of(new SimpleGrantedAuthority("ROLE_ADMINISTRATOR"));
    var tokenValue = "accessTokenValue";
    var requestMock = new MockClientHttpRequest();
    doReturn(authenticationMock).when(securityContextMock).getAuthentication();
    doReturn(authorities).when(authenticationMock).getAuthorities();
    doReturn(tokenValue).when(adminClient).getAccessToken();

    try (MockedStatic<SecurityContextHolder> securityContextHolderMock = Mockito.mockStatic(SecurityContextHolder.class)) {
      // given
      securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContextMock);

      // when
      underTest.intercept(requestMock, new byte[0], mock(ClientHttpRequestExecution.class));
    }

    // then
    assertThat(requestMock.getHeaders()).containsKey(AUTHORIZATION);
    assertThat(requestMock.getHeaders().get(AUTHORIZATION)).isEqualTo(List.of("Bearer " + tokenValue));
  }
}
