package rocks.metaldetector.persistence.domain.user;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import rocks.metaldetector.persistence.domain.artist.ArtistEntity;

import java.util.Collections;
import java.util.Set;

@SpringJUnitConfig
class UserEntityTest implements WithAssertions {

  @Autowired
  private BCryptPasswordEncoder passwordEncoder;

  @DisplayName("Public Id tests")
  @Nested
  class PublicIdTests {

    @Test
    @DisplayName("It is not allowed to set a new value for 'publicId'")
    void reset_public_id_should_not_be_possible() {
      // given
      UserEntity user = UserFactory.createUser("Test", "test@test.com");
      user.setPublicId("foo");

      // when
      Throwable throwable = catchThrowable(() -> user.setPublicId("bar"));

      // then
      assertThat(throwable).isInstanceOf(UnsupportedOperationException.class);
    }
  }

  @DisplayName("Username tests")
  @Nested
  class UsernameTests {

    @Test
    @DisplayName("setUsername() should throw exception when try to update the current username")
    void set_username_should_throw_exception() {
      UserEntity user = UserFactory.createUser("Test", "test@test.com");

      Throwable throwable = catchThrowable(() -> user.setUsername("new username"));

      assertThat(throwable).isInstanceOf(UnsupportedOperationException.class);
      assertThat(throwable).hasMessage("The username must not be changed.");
    }
  }

  @DisplayName("User role tests")
  @Nested
  class UserRoleTests {

    @Test
    @DisplayName("isUser() should return true for user of role 'ROLE_USER'")
    void is_user_should_return_true_for_user_of_role_user() {
      UserEntity user = UserFactory.createUser("User", "user@test.com");

      assertThat(user.isUser()).isTrue();
      assertThat(user.isAdministrator()).isFalse();
    }

    @Test
    @DisplayName("isAdministrator() should return true for user of role 'ROLE_ADMINISTRATOR'")
    void is_administrator_should_return_true_for_user_of_role_administrator() {
      UserEntity user = UserFactory.createAdministrator();

      assertThat(user.isUser()).isFalse();
      assertThat(user.isAdministrator()).isTrue();
    }

    @Test
    @DisplayName("changing roles should be possible")
    void changing_user_roles_should_be_possible() {
      UserEntity user = UserFactory.createUser("user", "mail");

      assertThat(user.isAdministrator()).isFalse();

      user.setUserRoles(UserRole.createAdministratorRole());

      assertThat(user.isAdministrator()).isTrue();
    }

    @Test
    @DisplayName("removing roles should be possible")
    void removing_user_roles_should_be_possible() {
      UserEntity user = UserFactory.createUser("user", "mail");

      assertThat(user.isAdministrator()).isFalse();

      Set<UserRole> roles = UserRole.createAdministratorRole();
      roles.addAll(UserRole.createUserRole());

      user.setUserRoles(roles);

      assertThat(user.isAdministrator()).isTrue();
      assertThat(user.isUser()).isTrue();

      boolean removeResult = user.removeUserRole(UserRole.ROLE_ADMINISTRATOR);
      assertThat(user.isAdministrator()).isFalse();
      assertThat(user.isUser()).isTrue();
      assertThat(removeResult).isTrue();
    }

    @Test
    @DisplayName("removing the last role should throw an exception")
    void removing_last_role_should_throw_exception() {
      UserEntity user = UserFactory.createAdministrator();

      Throwable removeLastRole = catchThrowable(() -> user.removeUserRole(UserRole.ROLE_ADMINISTRATOR));

      assertThat(removeLastRole).isInstanceOf(IllegalStateException.class);
      assertThat(removeLastRole).hasMessage("At least one user role must be set!");
    }

    @Test
    @DisplayName("updating the user roles with an empty set should throw an exception")
    void update_of_user_roles_with_empty_set_should_throw_exception() {
      UserEntity user = UserFactory.createAdministrator();

      Throwable setEmptyCollection = catchThrowable(() -> user.setUserRoles(Collections.emptySet()));

      assertThat(setEmptyCollection).isInstanceOf(IllegalArgumentException.class);
      assertThat(setEmptyCollection).hasMessage("At least one user role must be set!");
    }

    @Test
    @DisplayName("updating the user roles with an null set should throw an exception")
    void update_of_user_roles_with_null_value_should_throw_exception() {
      UserEntity user = UserFactory.createAdministrator();

      Throwable setNullValue = catchThrowable(() -> user.setUserRoles(null));

      assertThat(setNullValue).isInstanceOf(IllegalArgumentException.class);
      assertThat(setNullValue).hasMessage("At least one user role must be set!");
    }
  }

  @DisplayName("Email tests")
  @Nested
  class EmailTests {

    @Test
    @DisplayName("updating the email should be possible")
    void update_of_email_should_be_possible() {
      String initialEmail = "test@test.com";
      String newEmail     = "test-update@test.com";

      UserEntity user = UserFactory.createUser("user", initialEmail);
      assertThat(user.getEmail()).isEqualTo(initialEmail);

      user.setEmail(newEmail);
      assertThat(user.getEmail()).isEqualTo(newEmail);

      user.setEmail(null);
      assertThat(user.getEmail()).isEmpty();
    }
  }

  @DisplayName("Password tests")
  @Nested
  class PasswordTests {

    @Test
    @DisplayName("updating the password with valid values should be possible")
    void update_of_password_with_valid_value_should_be_possible() {
      String newEncryptedPassword = passwordEncoder.encode("test1234");
      UserEntity user = UserFactory.createAdministrator();

      assertThat(user.getPassword()).isNotEqualTo(newEncryptedPassword);
      user.setPassword(newEncryptedPassword);
      assertThat(user.getPassword()).isEqualTo(newEncryptedPassword);
    }

    @Test
    @DisplayName("updating the password with a null value should throw an exception")
    void update_password_with_null_value_should_throw_exception() {
      UserEntity user = UserFactory.createAdministrator();

      Throwable setNullPassword = catchThrowable(() -> user.setPassword(null));

      assertThat(setNullPassword).isInstanceOf(IllegalArgumentException.class);
      assertThat(setNullPassword).hasMessage("It seems that the new password has not been correctly encrypted.");
    }

    @Test
    @DisplayName("updating the password with an empty value should throw an exception")
    void update_password_with_empty_value_should_throw_exception() {
      UserEntity user = UserFactory.createAdministrator();

      Throwable setEmptyPassword = catchThrowable(() -> user.setPassword(""));

      assertThat(setEmptyPassword).isInstanceOf(IllegalArgumentException.class);
      assertThat(setEmptyPassword).hasMessage("It seems that the new password has not been correctly encrypted.");
    }
  }

  @DisplayName("Following artists tests")
  @Nested
  class FollowingArtistsTests {

    @Test
    @DisplayName("Following an artist add the artist to user and user to artist")
    void adding_artist_adds_artist_and_user() {
      // given
      UserEntity user = UserFactory.createUser("user", "email");
      ArtistEntity artist = ArtistFactory.withDiscogsId(1L);
      user.addFollowedArtist(artist);

      // when
      user.addFollowedArtist(artist);

      // then
      assertThat(user.getFollowedArtists()).containsExactly(artist);
      assertThat(artist.getFollowedByUsers()).containsExactly(user);
    }

    @Test
    @DisplayName("Unfollowing an artist removes the artist from user and user from artist")
    void removing_artist_removes_artist_and_user() {
      // given
      UserEntity user = UserFactory.createUser("user", "email");
      ArtistEntity artist = ArtistFactory.withDiscogsId(1L);
      user.addFollowedArtist(artist);

      // when
      user.removeFollowedArtist(artist);

      // then
      assertThat(user.getFollowedArtists()).doesNotContain(artist);
      assertThat(artist.getFollowedByUsers()).doesNotContain(user);
    }

    @Test
    @DisplayName("Should return true if user follows artist")
    void should_return_true_for_following() {
      // given
      UserEntity user = UserFactory.createUser("user", "email");
      ArtistEntity artist = ArtistFactory.withDiscogsId(1L);
      user.addFollowedArtist(artist);

      // when
      boolean result = user.isFollowing(artist.getArtistDiscogsId());

      // then
      assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Should return false if user does not follow artist")
    void should_return_false_for_following() {
      // given
      UserEntity user = UserFactory.createUser("user", "email");
      ArtistEntity artist = ArtistFactory.withDiscogsId(1L);

      // when
      boolean result = user.isFollowing(artist.getArtistDiscogsId());

      // then
      assertThat(result).isFalse();
    }
  }

  @TestConfiguration
  static class UserEntityTestConfiguration {

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
      return new BCryptPasswordEncoder();
    }

  }
}
