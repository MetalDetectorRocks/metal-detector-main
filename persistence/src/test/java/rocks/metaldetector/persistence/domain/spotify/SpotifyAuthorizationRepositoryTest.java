package rocks.metaldetector.persistence.domain.spotify;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import rocks.metaldetector.persistence.BaseDataJpaTest;
import rocks.metaldetector.persistence.WithIntegrationTestConfig;
import rocks.metaldetector.persistence.domain.user.UserEntity;
import rocks.metaldetector.persistence.domain.user.UserFactory;
import rocks.metaldetector.persistence.domain.user.UserRepository;

class SpotifyAuthorizationRepositoryTest extends BaseDataJpaTest implements WithAssertions, WithIntegrationTestConfig {

  private static UserEntity USER;
  private static SpotifyAuthorizationEntity SPOTIFY_AUTHORIZATION;

  @Autowired
  private SpotifyAuthorizationRepository underTest;

  @Autowired
  private UserRepository userRepository;

  @BeforeEach
  void setup() {
    USER = UserFactory.createUser("user", "user@example.com");
    SPOTIFY_AUTHORIZATION = new SpotifyAuthorizationEntity(USER, "sample-state");
    userRepository.save(USER);
    underTest.save(SPOTIFY_AUTHORIZATION);
  }

  @AfterEach
  void tearDown() {
    underTest.deleteAll();
    userRepository.deleteAll();
  }

  @Test
  @DisplayName("should find spotify authorization entity by user id")
  void should_find_spotify_authorization_entity_by_public_user_id() {
    // when
    var result = underTest.findByUserId(USER.getId());

    // then
    assertThat(result).isPresent();
    assertThat(result.get()).isEqualTo(SPOTIFY_AUTHORIZATION);
  }

  @Test
  @DisplayName("should return empty Optional if no spotify authorization could be found")
  void should_return_empty_optional_if_no_spotify_authorization_could_be_found() {
    // when
    var result = underTest.findByUserId(123456L);

    // then
    assertThat(result).isNotPresent();
  }
}
