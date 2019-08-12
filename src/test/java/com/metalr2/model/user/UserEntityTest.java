package com.metalr2.model.user;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class UserEntityTest {

  @Autowired
  private UserRepository userRepository;

  @EnableJpaAuditing
  @TestConfiguration
  static class MyTestConfiguration {

    @Bean
    public AuditorAware<String> auditorAware() {
      return () -> Optional.of("ANONYMOUS");
    }

  }

  @BeforeEach
  void setUp() {
  }

  @AfterEach
  void tearDown() {
  }

  @Test
  void jpaAuditingFieldsShouldBeNotNull() {
    UserEntity user = UserFactory.createUser("Test", "test@test.com");

    assertNull(user.getCreatedBy());
    assertNull(user.getCreatedDate());
    assertNull(user.getLastModifiedBy());
    assertNull(user.getLastModifiedDate());

    userRepository.save(user);

    assertEquals("ANONYMOUS", user.getCreatedBy());
    assertNull(user.getCreatedDate());
    assertEquals("ANONYMOUS", user.getLastModifiedBy());
    assertNull(user.getLastModifiedDate());
  }

  @Test
  void setUsernameShouldThrowException() {
  }

  @Test
  void isUserShouldReturnTrue() {
  }

  @Test
  void isUserShouldReturnFalse() {
  }

  @Test
  void isAdministratorShouldReturnTrue() {
  }

  @Test
  void isAdministratorShouldReturnFalse() {
  }

  @Test
  void isSuperUserShouldReturnTrue() {
  }

  @Test
  void isSuperUserShouldReturnFalse() {
  }
}