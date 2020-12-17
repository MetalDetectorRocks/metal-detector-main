package rocks.metaldetector.testutil;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import rocks.metaldetector.security.RedirectionHandlerInterceptor;
import rocks.metaldetector.service.user.UserService;
import rocks.metaldetector.support.SecurityProperties;

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

  @SpyBean
  protected ObjectMapper objectMapper;

  @MockBean
  protected ModelMapper mapper;

  @MockBean
  protected SecurityProperties securityProperties;

  @MockBean
  protected HttpSecurity httpSecurity;

}
