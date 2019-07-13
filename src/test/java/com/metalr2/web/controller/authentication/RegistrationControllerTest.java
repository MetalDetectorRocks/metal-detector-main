package com.metalr2.web.controller.authentication;

import com.metalr2.config.constants.Endpoints;
import com.metalr2.config.constants.ViewNames;
import com.metalr2.model.exceptions.EmailVerificationTokenExpiredException;
import com.metalr2.model.exceptions.ResourceNotFoundException;
import com.metalr2.security.WebSecurity;
import com.metalr2.service.token.TokenService;
import com.metalr2.service.user.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@ExtendWith(SpringExtension.class)
@WebMvcTest(RegistrationController.class)
@Import(WebSecurity.class)
class RegistrationControllerTest {

  private static final String NOT_EXISTING_TOKEN = "not_existing_token";
  private static final String EXPIRED_TOKEN      = "expired_token";

  private MockMvc mockMvc;

  // Dependencies for WebSecurity.class
  @MockBean private UserService securityUserService;
  @MockBean private TokenService securitTokenService;
  @MockBean private BCryptPasswordEncoder passwordEncoder;

  // Dependencies for RegistrationController.class
  @Mock private UserService userService;
  @Mock private TokenService tokenService;
  @Mock private ApplicationEventPublisher eventPublisher;
  @Mock private MessageSource messages;

  @BeforeEach
  void setUp() {
    userService = mock(UserService.class);
    doThrow(ResourceNotFoundException.class).when(userService).verifyEmailToken(NOT_EXISTING_TOKEN);
    doThrow(EmailVerificationTokenExpiredException.class).when(userService).verifyEmailToken(EXPIRED_TOKEN);

    tokenService = mock(TokenService.class);
    doThrow(ResourceNotFoundException.class).when(tokenService).resendExpiredEmailVerificationToken(NOT_EXISTING_TOKEN);

    eventPublisher = mock(ApplicationEventPublisher.class);
    messages       = mock(MessageSource.class);

    InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
    viewResolver.setPrefix("/resources/templates/");
    viewResolver.setSuffix(".html");
    mockMvc = MockMvcBuilders.standaloneSetup(new RegistrationController(userService, tokenService, eventPublisher, messages))
            .setViewResolvers(viewResolver)
            .build();
  }

  @AfterEach
  void tearDown() {
    // do nothing
  }

  @Test
  void given_register_uri_should_return_register_view() throws Exception {
    mockMvc.perform(get(Endpoints.REGISTER))
            .andExpect(status().isOk())
            .andExpect(view().name(ViewNames.REGISTER));
  }

  @Test
  void given_valid_token_on_registration_verification_uri_should_redirect_to_login_view() throws Exception {
    mockMvc.perform(get(Endpoints.REGISTRATION_VERIFICATION + "?token=valid_token"))
            .andExpect(status().is3xxRedirection())
            .andExpect(view().name("redirect:" + Endpoints.LOGIN + "?verificationSuccess"));
  }

  @Test
  void given_not_existing_token_on_registration_verification_uri_should_redirect_to_login_view() throws Exception {
    mockMvc.perform(get(Endpoints.REGISTRATION_VERIFICATION + "?token=" + NOT_EXISTING_TOKEN))
            .andExpect(status().is3xxRedirection())
            .andExpect(view().name("redirect:" + Endpoints.LOGIN + "?tokenNotFound"));
  }

  @Test
  void given_expired_token_on_registration_verification_uri_should_redirect_to_login_view() throws Exception {
    mockMvc.perform(get(Endpoints.REGISTRATION_VERIFICATION + "?token=" + EXPIRED_TOKEN))
            .andExpect(status().is3xxRedirection())
            .andExpect(view().name("redirect:" + Endpoints.LOGIN + "?tokenExpired&token=" + EXPIRED_TOKEN));
  }

  @Test
  void given_valid_token_on_resend_verification_token_uri_should_redirect_to_login_view() throws Exception {
    mockMvc.perform(get(Endpoints.RESEND_VERIFICATION_TOKEN + "?token=valid-token"))
            .andExpect(status().is3xxRedirection())
            .andExpect(view().name("redirect:" + Endpoints.LOGIN + "?resendVerificationTokenSuccess"));
  }

  @Test
  void given__not_existing_token_on_resend_verification_token_uri_should_redirect_to_login_view() throws Exception {
    mockMvc.perform(get(Endpoints.RESEND_VERIFICATION_TOKEN + "?token=" + NOT_EXISTING_TOKEN))
            .andExpect(status().is3xxRedirection())
            .andExpect(view().name("redirect:" + Endpoints.LOGIN + "?tokenNotFound"));
  }

}
