package com.metalr2.config.misc;

import com.metalr2.model.user.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Component
@Profile({"dev"})
public class DevelopmentDbInitializer implements ApplicationRunner {

  @PersistenceContext
  private final EntityManager entityManager;

  @Autowired
  public DevelopmentDbInitializer(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  @Override
  @Transactional
  public void run(ApplicationArguments args) {
    UserEntity johnDoe = new UserEntity();
    johnDoe.setUsername("JohnD");
    johnDoe.setEmail("john.doe@example.com");
    johnDoe.setEncryptedPassword("$2a$10$2IevDskxEeSmy7Sy41Xl7.u22hTcw3saxQghS.bWaIx3NQrzKTvxK");
    johnDoe.setEnabled(true);

    UserEntity mariaThompson = new UserEntity();
    mariaThompson.setUsername("MariaT");
    mariaThompson.setEmail("maria.thompson@example.com");
    mariaThompson.setEncryptedPassword("$2a$10$fiWhbakTv3lFCiz6weDJXO/qZuzUL.uLJFOkQuquOnRGIJaoJGKpS");
    mariaThompson.setEnabled(true);

    UserEntity mikeMiller = new UserEntity();
    mikeMiller.setUsername("MikeM");
    mikeMiller.setEmail("mike.miller@example.com");
    mikeMiller.setEncryptedPassword("$2a$10$ymg5PpCHQ.bp7RTynUzxzeLGfHN2.0K6y0q7NLlZ/d01zkhN1cb8W");
    mikeMiller.setEnabled(false);

    entityManager.persist(johnDoe);
    entityManager.persist(mariaThompson);
    entityManager.persist(mikeMiller);
  }

}
