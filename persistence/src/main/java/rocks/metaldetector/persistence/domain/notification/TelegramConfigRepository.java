package rocks.metaldetector.persistence.domain.notification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rocks.metaldetector.persistence.domain.user.AbstractUserEntity;

import java.util.Optional;

@Repository
public interface TelegramConfigRepository extends JpaRepository<TelegramConfigEntity, Long> {

  Optional<TelegramConfigEntity> findByNotificationConfig(NotificationConfigEntity notificationConfigEntity);

  @Query("select t from telegramConfigs t join t.notificationConfig n where n.user = :user and n.channel = 'TELEGRAM'")
  Optional<TelegramConfigEntity> findByUser(@Param("user") AbstractUserEntity user);

  @Query("delete from telegramConfigs t where t.notificationConfig in (select n from notificationConfigs n where n.user = :user and n.channel = 'TELEGRAM')")
  @Modifying
  void deleteByUser(@Param("user") AbstractUserEntity user);

  boolean existsByRegistrationId(Integer registrationId);

  Optional<TelegramConfigEntity> findByRegistrationId(Integer registrationId);
}
