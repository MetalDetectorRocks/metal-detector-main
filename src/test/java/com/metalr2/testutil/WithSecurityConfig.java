package com.metalr2.testutil;

import com.metalr2.security.SecurityConfig;
import org.junit.jupiter.api.Tag;
import org.springframework.context.annotation.Import;

@Tag("integration-test")
@Import(SecurityConfig.class)
public interface WithSecurityConfig {
}
