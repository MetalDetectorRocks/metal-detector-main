package com.metalr2.web.controller.mvc.admin;

import com.metalr2.config.constants.ViewNames;
import com.metalr2.testutil.BaseWebMvcTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcSecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import static com.metalr2.config.constants.Endpoints.AdminArea;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(value = AdminIndexController.class, excludeAutoConfiguration = MockMvcSecurityAutoConfiguration.class)
class AdminIndexControllerIT extends BaseWebMvcTest {

    @Test
    @DisplayName("Should return admin index page when requesting uri '" + AdminArea.INDEX + "'")
    void should_return_admin_index_page() throws Exception {
        mockMvc.perform(get(AdminArea.INDEX))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name(ViewNames.AdminArea.INDEX));
    }
}