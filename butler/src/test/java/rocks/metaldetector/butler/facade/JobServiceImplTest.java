package rocks.metaldetector.butler.facade;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.metaldetector.butler.ButlerDtoFactory;
import rocks.metaldetector.butler.api.ButlerImportJob;
import rocks.metaldetector.butler.client.ReleaseButlerRestClient;
import rocks.metaldetector.butler.client.transformer.ButlerImportJobTransformer;
import rocks.metaldetector.butler.facade.dto.ImportJobResultDto;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class JobServiceImplTest implements WithAssertions {

  @Mock
  private ReleaseButlerRestClient butlerClient;

  @Mock
  private ButlerImportJobTransformer importJobResponseTransformer;

  private JobServiceImpl underTest;

  @BeforeEach
  void setup() {
    underTest = new JobServiceImpl(butlerClient, importJobResponseTransformer);
  }

  @AfterEach
  void tearDown() {
    reset(butlerClient, importJobResponseTransformer);
  }

  @Test
  @DisplayName("Creating an import job should call butler client")
  void create_import_job_should_call_butler_client() {
    // when
    underTest.createImportJob();

    // then
    verify(butlerClient).createImportJob();
  }

  @Test
  @DisplayName("Querying import job results should use butler client")
  void query_import_job_results_should_use_butler_client() {
    // given
    doReturn(Collections.emptyList()).when(butlerClient).queryImportJobResults();

    // when
    underTest.queryImportJobResults();

    // then
    verify(butlerClient).queryImportJobResults();
  }

  @Test
  @DisplayName("Querying import job results should use import job transformer to transform each response")
  void query_import_job_results_should_use_transformer() {
    // given
    var butlerImportJobs = List.of(
        ButlerDtoFactory.ButlerImportJobFactory.createDefault(),
        ButlerDtoFactory.ButlerImportJobFactory.createDefault()
    );
    doReturn(butlerImportJobs).when(butlerClient).queryImportJobResults();

    // when
    underTest.queryImportJobResults();

    // then
    verify(importJobResponseTransformer, times(butlerImportJobs.size())).transform(any(ButlerImportJob.class));
  }

  @Test
  @DisplayName("Querying import job results should return transformed responses")
  void query_import_job_results_should_return_transformed_responses() {
    // given
    var butlerImportJobs = List.of(
        ButlerDtoFactory.ButlerImportJobFactory.createDefault(),
        ButlerDtoFactory.ButlerImportJobFactory.createDefault()
    );
    doReturn(butlerImportJobs).when(butlerClient).queryImportJobResults();
    doReturn(ButlerDtoFactory.ImportJobResultDtoFactory.createDefault()).when(importJobResponseTransformer).transform(any());

    // when
    List<ImportJobResultDto> response = underTest.queryImportJobResults();

    // then
    assertThat(response).isEqualTo(List.of(
        ButlerDtoFactory.ImportJobResultDtoFactory.createDefault(),
        ButlerDtoFactory.ImportJobResultDtoFactory.createDefault()
    ));
  }

  @Test
  @DisplayName("Creating an cover download job should call butler client")
  void create_cover_download_job_should_call_butler_client() {
    // when
    underTest.createRetryCoverDownloadJob();

    // then
    verify(butlerClient).createRetryCoverDownloadJob();
  }
}
