package rocks.metaldetector.web.controller.rest;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static rocks.metaldetector.web.controller.rest.CspViolationReportRestController.LOG_CSP_REPORT_PROPERTY;

@ExtendWith(MockitoExtension.class)
class CspViolationReportRestControllerTest implements WithAssertions {

  @Mock
  private Environment environment;

  @InjectMocks
  private CspViolationReportRestController underTest;

  @AfterEach
  private void tearDown() {
    reset(environment);
  }

  @ParameterizedTest
  @ValueSource(strings = {"true", "false"})
  @DisplayName("should do nothing")
  void test_handle_method(String shouldLog) {
    // given
    doReturn(shouldLog).when(environment).getProperty(anyString());

    // when
    underTest.handleCspViolationReport(Collections.emptyMap());
  }

  @Test
  @DisplayName("should call environment")
  void test_env_called() {
    // given
    doReturn("true").when(environment).getProperty(anyString());

    // when
    underTest.handleCspViolationReport(Collections.emptyMap());

    // then
    verify(environment).getProperty(LOG_CSP_REPORT_PROPERTY);
  }
}
