package rocks.metaldetector.security.filter;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
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
import rocks.metaldetector.service.oauthAuthorizationState.OAuthAuthorizationStateService;
import rocks.metaldetector.service.user.UserEntityFactory;
import rocks.metaldetector.support.exceptions.ResourceNotFoundException;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static rocks.metaldetector.support.Endpoints.Rest.OAUTH_CALLBACK;

@ExtendWith(MockitoExtension.class)
class OAuth2AuthorizationCodeLoginFilterTest implements WithAssertions {

  @InjectMocks
  private OAuth2AuthorizationCodeLoginFilter underTest;

  @Mock
  private OAuthAuthorizationStateService authorizationStateService;

  @Mock
  private AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource;

  @AfterEach
  void tearDown() {
    reset(authorizationStateService, authenticationDetailsSource);
  }

  @Test
  @DisplayName("Should filter if request URI does equal OAUTH_CALLBACK")
  void test_should_filter() {
    // given
    var request = new MockHttpServletRequest();
    request.setRequestURI(OAUTH_CALLBACK);

    // when
    boolean result = underTest.shouldNotFilter(request);

    // then
    assertThat(result).isFalse();
  }

  @Test
  @DisplayName("Should not filter if request URI does not equal OAUTH_CALLBACK")
  void test_should_not_filter() {
    // given
    var request = new MockHttpServletRequest();
    request.setRequestURI("/some/other/endpoint");

    // when
    boolean result = underTest.shouldNotFilter(request);

    // then
    assertThat(result).isTrue();
  }

  @Test
  @DisplayName("State service is not called if state is blank")
  void test_state_service_not_called_if_state_is_null() throws ServletException, IOException {
    // given
    var request = new MockHttpServletRequest();
    var response = new MockHttpServletResponse();
    var filterChain = new MockFilterChain();
    request.setParameter("state", " ");

    // when
    underTest.doFilterInternal(request, response, filterChain);

    // then
    verifyNoInteractions(authorizationStateService);
    assertThat(filterChain.getRequest()).isEqualTo(request);
    assertThat(filterChain.getResponse()).isEqualTo(response);
  }

  @Test
  @DisplayName("State service is called if state is set")
  void test_state_service_called_if_state_is_set() throws ServletException, IOException {
    // given
    var request = new MockHttpServletRequest();
    var state = "some-state";
    request.setParameter("state", state);

    // when
    underTest.doFilterInternal(request, new MockHttpServletResponse(), new MockFilterChain());

    // then
    verify(authorizationStateService).findUserByState(state);
  }

  @Test
  @DisplayName("authentication is set in security context if state is set")
  void test_authentication_is_set_in_security_context() throws ServletException, IOException {
    // given
    var request = new MockHttpServletRequest();
    var state = "some-state";
    request.setParameter("state", state);
    var user = UserEntityFactory.createDefaultUser();
    doReturn(user).when(authorizationStateService).findUserByState(anyString());

    // when
    underTest.doFilterInternal(request, new MockHttpServletResponse(), new MockFilterChain());

    // then
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    assertThat(authentication.getClass()).isEqualTo(UsernamePasswordAuthenticationToken.class);
  }

  @Test
  @DisplayName("user is set as principle if state is set")
  void test_user_is_set_as_principle() throws ServletException, IOException {
    // given
    var request = new MockHttpServletRequest();
    request.setParameter("state", "some-state");
    var user = UserEntityFactory.createDefaultUser();
    doReturn(user).when(authorizationStateService).findUserByState(anyString());

    // when
    underTest.doFilterInternal(request, new MockHttpServletResponse(), new MockFilterChain());

    // then
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    assertThat(authentication.getPrincipal()).isEqualTo(user);
  }

  @Test
  @DisplayName("state entity is deleted after setting context")
  void test_state_entity_is_deleted_after_setting_context() throws ServletException, IOException {
    // given
    var request = new MockHttpServletRequest();
    var state = "some-state";
    request.setParameter("state", state);
    var user = UserEntityFactory.createDefaultUser();
    doReturn(user).when(authorizationStateService).findUserByState(anyString());

    // when
    underTest.doFilterInternal(request, new MockHttpServletResponse(), new MockFilterChain());

    // then
    verify(authorizationStateService).deleteByState(state);
  }

  @Test
  @DisplayName("Should send error with 403 in case of exceptions")
  void test_in_case_of_exceptions_an_error_is_sent() throws ServletException, IOException {
    // given
    var request = new MockHttpServletRequest();
    var response = new MockHttpServletResponse();
    request.setParameter("state", "some-state");
    doThrow(ResourceNotFoundException.class).when(authorizationStateService).findUserByState(anyString());

    // when
    underTest.doFilterInternal(request, response, new MockFilterChain());

    // then
    assertThat(response.getStatus()).isEqualTo(FORBIDDEN.value());
  }

  @Test
  @DisplayName("FilterChain is called")
  void test_filter_chain_is_called() throws ServletException, IOException {
    // given
    var request = new MockHttpServletRequest();
    var response = new MockHttpServletResponse();
    var filterChain = new MockFilterChain();

    // when
    underTest.doFilterInternal(request, response, filterChain);

    // then
    assertThat(filterChain.getRequest()).isEqualTo(request);
    assertThat(filterChain.getResponse()).isEqualTo(response);
  }
}
