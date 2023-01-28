package rocks.metaldetector.security;

import org.apache.commons.codec.digest.DigestUtils;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import rocks.metaldetector.persistence.domain.user.UserEntity;
import rocks.metaldetector.service.user.UserService;
import rocks.metaldetector.testutil.DtoFactory.UserDtoFactory;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationListenerTest implements WithAssertions {

  @Mock
  private LoginAttemptService loginAttemptService;

  @Mock
  private UserService userService;

  @InjectMocks
  private AuthenticationListener underTest;

  @Mock
  private AuthenticationSuccessEvent successEvent;

  @Mock
  private AuthenticationFailureBadCredentialsEvent failureEvent;

  @Mock
  private Authentication authentication;

  @Mock
  private WebAuthenticationDetails webAuthenticationDetails;

  @Mock
  private UserEntity userEntity;

  @Mock
  private OAuth2AuthenticatedPrincipal oAuthUser;

  @AfterEach
  void tearDown() {
    reset(successEvent, failureEvent, authentication, webAuthenticationDetails, loginAttemptService, userService, userEntity, oAuthUser);
  }

  @Test
  @DisplayName("LoginAttemptService is called with hashed IP on authentication success")
  void authentication_success_calls_login_attempt_service() {
    // given
    String ip = "i'm an ip";
    String ipHash = "i'm a hashed ip";
    when(successEvent.getAuthentication()).thenReturn(authentication);
    when(authentication.getDetails()).thenReturn(webAuthenticationDetails);
    when(webAuthenticationDetails.getRemoteAddress()).thenReturn(ip);
    when(authentication.getPrincipal()).thenReturn(userEntity);
    when(userEntity.getPublicId()).thenReturn("publicId");

    // when
    try (MockedStatic<DigestUtils> mock = mockStatic(DigestUtils.class)) {
      mock.when(() -> DigestUtils.md5Hex(ip)).thenReturn(ipHash);
      underTest.onAuthenticationSuccess(successEvent);
    }

    // then
    verify(loginAttemptService).loginSucceeded(ipHash);
  }

  @Test
  @DisplayName("UserService is called to persist login on authentication success of UserEntity")
  void authentication_success_calls_user_service() {
    // given
    String publicId = "publicId";
    when(successEvent.getAuthentication()).thenReturn(authentication);
    when(authentication.getDetails()).thenReturn(webAuthenticationDetails);
    when(authentication.getPrincipal()).thenReturn(userEntity);
    when(userEntity.getPublicId()).thenReturn(publicId);
    when(webAuthenticationDetails.getRemoteAddress()).thenReturn("i'm an ip");

    // when
    underTest.onAuthenticationSuccess(successEvent);

    // then
    verify(userService).persistSuccessfulLogin(publicId);
  }

  @Test
  @DisplayName("UserService is called to get OAuth-User by email-address")
  void test_user_service_called_for_oauth_user() {
    // given
    var email = "mail@mail.mail";
    var userAttributes = Map.of("email", email);
    doReturn(authentication).when(successEvent).getAuthentication();
    doReturn(webAuthenticationDetails).when(authentication).getDetails();
    doReturn(oAuthUser).when(authentication).getPrincipal();
    doReturn(userAttributes).when(oAuthUser).getAttributes();
    doReturn(UserDtoFactory.createDefault()).when(userService).getUserByEmailOrUsername(any());
    doReturn("i'm an ip").when(webAuthenticationDetails).getRemoteAddress();

    // when
    underTest.onAuthenticationSuccess(successEvent);

    // then
    verify(userService).getUserByEmailOrUsername(email);
  }

  @Test
  @DisplayName("UserService is called to persist login on authentication success of OAuth user")
  void test_login_persisted_for_oauth_user() {
    // given
    var userDto = UserDtoFactory.createDefault();
    doReturn(authentication).when(successEvent).getAuthentication();
    doReturn(webAuthenticationDetails).when(authentication).getDetails();
    doReturn(oAuthUser).when(authentication).getPrincipal();
    doReturn(userDto).when(userService).getUserByEmailOrUsername(any());
    doReturn("i'm an ip").when(webAuthenticationDetails).getRemoteAddress();

    // when
    underTest.onAuthenticationSuccess(successEvent);

    // then
    verify(userService).persistSuccessfulLogin(userDto.getPublicId());
  }

  @Test
  @DisplayName("LoginAttemptService is called with hashed IP on authentication failure")
  void authentication_failure_calls_user_service() {
    // given
    String ip = "i'm an ip";
    String ipHash = "i'm a hashed ip";
    when(failureEvent.getAuthentication()).thenReturn(authentication);
    when(authentication.getDetails()).thenReturn(webAuthenticationDetails);
    when(webAuthenticationDetails.getRemoteAddress()).thenReturn(ip);

    // when
    try (MockedStatic<DigestUtils> mock = mockStatic(DigestUtils.class)) {
      mock.when(() -> DigestUtils.md5Hex(ip)).thenReturn(ipHash);
      underTest.onAuthenticationFailure(failureEvent);
    }

    // then
    verify(loginAttemptService).loginFailed(ipHash);
  }
}
