package com.metalr2.web.controller.mvc.admin;

import com.metalr2.config.constants.ViewNames;
import com.metalr2.testutil.WithIntegrationTestProfile;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcSecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static com.metalr2.config.constants.Endpoints.AdminArea;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = AdminIndexController.class, excludeAutoConfiguration = MockMvcSecurityAutoConfiguration.class)
class AdminIndexControllerIT implements WithIntegrationTestProfile {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Should return admin index page when requesting uri '" + AdminArea.INDEX + "'")
    void should_return_admin_index_page() throws Exception {
        mockMvc.perform(get(AdminArea.INDEX))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name(ViewNames.AdminArea.INDEX));
    }

}