package rocks.metaldetector.testutil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import rocks.metaldetector.security.RedirectionHandlerInterceptor;

@WebMvcTest
public abstract class BaseWebMvcTest implements WithIntegrationTestConfig {

  @Autowired
  protected MockMvc mockMvc;

  @MockBean
  protected RedirectionHandlerInterceptor redirectionInterceptor;

}
