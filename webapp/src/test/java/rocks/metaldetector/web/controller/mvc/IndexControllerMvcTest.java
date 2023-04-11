package rocks.metaldetector.web.controller.mvc;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import rocks.metaldetector.security.AuthenticationFacade;
import rocks.metaldetector.security.SecurityConfig;
import rocks.metaldetector.testutil.BaseWebMvcTestWithSecurity;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static rocks.metaldetector.support.Endpoints.Frontend.HOME;

@WebMvcTest(IndexController.class)
@Import({SecurityConfig.class})
public class IndexControllerMvcTest extends BaseWebMvcTestWithSecurity {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    @SuppressWarnings("unused")
    private AuthenticationFacade authenticationFacade;

    @Test
    void status_should_be_ok() throws Exception {
        mockMvc.perform(get(HOME)).andExpect(status().isOk());
    }
}
