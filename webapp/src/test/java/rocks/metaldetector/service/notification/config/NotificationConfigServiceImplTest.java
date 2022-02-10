package rocks.metaldetector.service.notification.config;

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
import rocks.metaldetector.persistence.domain.notification.NotificationConfigEntity;
import rocks.metaldetector.persistence.domain.notification.NotificationConfigRepository;
import rocks.metaldetector.persistence.domain.user.UserEntity;
import rocks.metaldetector.security.AuthenticationFacade;
import rocks.metaldetector.service.user.UserEntityFactory;
import rocks.metaldetector.telegram.facade.TelegramMessagingService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static rocks.metaldetector.persistence.domain.notification.NotificationChannel.EMAIL;
import static rocks.metaldetector.persistence.domain.notification.NotificationChannel.TELEGRAM;

@ExtendWith(MockitoExtension.class)
class NotificationConfigServiceImplTest implements WithAssertions {

  @Mock
  private NotificationConfigRepository notificationConfigRepository;

  @Mock
  private NotificationConfigTransformer notificationConfigTransformer;

  @Mock
  private AuthenticationFacade authenticationFacade;

  @Mock
  private TelegramMessagingService telegramMessagingService;

  @InjectMocks
  private NotificationConfigServiceImpl underTest;

  @AfterEach
  void tearDown() {
    reset(notificationConfigRepository, notificationConfigTransformer, authenticationFacade, telegramMessagingService);
  }

  @DisplayName("Tests for getting notification config")
  @Nested
  class GetNotificationConfigTest {

    @Test
    @DisplayName("Getting current user's config calls currentUserSupplier")
    void test_get_config_calls_current_user_supplier() {
      // given
      UserEntity userEntity = UserEntityFactory.createUser("name", "mail@mail.mail");
      doReturn(userEntity).when(authenticationFacade).getCurrentUser();
      doReturn(List.of(NotificationConfigEntity.builder().user(userEntity).build())).when(notificationConfigRepository).findAllByUser(any());

      // when
      underTest.getCurrentUserNotificationConfigs();

      // then
      verify(authenticationFacade).getCurrentUser();
    }

    @Test
    @DisplayName("Getting current user's config calls notificationConfigRepository")
    void test_get_config_calls_notification_repo() {
      // given
      var mockUser = mock(UserEntity.class);
      doReturn(mockUser).when(authenticationFacade).getCurrentUser();
      doReturn(List.of(NotificationConfigEntity.builder().user(mockUser).build())).when(notificationConfigRepository).findAllByUser(any());

      // when
      underTest.getCurrentUserNotificationConfigs();

      // then
      verify(notificationConfigRepository).findAllByUser(mockUser);
    }

    @Test
    @DisplayName("Getting current user's config returns empty list when id not found")
    void test_get_config_throws_exception() {
      // given
      var mockUser = mock(UserEntity.class);
      doReturn(mockUser).when(authenticationFacade).getCurrentUser();
      doReturn(Collections.emptyList()).when(notificationConfigRepository).findAllByUser(any());

      // when
      var result = underTest.getCurrentUserNotificationConfigs();

      // then
      assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Getting current user's config calls notificationConfigTransformer for each config")
    void test_get_config_calls_notification_config_trafo() {
      // given
      var userEntity = UserEntityFactory.createUser("name", "mail@mail.mail");
      var notificationConfigEntity1 = NotificationConfigEntity.builder().user(userEntity).channel(EMAIL).build();
      var notificationConfigEntity2 = NotificationConfigEntity.builder().user(userEntity).channel(TELEGRAM).build();
      doReturn(userEntity).when(authenticationFacade).getCurrentUser();
      doReturn(List.of(notificationConfigEntity1, notificationConfigEntity2)).when(notificationConfigRepository).findAllByUser(any());

      // when
      underTest.getCurrentUserNotificationConfigs();

      // then
      verify(notificationConfigTransformer).transform(notificationConfigEntity1);
      verify(notificationConfigTransformer).transform(notificationConfigEntity2);
    }

    @Test
    @DisplayName("Getting current user's config returns dtos")
    void test_get_config_returns_dtos() {
      // given
      var userEntity = UserEntityFactory.createUser("name", "mail@mail.mail");
      var notificationConfigEntity1 = NotificationConfigEntity.builder().user(userEntity).channel(TELEGRAM).build();
      var notificationConfigEntity2 = NotificationConfigEntity.builder().user(userEntity).channel(EMAIL).build();
      var notificationConfigDto1 = NotificationConfigDto.builder().frequencyInWeeks(4).build();
      var notificationConfigDto2 = NotificationConfigDto.builder().frequencyInWeeks(2).build();
      doReturn(userEntity).when(authenticationFacade).getCurrentUser();
      doReturn(List.of(notificationConfigEntity1, notificationConfigEntity2)).when(notificationConfigRepository).findAllByUser(any());
      doReturn(notificationConfigDto1).when(notificationConfigTransformer).transform(notificationConfigEntity1);
      doReturn(notificationConfigDto2).when(notificationConfigTransformer).transform(notificationConfigEntity2);

      // when
      var result = underTest.getCurrentUserNotificationConfigs();

      // then
      assertThat(result).hasSize(2);
      assertThat(result.get(0)).isEqualTo(notificationConfigDto1);
      assertThat(result.get(1)).isEqualTo(notificationConfigDto2);
    }
  }

  @DisplayName("Tests for updating notification config")
  @Nested
  class UpdateNotificationConfigTest {

    @Test
    @DisplayName("Updating current user's config calls currentUserSupplier")
    void test_update_config_calls_current_user_supplier() {
      // given
      var request = NotificationConfigDto.builder().channel("EMAIL").build();
      var userEntity = UserEntityFactory.createUser("name", "mail@mail.mail");
      doReturn(userEntity).when(authenticationFacade).getCurrentUser();
      doReturn(Optional.empty()).when(notificationConfigRepository).findByUserAndChannel(any(), any());

      // when
      underTest.updateCurrentUserNotificationConfig(request);

      // then
      verify(authenticationFacade).getCurrentUser();
    }

    @Test
    @DisplayName("Updating current user's config calls notificationConfigRepository")
    void test_update_config_calls_notification_repo() {
      // given
      var mockUser = mock(UserEntity.class);
      var request = NotificationConfigDto.builder().channel("EMAIL").build();
      doReturn(mockUser).when(authenticationFacade).getCurrentUser();
      doReturn(Optional.empty()).when(notificationConfigRepository).findByUserAndChannel(any(), any());

      // when
      underTest.updateCurrentUserNotificationConfig(request);

      // then
      verify(notificationConfigRepository).findByUserAndChannel(mockUser, EMAIL);
    }

    @Test
    @DisplayName("Existing config is updated")
    void test_updated_config_saved() {
      // given
      var userEntity = UserEntityFactory.createUser("name", "mail@mail.mail");
      var notificationConfig = NotificationConfigEntity.builder()
          .user(userEntity)
          .frequencyInWeeks(2)
          .channel(EMAIL)
          .build();
      var notificationConfigDto = NotificationConfigDto.builder()
          .frequencyInWeeks(4)
          .channel("EMAIL")
          .notifyReissues(true)
          .build();
      doReturn(userEntity).when(authenticationFacade).getCurrentUser();
      doReturn(Optional.of(notificationConfig)).when(notificationConfigRepository).findByUserAndChannel(any(), any());

      // when
      underTest.updateCurrentUserNotificationConfig(notificationConfigDto);

      // then
      verify(notificationConfigRepository).save(notificationConfig);
    }

    @Test
    @DisplayName("Updating current user's config creates new one if it not exists yet")
    void test_update_config_creates_new() {
      // given
      ArgumentCaptor<NotificationConfigEntity> argumentCaptor = ArgumentCaptor.forClass(NotificationConfigEntity.class);
      var mockUser = mock(UserEntity.class);
      var request = NotificationConfigDto.builder().channel("EMAIL").build();
      doReturn(mockUser).when(authenticationFacade).getCurrentUser();
      doReturn(Optional.empty()).when(notificationConfigRepository).findByUserAndChannel(any(), any());

      // when
      underTest.updateCurrentUserNotificationConfig(request);

      // then
      verify(notificationConfigRepository).save(argumentCaptor.capture());
      var savedEntity = argumentCaptor.getValue();
      assertThat(savedEntity.getUser()).isEqualTo(mockUser);
      assertThat(savedEntity.getChannel()).isEqualTo(EMAIL);
    }
  }
}
