package rocks.metaldetector.persistence.domain.notification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rocks.metaldetector.persistence.domain.user.AbstractUserEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationConfigRepository extends JpaRepository<NotificationConfigEntity, Long> {

  List<NotificationConfigEntity> findAllByUser(AbstractUserEntity user);

  Optional<NotificationConfigEntity> findByUserAndChannel(AbstractUserEntity user, NotificationChannel channel);

  void deleteAllByUser(AbstractUserEntity user);
}
