package rocks.metaldetector.security.rememberme;

import org.springframework.core.log.LogMessage;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.rememberme.CookieTheftException;
import org.springframework.security.web.authentication.rememberme.InvalidCookieException;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationException;
import rocks.metaldetector.persistence.domain.user.UserEntity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Date;

public class UserIdBasedPersistentTokenRememberMeServices extends PersistentTokenBasedRememberMeServices {

  private final UserIdBasedJdbcTokenRepository tokenRepository;

  public UserIdBasedPersistentTokenRememberMeServices(String key, UserDetailsService userDetailsService, UserIdBasedJdbcTokenRepository tokenRepository) {
    super(key, userDetailsService, tokenRepository);
    this.tokenRepository = tokenRepository;
  }

  @Override
  protected void onLoginSuccess(HttpServletRequest request, HttpServletResponse response, Authentication successfulAuthentication) {
    UserEntity user = ((UserEntity) successfulAuthentication.getPrincipal());
    this.logger.debug(LogMessage.format("Creating new persistent login for user %s", user.getUsername()));
    PersistentRememberMeToken persistentToken = new UserIdBasedPersistentRememberMeToken(user.getId(), user.getUsername(), generateSeriesData(),
                                                                                         generateTokenData(), new Date());

    try {
      this.tokenRepository.createNewToken(persistentToken);
      addCookie(persistentToken, request, response);
    }
    catch (Exception ex) {
      this.logger.error("Failed to save persistent token ", ex);
    }
  }

  @Override
  protected UserDetails processAutoLoginCookie(String[] cookieTokens, HttpServletRequest request, HttpServletResponse response) {
    if (cookieTokens.length != 2) {
      throw new InvalidCookieException("Cookie token did not contain " + 2 + " tokens, but contained '"
                                       + Arrays.asList(cookieTokens) + "'");
    }
    String presentedSeries = cookieTokens[0];
    String presentedToken = cookieTokens[1];
    UserIdBasedPersistentRememberMeToken token = (UserIdBasedPersistentRememberMeToken) this.tokenRepository.getTokenForSeries(presentedSeries);
    if (token == null) {
      // No series match, so we can't authenticate using this cookie
      throw new RememberMeAuthenticationException("No persistent token found for series id: " + presentedSeries);
    }
    // We have a match for this user/series combination
    if (!presentedToken.equals(token.getTokenValue())) {
      // Token doesn't match series value. Delete all logins for this user and throw
      // an exception to warn them.
      this.tokenRepository.removeUserTokensByUserId(token.getUserId());
      throw new CookieTheftException(this.messages.getMessage(
          "PersistentTokenBasedRememberMeServices.cookieStolen",
          "Invalid remember-me token (Series/token) mismatch. Implies previous cookie theft attack."));
    }
    if (token.getDate().getTime() + getTokenValiditySeconds() * 1000L < System.currentTimeMillis()) {
      throw new RememberMeAuthenticationException("Remember-me login has expired");
    }
    // Token also matches, so login is valid. Update the token value, keeping the
    // *same* series number.
    this.logger.debug(LogMessage.format("Refreshing persistent login token for user '%s', series '%s'",
                                        token.getUsername(), token.getSeries()));
    PersistentRememberMeToken newToken = new UserIdBasedPersistentRememberMeToken(token.getUserId(), token.getUsername(), token.getSeries(),
                                                                                  generateTokenData(), new Date());
    try {
      this.tokenRepository.updateToken(newToken.getSeries(), newToken.getTokenValue(), newToken.getDate());
      addCookie(newToken, request, response);
    }
    catch (Exception ex) {
      this.logger.error("Failed to update token: ", ex);
      throw new RememberMeAuthenticationException("Autologin failed due to data access problem");
    }
    return getUserDetailsService().loadUserByUsername(token.getUsername());
  }

  @Override
  public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
    if (authentication != null) {
      this.tokenRepository.removeUserTokensByUserId(((UserEntity) authentication.getPrincipal()).getId());
    }
    cancelCookie(request, response);
  }

  private void addCookie(PersistentRememberMeToken token, HttpServletRequest request, HttpServletResponse response) {
    setCookie(new String[] {token.getSeries(), token.getTokenValue()}, getTokenValiditySeconds(), request,
              response);
  }
}
