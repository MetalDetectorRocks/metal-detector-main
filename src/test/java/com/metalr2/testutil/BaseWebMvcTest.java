package com.metalr2.testutil;

import com.metalr2.security.RedirectionHandlerInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest
public abstract class BaseWebMvcTest implements WithIntegrationTestProfile {

  @Autowired
  protected MockMvc mockMvc;

  @MockBean
  protected RedirectionHandlerInterceptor redirectionInterceptor;

}
