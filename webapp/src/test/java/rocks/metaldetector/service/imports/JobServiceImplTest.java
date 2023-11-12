package rocks.metaldetector.service.imports;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.metaldetector.butler.facade.ButlerJobService;
import rocks.metaldetector.butler.facade.dto.ImportJobResultDto;
import rocks.metaldetector.config.misc.DelayedEventPublisher;

import java.util.List;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class JobServiceImplTest implements WithAssertions {

  @Mock
  private ButlerJobService butlerJobService;

  @Mock
  private DelayedEventPublisher delayedEventPublisher;

  @InjectMocks
  private JobServiceImpl underTest;

  @AfterEach
  void tearDown() {
    reset(butlerJobService, delayedEventPublisher);
  }

  @Test
  @DisplayName("createImportJobs() calls butlerJobService")
  void test_create_import_jobs_calls_butler_job_service() {
    // when
    underTest.createImportJobs();

    // then
    verify(butlerJobService).createImportJobs();
  }

  @Test
  @DisplayName("createImportJobs() calls eventPublisher for every jobId")
  void test_create_import_jobs_calls_event_publisher() {
    // given
    var jobIds = List.of("1", "2", "3");
    doReturn(jobIds).when(butlerJobService).createImportJobs();

    // when
    underTest.createImportJobs();

    // then
    verify(delayedEventPublisher).publishDelayedJobEvent("1");
    verify(delayedEventPublisher).publishDelayedJobEvent("2");
    verify(delayedEventPublisher).publishDelayedJobEvent("3");
  }

  @Test
  @DisplayName("createRetryCoverDownloadJob() calls butlerJobService")
  void test_create_cover_download_job_calls_butler_job_service() {
    // when
    underTest.createRetryCoverDownloadJob();

    // then
    verify(butlerJobService).createRetryCoverDownloadJob();
  }

  @Test
  @DisplayName("queryImportJobs() calls butlerJobService")
  void test_query_import_jobs_calls_butler_job_service() {
    // when
    underTest.queryImportJobs();

    // then
    verify(butlerJobService).queryImportJobs();
  }

  @Test
  @DisplayName("queryImportJobs() returns import jobs")
  void test_query_import_jobs_returns_import_jobs() {
    // given
    var importJobs = List.of(ImportJobResultDto.builder().source("Metal Archives").build());
    doReturn(importJobs).when(butlerJobService).queryImportJobs();

    // when
    var result = underTest.queryImportJobs();

    // then
    assertThat(result).isEqualTo(importJobs);
  }
}
