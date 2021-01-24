package rocks.metaldetector.service.user.events;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import rocks.metaldetector.persistence.domain.user.UserEntity;
import rocks.metaldetector.service.user.UserEntityFactory;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserDeletionEventPublisherTest implements WithAssertions {

  @Mock
  private ApplicationEventPublisher applicationEventPublisher;

  @InjectMocks
  private UserDeletionEventPublisher underTest;

  private UserEntity userEntity;

  @BeforeEach
  void setup() {
    userEntity = UserEntityFactory.createUser("user", "user@mail.com");
  }

  @AfterEach
  void tearDown() {
    reset(applicationEventPublisher);
  }

  @Test
  @DisplayName("UserDeletionEvent is published")
  void test_user_deletion_event_published() {
    // given
    ArgumentCaptor<UserDeletionEvent> argumentCaptor = ArgumentCaptor.forClass(UserDeletionEvent.class);

    // when
    underTest.publishUserDeletionEvent(userEntity);

    // then
    verify(applicationEventPublisher).publishEvent(argumentCaptor.capture());
    UserDeletionEvent userDeletionEvent = argumentCaptor.getValue();

    assertThat(userDeletionEvent.getUserEntity()).isEqualTo(userEntity);
    assertThat(userDeletionEvent.getSource()).isEqualTo(underTest);
  }
}