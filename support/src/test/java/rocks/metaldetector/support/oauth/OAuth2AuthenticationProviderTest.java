package rocks.metaldetector.support.oauth;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.springframework.security.oauth2.core.AuthorizationGrantType.AUTHORIZATION_CODE;
import static org.springframework.security.oauth2.core.AuthorizationGrantType.CLIENT_CREDENTIALS;
import static rocks.metaldetector.support.oauth.OAuth2AuthenticationProvider.PRINCIPAL;

@ExtendWith(MockitoExtension.class)
class OAuth2AuthenticationProviderTest implements WithAssertions {

  private final OAuth2AuthenticationProvider underTest = new OAuth2AuthenticationProvider();

  @Test
  @DisplayName("anonymous principal is returned for grant 'client credentials'")
  void test_grant_client_credentials() {
    // when
    var result = underTest.provideForGrant(CLIENT_CREDENTIALS);

    // then
    assertThat(result).isEqualTo(PRINCIPAL);
  }

  @Test
  @DisplayName("currently authorized principal is returned for grant 'authorization code'")
  void test_grant_authorization_code() {
    // given
    var securityContextMock = mock(SecurityContext.class);
    var authenticationMock = mock(Authentication.class);
    doReturn(authenticationMock).when(securityContextMock).getAuthentication();
    Authentication result;

    try (MockedStatic<SecurityContextHolder> securityContextHolderMock = Mockito.mockStatic(SecurityContextHolder.class)) {
      // given
      securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContextMock);

      // when
      result = underTest.provideForGrant(AUTHORIZATION_CODE);
    }

    // then
    assertThat(result).isEqualTo(authenticationMock);
  }

  @Test
  @DisplayName("SecurityContext and ContextHolder are called correctly")
  void test_security_context_called() {
    // given
    var securityContextMock = mock(SecurityContext.class);
    doReturn(mock(Authentication.class)).when(securityContextMock).getAuthentication();

    try (MockedStatic<SecurityContextHolder> securityContextHolderMock = Mockito.mockStatic(SecurityContextHolder.class)) {
      // given
      securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContextMock);

      // when
      underTest.provideForGrant(AUTHORIZATION_CODE);

      // then
      securityContextHolderMock.verify(SecurityContextHolder::getContext);
    }

    // then
    verify(securityContextMock).getAuthentication();
  }

  @ParameterizedTest
  @DisplayName("exception is thrown for other grants")
  @ValueSource(strings = {"refresh_token", "password", "implicit", "urn:ietf:params:oauth:grant-type:jwt-bearer"})
  void test_exception_thrown_for_other_grants(String grant) {
    // when
    var throwable = catchThrowable(() -> underTest.provideForGrant(new AuthorizationGrantType(grant)));

    // then
    assertThat(throwable).isInstanceOf(IllegalArgumentException.class);
    assertThat(throwable).hasMessageContaining(grant);
  }
}