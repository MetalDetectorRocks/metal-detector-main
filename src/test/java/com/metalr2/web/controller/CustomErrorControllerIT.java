package com.metalr2.web.controller;

import com.metalr2.config.constants.Endpoints;
import com.metalr2.config.constants.ViewNames;
import com.metalr2.web.controller.mvc.CustomErrorController;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import javax.servlet.RequestDispatcher;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomErrorController.class)
@ActiveProfiles("test")
@Tag("integration-test")
class CustomErrorControllerIT implements WithAssertions {

  @Autowired
  private MockMvc mockMvc;

  @Test
  @DisplayName("Return 404 page if no controller for the requested URI was found")
  void test_error_404() throws Exception {
    final String REQUEST_URI = "/not-existing";

    mockMvc.perform(get(Endpoints.ERROR)
              .requestAttr(RequestDispatcher.ERROR_STATUS_CODE, HttpStatus.NOT_FOUND.value())
              .requestAttr(RequestDispatcher.ERROR_REQUEST_URI, REQUEST_URI))
            .andExpect(model().attribute("requestedURI", REQUEST_URI))
            .andExpect(status().isNotFound())
            .andExpect(view().name(ViewNames.Guest.ERROR_404));
  }

  @Test
  @DisplayName("Return 403 page if the access for the requested URI is denied")
  void test_error_403() throws Exception {
    final String REQUEST_URI = "/not-allowed";

    mockMvc.perform(get(Endpoints.ERROR)
            .requestAttr(RequestDispatcher.ERROR_STATUS_CODE, HttpStatus.FORBIDDEN.value())
            .requestAttr(RequestDispatcher.ERROR_REQUEST_URI, REQUEST_URI))
            .andExpect(model().attribute("requestedURI", REQUEST_URI))
            .andExpect(status().isForbidden())
            .andExpect(view().name(ViewNames.Guest.ERROR_403));
  }

  @Test
  @DisplayName("Return 500 page in case of unhandled server error")
  void test_error_500() throws Exception {
    final String REQUEST_URI = "/server-error";

    mockMvc.perform(get(Endpoints.ERROR)
            .requestAttr(RequestDispatcher.ERROR_STATUS_CODE, HttpStatus.INTERNAL_SERVER_ERROR.value())
            .requestAttr(RequestDispatcher.ERROR_REQUEST_URI, REQUEST_URI))
            .andExpect(model().attribute("requestedURI", REQUEST_URI))
            .andExpect(status().isInternalServerError())
            .andExpect(view().name(ViewNames.Guest.ERROR_500));
  }

  @Test
  @DisplayName("Return general error page for all other errors")
  void test_all_other_errors() throws Exception {
    final String REQUEST_URI = "/all-other-errors";

    mockMvc.perform(get(Endpoints.ERROR)
            .requestAttr(RequestDispatcher.ERROR_STATUS_CODE, HttpStatus.BAD_GATEWAY.value())
            .requestAttr(RequestDispatcher.ERROR_REQUEST_URI, REQUEST_URI))
            .andExpect(model().attribute("requestedURI", REQUEST_URI))
            .andExpect(status().isBadGateway())
            .andExpect(view().name(ViewNames.Guest.ERROR));
  }

}
