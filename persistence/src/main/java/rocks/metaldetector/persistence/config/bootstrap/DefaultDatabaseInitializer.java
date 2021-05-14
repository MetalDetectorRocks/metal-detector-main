package rocks.metaldetector.persistence.config.bootstrap;

import lombok.AllArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import rocks.metaldetector.persistence.domain.artist.ArtistEntity;
import rocks.metaldetector.persistence.domain.artist.FollowActionEntity;
import rocks.metaldetector.persistence.domain.user.AbstractUserEntity;
import rocks.metaldetector.persistence.domain.user.UserEntity;
import rocks.metaldetector.persistence.domain.user.UserRole;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import java.util.List;

import static rocks.metaldetector.persistence.domain.artist.ArtistSource.SPOTIFY;

@Component
@AllArgsConstructor
@Profile({"default", "mockmode"})
public class DefaultDatabaseInitializer implements ApplicationRunner {

  @PersistenceContext
  private final EntityManager entityManager;
  private final DataSource dataSource;

  private static final String OPETH_SPOTIFY_ID = "0ybFZ2Ab08V8hueghSXm6E";
  private static final String DARKTHRONE_SPOTIFY_ID = "7kWnE981vITXDnAD2cZmCV";
  private static final String MAYHEM_SPOTIFY_ID = "0dR10i73opHXuRuLbgxltM";

  @Override
  @Transactional
  public void run(ApplicationArguments args) {
    if (!(dataSource instanceof EmbeddedDatabase)) {
      List<AbstractUserEntity> currentExistingUser = entityManager.createQuery("select u from users u", AbstractUserEntity.class).getResultList();

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
    AbstractUserEntity administrator = entityManager.createQuery("select u from users u where u.username = :username", AbstractUserEntity.class)
            .setParameter("username", "Administrator").getSingleResult();

    ArtistEntity opeth = ArtistEntity.builder()
            .externalId(OPETH_SPOTIFY_ID)
            .artistName("Opeth")
            .source(SPOTIFY)
            .imageL("https://img.discogs.com/_ejoULEnb6ub_-_6fUoLW0ZS6C8=/150x150/smart/filters:strip_icc():format(jpeg):mode_rgb():quality(40)/discogs-images/A-245797-1584786531-2513.jpeg.jpg")
            .build();

    ArtistEntity darkthrone = ArtistEntity.builder()
            .externalId(DARKTHRONE_SPOTIFY_ID)
            .artistName("Darkthrone")
            .source(SPOTIFY)
            .imageL("https://img.discogs.com/z6M8OMNo7GXZR9PzQF8WvaqMvXw=/150x150/smart/filters:strip_icc():format(jpeg):mode_rgb():quality(40)/discogs-images/A-252211-1579868454-4269.jpeg.jpg")
            .build();

    ArtistEntity mayhem = ArtistEntity.builder()
            .externalId(MAYHEM_SPOTIFY_ID)
            .artistName("Mayhem")
            .source(SPOTIFY)
            .imageL("https://img.discogs.com/ZtM5dcXMOugk9djxyVN7T6BJm7M=/150x150/smart/filters:strip_icc():format(jpeg):mode_rgb():quality(40)/discogs-images/A-14092-1551425950-6112.jpeg.jpg")
            .build();

    entityManager.persist(opeth);
    entityManager.persist(darkthrone);
    entityManager.persist(mayhem);

    FollowActionEntity opethFollowAction = FollowActionEntity.builder().artist(opeth).user(administrator).build();
    FollowActionEntity darkthroneFollowAction = FollowActionEntity.builder().artist(darkthrone).user(administrator).build();
    FollowActionEntity mayhemFollowAction = FollowActionEntity.builder().artist(mayhem).user(administrator).build();

    entityManager.persist(opethFollowAction);
    entityManager.persist(darkthroneFollowAction);
    entityManager.persist(mayhemFollowAction);
  }
}

