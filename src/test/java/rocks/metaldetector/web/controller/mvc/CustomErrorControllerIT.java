package rocks.metaldetector.web.controller.mvc;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcSecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import rocks.metaldetector.config.constants.Endpoints;
import rocks.metaldetector.config.constants.ViewNames;
import rocks.metaldetector.testutil.BaseWebMvcTest;

import javax.servlet.RequestDispatcher;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(controllers = CustomErrorController.class, excludeAutoConfiguration = MockMvcSecurityAutoConfiguration.class)
class CustomErrorControllerIT extends BaseWebMvcTest implements WithAssertions {

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
