package rocks.metaldetector.service.auth;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import rocks.metaldetector.persistence.domain.user.UserEntity;
import rocks.metaldetector.service.user.UserDto;
import rocks.metaldetector.service.user.UserFromTokenExtractor;
import rocks.metaldetector.service.user.UserService;
import rocks.metaldetector.service.user.events.OnRequestPasswordResetEvent;
import rocks.metaldetector.web.api.auth.InitResetPasswordRequest;
import rocks.metaldetector.web.api.auth.ResetPasswordRequest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static rocks.metaldetector.testutil.DtoFactory.*;

@ExtendWith(MockitoExtension.class)
class ResetPasswordServiceTest implements WithAssertions {

  @Mock
  private UserService userService;

  @Mock
  private ApplicationEventPublisher eventPublisher;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private UserFromTokenExtractor userFromTokenExtractor;

  @InjectMocks
  private ResetPasswordService underTest;

  @AfterEach
  void afterEach() {
    reset(userService, eventPublisher);
  }

  @Nested
  class RequestPasswordResetTests {

    @Test
    @DisplayName("should fetch user via user service")
    void should_fetch_user_via_user_service() {
      // given
      String email = "test@example.com";
      InitResetPasswordRequest request = new InitResetPasswordRequest(email);

      // when
      underTest.requestPasswordReset(request);

      // then
      verify(userService).getUserByEmailOrUsername(email);
    }

    @Test
    @DisplayName("should publish event")
    void should_publish_event() {
      // given
      UserDto userDto = UserDtoFactory.createDefault();
      when(userService.getUserByEmailOrUsername(any())).thenReturn(userDto);

      // when
      underTest.requestPasswordReset(new InitResetPasswordRequest("test@example.com"));

      // then
      ArgumentCaptor<OnRequestPasswordResetEvent> captor = ArgumentCaptor.forClass(OnRequestPasswordResetEvent.class);
      verify(eventPublisher).publishEvent(captor.capture());
      assertThat(captor.getValue().getSource()).isEqualTo(underTest);
      assertThat(captor.getValue().getUserDto()).isEqualTo(userDto);
    }
  }

  @Nested
  class ResetPasswordTests {

    @Test
    @DisplayName("should extract user from token")
    void should_extract_user_from_token() {
      // given
      ResetPasswordRequest request = new ResetPasswordRequest("eyFoobar", "new-password");
      when(userFromTokenExtractor.extractUserFromToken(any())).thenReturn(mock(UserEntity.class));

      // when
      underTest.resetPassword(request);

      // then
      verify(userFromTokenExtractor).extractUserFromToken(request.getToken());
    }

    @Test
    @DisplayName("should encode password")
    void should_encode_password() {
      // given
      ResetPasswordRequest request = new ResetPasswordRequest("eyFoobar", "new-password");
      when(userFromTokenExtractor.extractUserFromToken(any())).thenReturn(mock(UserEntity.class));

      // when
      underTest.resetPassword(request);

      // then
      verify(passwordEncoder).encode(request.getNewPlainPassword());
    }

    @Test
    @DisplayName("should set new password as encoded value")
    void should_set_new_password_as_encoded_value() {
      // given
      ResetPasswordRequest request = new ResetPasswordRequest("eyFoobar", "new-password");
      var user = mock(UserEntity.class);
      String encodedPassword = "new-password-encoded";
      when(passwordEncoder.encode(any())).thenReturn(encodedPassword);
      when(userFromTokenExtractor.extractUserFromToken(any())).thenReturn(user);

      // when
      underTest.resetPassword(request);

      // then
      verify(user).setPassword(encodedPassword);
    }
  }
}
