package rocks.metaldetector.persistence.config.bootstrap;

import lombok.AllArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import rocks.metaldetector.persistence.domain.artist.ArtistEntity;
import rocks.metaldetector.persistence.domain.artist.FollowedArtistEntity;
import rocks.metaldetector.persistence.domain.user.UserEntity;
import rocks.metaldetector.persistence.domain.user.UserRole;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import java.util.List;

@Component
@Profile("default")
@AllArgsConstructor
public class DefaultDatabaseInitializer implements ApplicationRunner {

  @PersistenceContext
  private final EntityManager entityManager;
  private final DataSource dataSource;

  private final long OPETH_DISCOGS_ID = 245797L;
  private final long DARKTHRONE_DISCOGS_ID = 252211L;
  private final long MAYHEM_DISCOGS_ID = 252211L;

  @Override
  @Transactional
  public void run(ApplicationArguments args) {
    if (!(dataSource instanceof EmbeddedDatabase)) {
      List<UserEntity> currentExistingUser = entityManager.createQuery("select u from users u", UserEntity.class).getResultList();

      // It is assumed that the database has no demo data if there are no users
      if (currentExistingUser.isEmpty()) {
        createDemoData();
      }
    }
  }

  private void createDemoData() {
    createUser();
    createAdministrator();
    createArtists();
    createFollowedArtists();
  }

  private void createUser() {
    UserEntity johnDoe = UserEntity.builder()
        .username("JohnD")
        .email("john.doe@example.com")
        .password("$2a$10$2IevDskxEeSmy7Sy41Xl7.u22hTcw3saxQghS.bWaIx3NQrzKTvxK")
        .enabled(true)
        .userRoles(UserRole.createUserRole())
        .build();

    UserEntity mariaThompson = UserEntity.builder()
        .username("MariaT")
        .email("maria.thompson@example.com")
        .password("$2a$10$fiWhbakTv3lFCiz6weDJXO/qZuzUL.uLJFOkQuquOnRGIJaoJGKpS")
        .enabled(true)
        .userRoles(UserRole.createUserRole())
        .build();

    UserEntity mikeMiller = UserEntity.builder()
        .username("MikeM")
        .email("mike.miller@example.com")
        .password("$2a$10$ymg5PpCHQ.bp7RTynUzxzeLGfHN2.0K6y0q7NLlZ/d01zkhN1cb8W")
        .enabled(false)
        .userRoles(UserRole.createUserRole())
        .build();

    entityManager.persist(johnDoe);
    entityManager.persist(mariaThompson);
    entityManager.persist(mikeMiller);
  }

  private void createAdministrator() {
    UserEntity administrator = UserEntity.builder()
        .username("Administrator")
        .email("administrator@example.com")
        .password("$2a$10$SHReFWSMijakmT6GTC/EN.kLY4RYvqfcLsjDibRMEMeYCqPPbcNE6")
        .enabled(true)
        .userRoles(UserRole.createAdministratorRole())
        .build();

    entityManager.persist(administrator);
  }

  private void createArtists() {
    ArtistEntity opeth = new ArtistEntity(OPETH_DISCOGS_ID, "Opeth", null);
    ArtistEntity darkthrone = new ArtistEntity(DARKTHRONE_DISCOGS_ID, "Darkthrone", null);
    ArtistEntity mayhem = new ArtistEntity(MAYHEM_DISCOGS_ID, "Mayhem", null);

    entityManager.persist(opeth);
    entityManager.persist(darkthrone);
    entityManager.persist(mayhem);
  }

  private void createFollowedArtists() {
    UserEntity administrator = entityManager.createQuery("select u from users u where u.username = :username", UserEntity.class).setParameter("username", "Administrator").getSingleResult();
    FollowedArtistEntity opeth = new FollowedArtistEntity(administrator.getPublicId(), OPETH_DISCOGS_ID);
    FollowedArtistEntity darkthrone = new FollowedArtistEntity(administrator.getPublicId(), DARKTHRONE_DISCOGS_ID);
    FollowedArtistEntity mayhem = new FollowedArtistEntity(administrator.getPublicId(), MAYHEM_DISCOGS_ID);

    entityManager.persist(opeth);
    entityManager.persist(darkthrone);
    entityManager.persist(mayhem);
  }
}
