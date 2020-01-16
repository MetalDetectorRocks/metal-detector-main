package com.metalr2.config.bootstrap;

import com.metalr2.model.artist.ArtistEntity;
import com.metalr2.model.artist.FollowedArtistEntity;
import com.metalr2.model.user.UserEntity;
import com.metalr2.model.user.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Component
@Profile({"dev"})
public class DevelopmentDbInitializer implements ApplicationRunner {

  @PersistenceContext
  private final EntityManager entityManager;

  private final long OPETH_DISCOGS_ID = 245797L;
  private final long DARKTHRONE_DISCOGS_ID = 252211L;

  @Autowired
  public DevelopmentDbInitializer(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  @Override
  @Transactional
  public void run(ApplicationArguments args) {
    List<UserEntity> currentExistingUser = entityManager.createQuery("select u from users u", UserEntity.class).getResultList();

    // It is assumed that the database has no demo data if there are no users
    if (currentExistingUser.isEmpty()) {
      createUser();
      String publicId = createAdministrator();
      createArtists();
      createFollowedArtists(publicId);
    }
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

  private String createAdministrator() {
    UserEntity administrator = UserEntity.builder()
        .username("Administrator")
        .email("administrator@example.com")
        .password("$2a$10$SHReFWSMijakmT6GTC/EN.kLY4RYvqfcLsjDibRMEMeYCqPPbcNE6")
        .enabled(true)
        .userRoles(UserRole.createSuperUserRole())
        .build();

    entityManager.persist(administrator);

    return administrator.getPublicId();
  }

  private void createArtists() {
    ArtistEntity opeth = new ArtistEntity(OPETH_DISCOGS_ID, "Opeth", null);
    ArtistEntity darkthrone = new ArtistEntity(DARKTHRONE_DISCOGS_ID, "Darkthrone", null);

    entityManager.persist(opeth);
    entityManager.persist(darkthrone);
  }

  private void createFollowedArtists(String userId) {
    FollowedArtistEntity followedArtistEntityOpeth = new FollowedArtistEntity(userId, OPETH_DISCOGS_ID);
    FollowedArtistEntity followedArtistEntityDarkthrone = new FollowedArtistEntity(userId, DARKTHRONE_DISCOGS_ID);

    entityManager.persist(followedArtistEntityOpeth);
    entityManager.persist(followedArtistEntityDarkthrone);
  }

}
