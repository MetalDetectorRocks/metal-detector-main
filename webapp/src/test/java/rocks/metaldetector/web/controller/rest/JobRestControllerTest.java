package rocks.metaldetector.web.controller.rest;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import io.restassured.module.mockmvc.response.ValidatableMockMvcResponse;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder;
import rocks.metaldetector.butler.facade.JobService;
import rocks.metaldetector.butler.facade.dto.ImportJobResultDto;
import rocks.metaldetector.support.Endpoints;
import rocks.metaldetector.testutil.DtoFactory;
import rocks.metaldetector.web.RestAssuredMockMvcUtils;

import java.util.List;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@ExtendWith(MockitoExtension.class)
class JobRestControllerTest implements WithAssertions {

  @Mock
  private JobService jobService;

  @BeforeEach
  void setUp() {
    JobRestController underTest = new JobRestController(jobService);
    StandaloneMockMvcBuilder mockMvcBuilder = MockMvcBuilders.standaloneSetup(underTest);
    RestAssuredMockMvc.standaloneSetup(mockMvcBuilder);
  }

  @AfterEach
  void tearDown() {
    reset(jobService);
  }

  @Nested
  @DisplayName("Tests creating an import job")
  class CreateImportTest {

    private RestAssuredMockMvcUtils restAssuredUtils;

    @BeforeEach
    void setUp() {
      restAssuredUtils = new RestAssuredMockMvcUtils(Endpoints.Rest.IMPORT_JOB);
    }

    @Test
    @DisplayName("Should call release service")
    void should_call_release_service() {
      // when
      restAssuredUtils.doPost();

      // then
      verify(jobService).createImportJob();
    }

    @Test
    @DisplayName("Should return CREATED")
    void should_return_status_created() {
      // when
      ValidatableMockMvcResponse validatableResponse = restAssuredUtils.doPost();

      // then
      validatableResponse.statusCode(CREATED.value());
    }
  }

  @Nested
  @DisplayName("Tests creating a job for retrying cover downloads")
  class CreateRetryDownloadTest {

    private RestAssuredMockMvcUtils restAssuredUtils;

    @BeforeEach
    void setUp() {
      restAssuredUtils = new RestAssuredMockMvcUtils(Endpoints.Rest.COVER_JOB);
    }

    @Test
    @DisplayName("Should call release service")
    void should_call_release_service() {
      // when
      restAssuredUtils.doPost();

      // then
      verify(jobService).createRetryCoverDownloadJob();
    }

    @Test
    @DisplayName("Should return OK")
    void should_return_status_ok() {
      // when
      ValidatableMockMvcResponse validatableResponse = restAssuredUtils.doPost();

      // then
      validatableResponse.statusCode(OK.value());
    }
  }

  @Nested
  @DisplayName("Tests querying import job results")
  class QueryImportJobResultsTest {

    private RestAssuredMockMvcUtils restAssuredUtils;

    @BeforeEach
    void setUp() {
      restAssuredUtils = new RestAssuredMockMvcUtils(Endpoints.Rest.IMPORT_JOB);
    }

    @Test
    @DisplayName("Should call release service")
    void should_call_release_service() {
      // when
      restAssuredUtils.doGet();

      // then
      verify(jobService).queryImportJobResults();
    }

    @Test
    @DisplayName("Should return result from release service with status OK")
    void should_return_status_created() {
      // given
      var importJobResultDto = List.of(
          DtoFactory.ImportJobResultDtoFactory.createDefault(),
          DtoFactory.ImportJobResultDtoFactory.createDefault()
      );
      doReturn(importJobResultDto).when(jobService).queryImportJobResults();

      // when
      ValidatableMockMvcResponse validatableResponse = restAssuredUtils.doGet();

      // then
      validatableResponse.statusCode(OK.value());
      var result = validatableResponse.extract().body().jsonPath().getList(".", ImportJobResultDto.class);
      assertThat(result).isEqualTo(importJobResultDto);
    }
  }
}
