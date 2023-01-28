package rocks.metaldetector.service.user;

import io.jsonwebtoken.Claims;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.metaldetector.persistence.domain.user.UserRepository;
import rocks.metaldetector.support.JwtsSupport;
import rocks.metaldetector.support.exceptions.ResourceNotFoundException;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserFromTokenExtractorTest implements WithAssertions {

  @Mock
  private JwtsSupport jwtsSupport;

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private UserFromTokenExtractor underTest;

  @AfterEach
  void tearDown() {
    reset(userRepository, jwtsSupport);
  }

  @Test
  @DisplayName("should get claims from jwts support")
  void should_get_claims_from_jwts_support() {
    // given
    String token = "eyFoo";
    when(jwtsSupport.getClaims(any())).thenReturn(mock(Claims.class));
    when(userRepository.findByPublicId(any())).thenReturn(Optional.of(UserEntityFactory.createDefaultUser()));

    // when
    underTest.extractUserFromToken(token);

    // then
    verify(jwtsSupport).getClaims(token);
  }

  @Test
  @DisplayName("should find user by public id from claims")
  void should_find_user_by_public_id_from_claims() {
    // given
    var claims = mock(Claims.class);
    var publicId = UUID.randomUUID().toString();
    when(claims.getSubject()).thenReturn(publicId);
    when(jwtsSupport.getClaims(any())).thenReturn(claims);
    when(userRepository.findByPublicId(any())).thenReturn(Optional.of(UserEntityFactory.createDefaultUser()));

    // when
    underTest.extractUserFromToken("eyFoo");

    // then
    verify(userRepository).findByPublicId(publicId);
  }

  @Test
  @DisplayName("should throw exception if no user was found")
  void should_throw_exception_if_no_user_was_found() {
    // given
    when(jwtsSupport.getClaims(any())).thenReturn(mock(Claims.class));
    when(userRepository.findByPublicId(any())).thenReturn(Optional.empty());

    // when
    Throwable throwable = catchThrowable(() -> underTest.extractUserFromToken("eyFoo"));

    // then
    assertThat(throwable).isInstanceOf(ResourceNotFoundException.class);
  }
}
