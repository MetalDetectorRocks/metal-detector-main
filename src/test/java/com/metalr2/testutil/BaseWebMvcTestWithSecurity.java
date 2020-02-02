package com.metalr2.testutil;

import com.metalr2.security.RedirectionHandlerInterceptor;
import com.metalr2.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import javax.sql.DataSource;

@WebMvcTest
public abstract class BaseWebMvcTestWithSecurity implements WithSecurityConfig {

  @Autowired
  protected MockMvc mockMvc;

  @SpyBean
  protected BCryptPasswordEncoder passwordEncoder;

  @MockBean
  protected DataSource dataSource;

  @MockBean
  protected RedirectionHandlerInterceptor redirectionInterceptor;

  @MockBean
  protected UserService userService;

}
