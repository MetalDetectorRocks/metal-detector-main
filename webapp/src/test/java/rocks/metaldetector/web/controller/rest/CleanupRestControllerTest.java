package rocks.metaldetector.web.controller.rest;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.metaldetector.service.cleanup.CleanupService;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CleanupRestControllerTest implements WithAssertions {

  @Mock
  private CleanupService cleanupService;

  @InjectMocks
  private CleanupRestController underTest;

  @AfterEach
  void tearDown() {
    reset(cleanupService);
  }

  @Test
  @DisplayName("cleanupService is called")
  void test_cleanup_service_called() {
    // when
    underTest.cleanup();

    // then
    verify(cleanupService).cleanupUsersWithExpiredToken();
  }
}
