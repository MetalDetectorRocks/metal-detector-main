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
import rocks.metaldetector.persistence.domain.user.UserEntity;
import rocks.metaldetector.service.user.UserService;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationListenerTest {

  private static final String USERNAME = "user";

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
  private UserEntity userEntity;

  @AfterEach
  void tearDown() {
    reset(userEntity, successEvent, failureEvent, authentication, userEntity);
  }

  @Test
  @DisplayName("UserService is called on authentication success")
  void authentication_success_calls_user_service() {
    // given
    when(successEvent.getAuthentication()).thenReturn(authentication);
    when(authentication.getPrincipal()).thenReturn(userEntity);

    // when
    underTest.onAuthenticationSuccess(successEvent);

    // then
    verify(userService, times(1)).persistSuccessfulLogin(userEntity.getPublicId());
  }

  @Test
  @DisplayName("UserService is called on authentication failure")
  void authentication_failure_calls_user_service() {
    // given
    when(failureEvent.getAuthentication()).thenReturn(authentication);
    when(authentication.getPrincipal()).thenReturn(USERNAME);

    // when
    underTest.onAuthenticationFailure(failureEvent);

    // then
    verify(userService, times(1)).handleFailedLogin(USERNAME);
  }
}