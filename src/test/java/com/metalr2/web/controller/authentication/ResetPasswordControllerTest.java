package com.metalr2.web.controller.authentication;

import com.metalr2.security.WebSecurity;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ResetPasswordController.class)
@Import(WebSecurity.class)
class ResetPasswordControllerTest {

  // ToDo DanielW: Implement this

}
