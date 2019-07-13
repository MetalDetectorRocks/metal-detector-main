package com.metalr2.web.controller.authentication;

import com.metalr2.security.WebSecurity;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@WebMvcTest(ResetPasswordController.class)
@Import(WebSecurity.class)
public class ResetPasswordControllerTest {

  // ToDo DanielW: Implement this

}
