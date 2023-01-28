package rocks.metaldetector.service.auth;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import rocks.metaldetector.service.user.UserDto;
import rocks.metaldetector.service.user.UserService;
import rocks.metaldetector.service.user.events.OnRequestPasswordResetEvent;
import rocks.metaldetector.web.api.auth.PasswordResetRequest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static rocks.metaldetector.testutil.DtoFactory.*;

@ExtendWith(MockitoExtension.class)
class PasswordResetServiceTest implements WithAssertions {

  @Mock
  private UserService userService;

  @Mock
  private ApplicationEventPublisher eventPublisher;

  @InjectMocks
  private PasswordResetService underTest;

  @AfterEach
  void afterEach() {
    reset(userService, eventPublisher);
  }

  @Test
  @DisplayName("should fetch user via user service")
  void should_fetch_user_via_user_service() {
    // given
    String email = "test@example.com";
    PasswordResetRequest request = new PasswordResetRequest(email);

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
    underTest.requestPasswordReset(new PasswordResetRequest("test@example.com"));

    // then
    ArgumentCaptor<OnRequestPasswordResetEvent> captor = ArgumentCaptor.forClass(OnRequestPasswordResetEvent.class);
    verify(eventPublisher).publishEvent(captor.capture());
    assertThat(captor.getValue().getSource()).isEqualTo(underTest);
    assertThat(captor.getValue().getUserDto()).isEqualTo(userDto);
  }
}
