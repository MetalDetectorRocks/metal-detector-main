package rocks.metaldetector.persistence.config.bootstrap;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.AllArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import rocks.metaldetector.persistence.domain.user.AbstractUserEntity;
import rocks.metaldetector.persistence.domain.user.UserEntity;
import rocks.metaldetector.persistence.domain.user.UserRole;

import java.util.List;

@Component
@Profile({"preview", "prod"})
@AllArgsConstructor
public class DatabaseInitializer implements ApplicationRunner {

  @PersistenceContext
  private final EntityManager entityManager;

  @Override
  @Transactional
  public void run(ApplicationArguments args) {
    List<AbstractUserEntity> currentExistingUser = entityManager.createQuery("select u from users u", AbstractUserEntity.class).getResultList();
    if (currentExistingUser.isEmpty()) {
      createDefaultAdministrator();
    }
  }

  private void createDefaultAdministrator() {
    UserEntity administrator = UserEntity.builder()
            .username("Administrator")
            .email("administrator@example.com")
            .password("$2a$10$Wm9V0xfkgB5io3gNKom91eqzwlTrQArY9L3.6m2m9aKynA8hV.KU2")
            .enabled(true)
            .userRoles(UserRole.createAdministratorRole())
            .build();

    entityManager.persist(administrator);
  }
}
