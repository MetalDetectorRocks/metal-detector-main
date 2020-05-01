package rocks.metaldetector.security;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationListenerTest {

  @Mock
  private LoginAttemptService loginAttemptService;

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

  @AfterEach
  void tearDown() {
    reset(webAuthenticationDetails, successEvent, failureEvent, authentication, webAuthenticationDetails, loginAttemptService);
  }

  @Test
  @DisplayName("UserService is called on authentication success")
  void authentication_success_calls_user_service() {
    // given
    String ip = "i'm an ip";
    when(successEvent.getAuthentication()).thenReturn(authentication);
    when(authentication.getDetails()).thenReturn(webAuthenticationDetails);
    when(webAuthenticationDetails.getRemoteAddress()).thenReturn(ip);

    // when
    underTest.onAuthenticationSuccess(successEvent);

    // then
    verify(loginAttemptService, times(1)).loginSucceeded(ip.hashCode());
  }

  @Test
  @DisplayName("UserService is called on authentication failure")
  void authentication_failure_calls_user_service() {
    // given
    String ip = "i'm an ip";
    when(failureEvent.getAuthentication()).thenReturn(authentication);
    when(authentication.getDetails()).thenReturn(webAuthenticationDetails);
    when(webAuthenticationDetails.getRemoteAddress()).thenReturn(ip);

    // when
    underTest.onAuthenticationFailure(failureEvent);

    // then
    verify(loginAttemptService, times(1)).loginFailed(ip.hashCode());
  }
}
