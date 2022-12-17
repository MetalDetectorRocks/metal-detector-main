package rocks.metaldetector.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import rocks.metaldetector.testutil.BaseSpringBootTest;

import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestPropertySource(locations = {"classpath:integrationtest.properties"}, properties = {"management.health.mail.enabled=false"})
@AutoConfigureMockMvc
@SpringBootTest
public class ActuatorEndpointSecurityIntegrationTest extends BaseSpringBootTest {

  @Autowired
  private MockMvc mockMvc;

  @ParameterizedTest(name = "actuator endpoint {0} is not secured")
  @MethodSource("actuatorEndpointProvider")
  @DisplayName("actuator endpoints can be called from anonymous user")
  @WithAnonymousUser
  void anonymous_user_is_allowed_to_call_actuator_endpoints(String actuatorEndpoint) throws Exception {
    mockMvc.perform(get(actuatorEndpoint))
        .andDo(print())
        .andExpect(status().isOk());
  }

  private static Stream<Arguments> actuatorEndpointProvider() {
    return Stream.of(
        Arguments.of("/actuator"),
        Arguments.of("/actuator/info"),
        Arguments.of("/actuator/health"),
        Arguments.of("/actuator/metrics")
    );
  }
}
