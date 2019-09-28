package com.metalr2.web.controller.authentication;

import com.metalr2.security.WebSecurity;
import org.junit.jupiter.api.Tag;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;

@WebMvcTest(ResetPasswordController.class)
@Import(WebSecurity.class)
@Tag("integration-test")
class ResetPasswordControllerIT {
}
