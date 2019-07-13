package com.metalr2.web.controller.authentication;

import com.metalr2.config.constants.Endpoints;
import com.metalr2.config.constants.ViewNames;
import com.metalr2.service.token.TokenService;
import com.metalr2.service.user.UserService;
import com.metalr2.web.dto.UserDto;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class RegisterNewUserAccountIntegrationTest {

  private static final String RESPONSE_ATTRIBUTE_NAME = "registerUserRequest";
  private static final String PARAM_FIRST_NAME        = "firstName";
  private static final String PARAM_LAST_NAME         = "lastName";
  private static final String PARAM_EMAIL             = "email";
  private static final String PARAM_PASSWORD          = "password";
  private static final String PARAM_VERIFY_PASSWORD   = "verifyPassword";

  private Map<String, String> paramValues = new HashMap<>();
  private MockMvc mockMvc;

  @Mock private UserService userService;
  @Mock private TokenService tokenService;
  @Mock private ApplicationEventPublisher eventPublisher;
  @Mock private MessageSource messages;

  @Before
  public void setup(){
    userService = mock(UserService.class);
    when(userService.createUser(new UserDto())).thenReturn(new UserDto());

    tokenService   = mock(TokenService.class);
    eventPublisher = mock(ApplicationEventPublisher.class);
    messages       = mock(MessageSource.class);

    InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
    viewResolver.setPrefix("/resources/templates/");
    viewResolver.setSuffix(".html");
    mockMvc = MockMvcBuilders.standaloneSetup(new RegistrationController(userService, tokenService, eventPublisher, messages))
                             .setViewResolvers(viewResolver)
                             .build();

    paramValues.put(PARAM_FIRST_NAME, "John");
    paramValues.put(PARAM_LAST_NAME, "Doe");
    paramValues.put(PARAM_EMAIL, "john.doe@example.com");
    paramValues.put(PARAM_PASSWORD, "secret-password");
    paramValues.put(PARAM_VERIFY_PASSWORD, "secret-password");
  }

  @Test
  public void register_new_user_account_should_be_ok() throws Exception {
    mockMvc.perform(createRequestBuilder())
           .andExpect(model().errorCount(0))
           .andExpect(status().isOk())
           .andExpect(view().name(ViewNames.REGISTER));
  }

  @Test
  public void register_new_user_account_should_fail_due_to_empty_names() throws Exception {
    paramValues.put(PARAM_FIRST_NAME, "");
    paramValues.put(PARAM_LAST_NAME, null);

    mockMvc.perform(createRequestBuilder())
            .andExpect(model().errorCount(2))
            .andExpect(model().attributeHasFieldErrors(RESPONSE_ATTRIBUTE_NAME, PARAM_FIRST_NAME, PARAM_LAST_NAME))
            .andExpect(status().isOk())
            .andExpect(view().name(ViewNames.REGISTER));
  }

  @Test
  public void register_new_user_account_should_fail_due_to_invalid_email() throws Exception {
    paramValues.put(PARAM_EMAIL, "john.doe.example.de");
    mockMvc.perform(createRequestBuilder())
            .andExpect(model().errorCount(1))
            .andExpect(model().attributeHasFieldErrors(RESPONSE_ATTRIBUTE_NAME, PARAM_EMAIL))
            .andExpect(status().isOk())
            .andExpect(view().name(ViewNames.REGISTER));
  }

  @Test
  public void register_new_user_account_should_fail_due_to_empty_email() throws Exception {
    paramValues.put(PARAM_EMAIL, "");
    mockMvc.perform(createRequestBuilder())
            .andExpect(model().errorCount(1))
            .andExpect(model().attributeHasFieldErrors(RESPONSE_ATTRIBUTE_NAME, PARAM_EMAIL))
            .andExpect(status().isOk())
            .andExpect(view().name(ViewNames.REGISTER));
  }

  @Test
  public void register_new_user_account_should_fail_due_to_not_matching_passwords() throws Exception {
    paramValues.put(PARAM_PASSWORD, "secret-password");
    paramValues.put(PARAM_VERIFY_PASSWORD, "other-secret-password");
    mockMvc.perform(createRequestBuilder())
            .andExpect(model().errorCount(1))
            .andExpect(status().isOk())
            .andExpect(view().name(ViewNames.REGISTER));
  }

  @Test
  public void register_new_user_account_should_fail_due_to_too_short_passwords() throws Exception {
    paramValues.put(PARAM_PASSWORD, "secret");
    paramValues.put(PARAM_VERIFY_PASSWORD, "secret");
    mockMvc.perform(createRequestBuilder())
            .andExpect(model().errorCount(2))
            .andExpect(model().attributeHasFieldErrors(RESPONSE_ATTRIBUTE_NAME, PARAM_PASSWORD, PARAM_PASSWORD))
            .andExpect(status().isOk())
            .andExpect(view().name(ViewNames.REGISTER));
  }

  @Test
  public void register_new_user_account_should_fail_due_to_empty_and_not_matching_passwords() throws Exception {
    paramValues.put(PARAM_PASSWORD, "");
    paramValues.put(PARAM_VERIFY_PASSWORD, "");
    mockMvc.perform(createRequestBuilder())
            .andExpect(model().errorCount(4))
            .andExpect(model().attributeHasFieldErrors(RESPONSE_ATTRIBUTE_NAME, PARAM_PASSWORD, PARAM_PASSWORD))
            .andExpect(status().isOk())
            .andExpect(view().name(ViewNames.REGISTER));
  }

  private MockHttpServletRequestBuilder createRequestBuilder() {
    return MockMvcRequestBuilders
            .post(Endpoints.REGISTER)
            .accept(MediaType.TEXT_HTML)
            .param(PARAM_FIRST_NAME, paramValues.get(PARAM_FIRST_NAME))
            .param(PARAM_LAST_NAME, paramValues.get(PARAM_LAST_NAME))
            .param(PARAM_EMAIL, paramValues.get(PARAM_EMAIL))
            .param(PARAM_PASSWORD, paramValues.get(PARAM_PASSWORD))
            .param(PARAM_VERIFY_PASSWORD, paramValues.get(PARAM_VERIFY_PASSWORD));
  }
}
