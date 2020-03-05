package rocks.metaldetector.testutil;

import org.junit.jupiter.api.Tag;
import org.springframework.context.annotation.Import;
import rocks.metaldetector.security.MethodSecurityConfig;
import rocks.metaldetector.security.SecurityConfig;

@Tag("integration-test")
@Import({SecurityConfig.class, MethodSecurityConfig.class})
public interface WithSecurityConfig {
}
