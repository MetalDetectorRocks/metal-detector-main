package rocks.metaldetector.persistence.config.bootstrap;

import lombok.AllArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import rocks.metaldetector.persistence.domain.artist.ArtistEntity;
import rocks.metaldetector.persistence.domain.user.UserEntity;
import rocks.metaldetector.persistence.domain.user.UserRole;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import java.util.List;

import static rocks.metaldetector.persistence.domain.artist.ArtistSource.DISCOGS;

@Component
@AllArgsConstructor
public class DefaultDatabaseInitializer implements ApplicationRunner {

  @PersistenceContext
  private final EntityManager entityManager;
  private final DataSource dataSource;

  private static final String OPETH_DISCOGS_ID = "245797";
  private static final String DARKTHRONE_DISCOGS_ID = "252211";
  private static final String MAYHEM_DISCOGS_ID = "14092";

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
    createAndFollowArtists();
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

  private void createAndFollowArtists() {
    UserEntity administrator = entityManager.createQuery("select u from users u where u.username = :username", UserEntity.class).setParameter("username", "Administrator").getSingleResult();

    ArtistEntity opeth = new ArtistEntity(OPETH_DISCOGS_ID, "Opeth", "https://img.discogs.com/_ejoULEnb6ub_-_6fUoLW0ZS6C8=/150x150/smart/filters:strip_icc():format(jpeg):mode_rgb():quality(40)/discogs-images/A-245797-1584786531-2513.jpeg.jpg", DISCOGS);
    ArtistEntity darkthrone = new ArtistEntity(DARKTHRONE_DISCOGS_ID, "Darkthrone", "https://img.discogs.com/z6M8OMNo7GXZR9PzQF8WvaqMvXw=/150x150/smart/filters:strip_icc():format(jpeg):mode_rgb():quality(40)/discogs-images/A-252211-1579868454-4269.jpeg.jpg", DISCOGS);
    ArtistEntity mayhem = new ArtistEntity(MAYHEM_DISCOGS_ID, "Mayhem", "https://img.discogs.com/ZtM5dcXMOugk9djxyVN7T6BJm7M=/150x150/smart/filters:strip_icc():format(jpeg):mode_rgb():quality(40)/discogs-images/A-14092-1551425950-6112.jpeg.jpg", DISCOGS);

    entityManager.persist(opeth);
    entityManager.persist(darkthrone);
    entityManager.persist(mayhem);

    administrator.addFollowedArtist(opeth);
    administrator.addFollowedArtist(darkthrone);
    administrator.addFollowedArtist(mayhem);

    entityManager.merge(administrator);
  }
}
