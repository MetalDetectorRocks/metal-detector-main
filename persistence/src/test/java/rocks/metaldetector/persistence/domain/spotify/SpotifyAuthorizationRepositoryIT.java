package rocks.metaldetector.persistence.domain.spotify;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import rocks.metaldetector.persistence.BaseDataJpaTest;
import rocks.metaldetector.persistence.domain.user.UserEntity;
import rocks.metaldetector.persistence.domain.user.UserFactory;
import rocks.metaldetector.persistence.domain.user.UserRepository;

class SpotifyAuthorizationRepositoryIT extends BaseDataJpaTest implements WithAssertions {

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
  @DisplayName("should find spotifyAuthorization by user")
  void should_find_spotify_authorization_entity_by_user() {
    // when
    var result = underTest.findByUser(USER);

    // then
    assertThat(result).isPresent();
    assertThat(result.get()).isEqualTo(SPOTIFY_AUTHORIZATION);
  }

  @Test
  @DisplayName("should delete spotifyAuthorization by user")
  void test_delete_by_user() {
    // when
    underTest.deleteByUser(USER);
    var result = underTest.findByUser(USER);

    // then
    assertThat(result).isEmpty();
  }
}
