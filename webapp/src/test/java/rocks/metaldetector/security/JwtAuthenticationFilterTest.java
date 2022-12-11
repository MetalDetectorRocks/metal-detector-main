package rocks.metaldetector.security;

import io.jsonwebtoken.Claims;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import rocks.metaldetector.persistence.domain.user.UserEntity;
import rocks.metaldetector.persistence.domain.user.UserRepository;
import rocks.metaldetector.service.user.UserEntityFactory;
import rocks.metaldetector.support.JwtsSupport;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest implements WithAssertions {

  @Mock
  private JwtsSupport jwtsSupport;

  @Mock
  private UserRepository userRepository;

  @Mock
  private AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource;

  @Mock
  private MockHttpServletRequest request;

  @InjectMocks
  private JwtAuthenticationFilter underTest;

  @AfterEach
  void tearDown() {
    reset(jwtsSupport, userRepository, authenticationDetailsSource);
  }

  @Test
  @DisplayName("should validate token from header")
  void should_validate_token_from_header() throws ServletException, IOException {
    // given
    var authHeaderValue = "Bearer eyFoo";
    doReturn(authHeaderValue).when(request).getHeader(AUTHORIZATION);

    // when
    underTest.doFilterInternal(request, new MockHttpServletResponse(), new MockFilterChain());

    // then
    verify(jwtsSupport).validateJwtToken("eyFoo");
  }

  @Test
  @DisplayName("should do nothing if there is no token present")
  void should_do_nothing_if_there_is_no_token_present() throws ServletException, IOException {
    // given
    var authHeaderValue = "";
    doReturn(authHeaderValue).when(request).getHeader(AUTHORIZATION);

    // when
    underTest.doFilterInternal(request, new MockHttpServletResponse(), new MockFilterChain());

    // then
    verifyNoInteractions(jwtsSupport);
    verifyNoInteractions(userRepository);
    verifyNoInteractions(authenticationDetailsSource);

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    assertThat(authentication).isNull();
  }

  @Test
  @DisplayName("should get claims from token")
  void should_get_claims_from_token() throws ServletException, IOException {
    // given
    var authHeaderValue = "Bearer eyFoo";
    doReturn(authHeaderValue).when(request).getHeader(AUTHORIZATION);
    doReturn(true).when(jwtsSupport).validateJwtToken(any());

    // when
    underTest.doFilterInternal(request, new MockHttpServletResponse(), new MockFilterChain());

    // then
    verify(jwtsSupport).getClaims("eyFoo");
  }

  @Test
  @DisplayName("should find user by subject from token")
  void should_find_user_by_subject_from_token() throws ServletException, IOException {
    // given
    var authHeaderValue = "Bearer eyFoo";
    doReturn(authHeaderValue).when(request).getHeader(AUTHORIZATION);
    doReturn(true).when(jwtsSupport).validateJwtToken(any());

    var subject = "test-subject";
    var claims = mock(Claims.class);
    doReturn(subject).when(claims).getSubject();
    doReturn(claims).when(jwtsSupport).getClaims(any());

    // when
    underTest.doFilterInternal(request, new MockHttpServletResponse(), new MockFilterChain());

    // then
    verify(userRepository).findByPublicId(subject);
  }

  @Test
  @DisplayName("should set authentication")
  void should_set_authentication() throws ServletException, IOException {
    // given
    var authHeaderValue = "Bearer eyFoo";
    doReturn(authHeaderValue).when(request).getHeader(AUTHORIZATION);
    doReturn(true).when(jwtsSupport).validateJwtToken(any());
    doReturn(mock(Claims.class)).when(jwtsSupport).getClaims(any());
    doReturn(Optional.of(mock(UserEntity.class))).when(userRepository).findByPublicId(any());

    // when
    underTest.doFilterInternal(request, new MockHttpServletResponse(), new MockFilterChain());

    // then
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    assertThat(authentication.getClass()).isEqualTo(UsernamePasswordAuthenticationToken.class);
  }

  @Test
  @DisplayName("should set found user as principal")
  void should_set_found_user_as_principal() throws ServletException, IOException {
    // given
    var user = UserEntityFactory.createUser("user", "user@example.com");
    var authHeaderValue = "Bearer eyFoo";
    doReturn(authHeaderValue).when(request).getHeader(AUTHORIZATION);
    doReturn(true).when(jwtsSupport).validateJwtToken(any());
    doReturn(mock(Claims.class)).when(jwtsSupport).getClaims(any());
    doReturn(Optional.of(user)).when(userRepository).findByPublicId(any());

    // when
    underTest.doFilterInternal(request, new MockHttpServletResponse(), new MockFilterChain());

    // then
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    assertThat(authentication.getPrincipal()).isEqualTo(user);
  }
}
