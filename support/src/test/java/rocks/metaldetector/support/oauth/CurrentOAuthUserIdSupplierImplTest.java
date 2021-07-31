package rocks.metaldetector.support.oauth;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static rocks.metaldetector.support.oauth.CurrentOAuthUserIdSupplierImpl.GOOGLE_REGISTRATION_ID;
import static rocks.metaldetector.support.oauth.CurrentOAuthUserIdSupplierImpl.GOOGLE_USER_ID_ATTRIBUTE;

@ExtendWith(MockitoExtension.class)
class CurrentOAuthUserIdSupplierImplTest implements WithAssertions {

  private final CurrentOAuthUserIdSupplierImpl underTest = new CurrentOAuthUserIdSupplierImpl();

  @Test
  @DisplayName("securityContextHolder is called for securityContext")
  void test_security_context_holder_called() {
    // given
    var securityContextMock = mock(SecurityContext.class);
    doReturn(mock(Authentication.class)).when(securityContextMock).getAuthentication();

    try (MockedStatic<SecurityContextHolder> securityContextHolderMock = Mockito.mockStatic(SecurityContextHolder.class)) {
      // given
      securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContextMock);

      // when
      underTest.get();

      // then
      securityContextHolderMock.verify(SecurityContextHolder::getContext);
    }
  }

  @Test
  @DisplayName("securityContext is called for current authentication")
  void test_security_context_called() {
    // given
    var securityContextMock = mock(SecurityContext.class);
    doReturn(mock(Authentication.class)).when(securityContextMock).getAuthentication();

    try (MockedStatic<SecurityContextHolder> securityContextHolderMock = Mockito.mockStatic(SecurityContextHolder.class)) {
      // given
      securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContextMock);

      // when
      underTest.get();
    }

    // then
    verify(securityContextMock).getAuthentication();
  }

  @Test
  @DisplayName("authentication is called for username")
  void test_authentication_called() {
    // given
    var securityContextMock = mock(SecurityContext.class);
    var authenticationMock = mock(Authentication.class);
    doReturn(authenticationMock).when(securityContextMock).getAuthentication();

    try (MockedStatic<SecurityContextHolder> securityContextHolderMock = Mockito.mockStatic(SecurityContextHolder.class)) {
      // given
      securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContextMock);

      // when
      underTest.get();
    }

    // then
    verify(authenticationMock).getName();
  }

  @Test
  @DisplayName("username is returned")
  void test_username_returned() {
    // given
    var securityContextMock = mock(SecurityContext.class);
    var authenticationMock = mock(Authentication.class);
    var username = "username";
    String result;
    doReturn(authenticationMock).when(securityContextMock).getAuthentication();
    doReturn(username).when(authenticationMock).getName();

    try (MockedStatic<SecurityContextHolder> securityContextHolderMock = Mockito.mockStatic(SecurityContextHolder.class)) {
      // given
      securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContextMock);

      // when
      result = underTest.get();
    }

    // then
    assertThat(result).isEqualTo(username);
  }

  @Test
  @DisplayName("Authentication is called for registrationId if it is an OAuthUser")
  void test_authentication_called_for_registration_id() {
    // given
    var securityContextMock = mock(SecurityContext.class);
    var authenticationMock = mock(OAuth2AuthenticationToken.class);
    doReturn(authenticationMock).when(securityContextMock).getAuthentication();
    doReturn(mock(OAuth2User.class)).when(authenticationMock).getPrincipal();
    doReturn(GOOGLE_REGISTRATION_ID).when(authenticationMock).getAuthorizedClientRegistrationId();

    try (MockedStatic<SecurityContextHolder> securityContextHolderMock = Mockito.mockStatic(SecurityContextHolder.class)) {
      // given
      securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContextMock);

      // when
      underTest.get();
    }

    // then
    verify(authenticationMock).getAuthorizedClientRegistrationId();
  }

  @Test
  @DisplayName("Principal is called with google's id if it is an OAuthUser")
  void test_principal_called_with_google_id() {
    // given
    var securityContextMock = mock(SecurityContext.class);
    var authenticationMock = mock(OAuth2AuthenticationToken.class);
    var mockUser = mock(OAuth2User.class);
    doReturn(authenticationMock).when(securityContextMock).getAuthentication();
    doReturn(GOOGLE_REGISTRATION_ID).when(authenticationMock).getAuthorizedClientRegistrationId();
    doReturn(mockUser).when(authenticationMock).getPrincipal();

    try (MockedStatic<SecurityContextHolder> securityContextHolderMock = Mockito.mockStatic(SecurityContextHolder.class)) {
      // given
      securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContextMock);

      // when
      underTest.get();
    }

    // then
    verify(mockUser).getAttribute(GOOGLE_USER_ID_ATTRIBUTE);
  }

  @Test
  @DisplayName("google's id is returned if it is an OAuthUser")
  void test_google_id_returned() {
    // given
    var securityContextMock = mock(SecurityContext.class);
    var authenticationMock = mock(OAuth2AuthenticationToken.class);
    var mockUser = mock(OAuth2User.class);
    var userId = "userId";
    String result;
    doReturn(authenticationMock).when(securityContextMock).getAuthentication();
    doReturn(GOOGLE_REGISTRATION_ID).when(authenticationMock).getAuthorizedClientRegistrationId();
    doReturn(mockUser).when(authenticationMock).getPrincipal();
    doReturn(userId).when(mockUser).getAttribute(any());

    try (MockedStatic<SecurityContextHolder> securityContextHolderMock = Mockito.mockStatic(SecurityContextHolder.class)) {
      // given
      securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContextMock);

      // when
      result = underTest.get();
    }

    // then
    assertThat(result).isEqualTo(userId);
  }

  @Test
  @DisplayName("Exception is thrown on unknown registrationId")
  void test_exception_on_unknown_registration_id() {
    // given
    var securityContextMock = mock(SecurityContext.class);
    var authenticationMock = mock(OAuth2AuthenticationToken.class);
    var unknownRegistrationId = "unknownRegistrationId";
    Throwable throwable;
    doReturn(authenticationMock).when(securityContextMock).getAuthentication();
    doReturn(unknownRegistrationId).when(authenticationMock).getAuthorizedClientRegistrationId();

    try (MockedStatic<SecurityContextHolder> securityContextHolderMock = Mockito.mockStatic(SecurityContextHolder.class)) {
      // given
      securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContextMock);

      // when
      throwable = catchThrowable(underTest::get);
    }

    // then
    assertThat(throwable).isInstanceOf(IllegalStateException.class);
    assertThat(throwable).hasMessageContaining(unknownRegistrationId);
  }
}
