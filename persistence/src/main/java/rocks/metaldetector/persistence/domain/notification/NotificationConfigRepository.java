package rocks.metaldetector.persistence.domain.notification;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NotificationConfigRepository extends JpaRepository<NotificationConfigEntity, Long> {

  Optional<NotificationConfigEntity> findByUserId(Long userId);

  void deleteByUserId(Long userId);
}
