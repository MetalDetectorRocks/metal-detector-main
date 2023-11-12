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
import rocks.metaldetector.butler.api.ButlerImportCreatedResponse;
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
class ButlerJobServiceImplTest implements WithAssertions {

  @Mock
  private ReleaseButlerRestClient butlerClient;

  @Mock
  private ButlerImportJobTransformer importJobResponseTransformer;

  private ButlerJobServiceImpl underTest;

  @BeforeEach
  void setup() {
    underTest = new ButlerJobServiceImpl(butlerClient, importJobResponseTransformer);
  }

  @AfterEach
  void tearDown() {
    reset(butlerClient, importJobResponseTransformer);
  }

  @Test
  @DisplayName("Creating an import job should call butler client")
  void create_import_job_should_call_butler_client() {
    // given
    doReturn(ButlerImportCreatedResponse.builder().build()).when(butlerClient).createImportJobs();

    // when
    underTest.createImportJobs();

    // then
    verify(butlerClient).createImportJobs();
  }

  @Test
  @DisplayName("Creating an import job should return ids of created jobs")
  void create_import_job_should_return_ids() {
    // given
    var jobIds = List.of("1", "2", "3");
    doReturn(ButlerImportCreatedResponse.builder().importJobIds(jobIds).build()).when(butlerClient).createImportJobs();

    // when
    var result = underTest.createImportJobs();

    // then
    assertThat(result).isEqualTo(jobIds);
  }

  @Test
  @DisplayName("Querying import job results should use butler client")
  void query_import_job_results_should_use_butler_client() {
    // given
    doReturn(Collections.emptyList()).when(butlerClient).queryImportJobs();

    // when
    underTest.queryImportJobs();

    // then
    verify(butlerClient).queryImportJobs();
  }

  @Test
  @DisplayName("Querying import job results should use import job transformer to transform each response")
  void query_import_job_results_should_use_transformer() {
    // given
    var butlerImportJobs = List.of(
        ButlerDtoFactory.ButlerImportJobFactory.createDefault(),
        ButlerDtoFactory.ButlerImportJobFactory.createDefault()
    );
    doReturn(butlerImportJobs).when(butlerClient).queryImportJobs();

    // when
    underTest.queryImportJobs();

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
    doReturn(butlerImportJobs).when(butlerClient).queryImportJobs();
    doReturn(ButlerDtoFactory.ImportJobResultDtoFactory.createDefault()).when(importJobResponseTransformer).transform(any());

    // when
    List<ImportJobResultDto> response = underTest.queryImportJobs();

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

  @Test
  @DisplayName("Querying a job should call butler client")
  void query_import_job_should_call_butler() {
    // given
    var jobId = "666";

    // when
    underTest.queryImportJob(jobId);

    // then
    verify(butlerClient).queryImportJob(jobId);
  }

  @Test
  @DisplayName("Querying a job should call job transformer")
  void query_import_job_should_call_job_transformer() {
    // given
    var job = ButlerImportJob.builder().totalCountImported(666).build();
    doReturn(job).when(butlerClient).queryImportJob(any());

    // when
    underTest.queryImportJob("666");

    // then
    verify(importJobResponseTransformer).transform(job);
  }

  @Test
  @DisplayName("Querying a job should return job dto")
  void query_import_job_should_return_job_dto() {
    // given
    var jobDto = ImportJobResultDto.builder().totalCountImported(666).build();
    doReturn(jobDto).when(importJobResponseTransformer).transform(any());

    // when
    var result = underTest.queryImportJob("666");

    // then
    assertThat(result).isEqualTo(jobDto);
  }
}
