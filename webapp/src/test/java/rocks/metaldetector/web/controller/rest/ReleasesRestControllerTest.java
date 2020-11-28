package rocks.metaldetector.web.controller.rest;

import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import io.restassured.module.mockmvc.response.ValidatableMockMvcResponse;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortHandlerMethodArgumentResolver;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder;
import rocks.metaldetector.butler.facade.ReleaseService;
import rocks.metaldetector.butler.facade.dto.ImportJobResultDto;
import rocks.metaldetector.butler.facade.dto.ReleaseDto;
import rocks.metaldetector.service.artist.FollowArtistService;
import rocks.metaldetector.service.exceptions.RestExceptionsHandler;
import rocks.metaldetector.support.Endpoints;
import rocks.metaldetector.support.Page;
import rocks.metaldetector.support.PageRequest;
import rocks.metaldetector.support.Pagination;
import rocks.metaldetector.support.Sorting;
import rocks.metaldetector.support.TimeRange;
import rocks.metaldetector.testutil.DtoFactory;
import rocks.metaldetector.testutil.DtoFactory.ImportJobResultDtoFactory;
import rocks.metaldetector.testutil.DtoFactory.ReleaseDtoFactory;
import rocks.metaldetector.testutil.DtoFactory.ReleaseRequestFactory;
import rocks.metaldetector.web.RestAssuredMockMvcUtils;
import rocks.metaldetector.web.api.request.PaginatedReleasesRequest;
import rocks.metaldetector.web.api.request.ReleaseUpdateRequest;
import rocks.metaldetector.web.api.request.ReleasesRequest;
import rocks.metaldetector.web.transformer.SortingTransformer;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static rocks.metaldetector.testutil.DtoFactory.PaginatedReleaseRequestFactory;

@ExtendWith(MockitoExtension.class)
class ReleasesRestControllerTest implements WithAssertions {

  @Mock
  private ReleaseService releasesService;

  @Mock
  private FollowArtistService followArtistService;

  @Mock
  private SortingTransformer sortingTransformer;

  @InjectMocks
  private ReleasesRestController underTest;

  @BeforeEach
  void setUp() {
    StandaloneMockMvcBuilder mockMvcBuilder = MockMvcBuilders.standaloneSetup(underTest, RestExceptionsHandler.class)
        .setCustomArgumentResolvers(new SortHandlerMethodArgumentResolver());
    RestAssuredMockMvc.standaloneSetup(mockMvcBuilder);
  }

  @AfterEach
  void tearDown() {
    reset(releasesService, followArtistService, sortingTransformer);
  }

  @Nested
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  @DisplayName("Tests for endpoint '" + Endpoints.Rest.ALL_RELEASES + "'")
  class QueryAllReleasesTest {

    private RestAssuredMockMvcUtils restAssuredUtils;

    @BeforeEach
    void setUp() {
      restAssuredUtils = new RestAssuredMockMvcUtils(Endpoints.Rest.ALL_RELEASES);
    }

    @Test
    @DisplayName("Should pass query request parameter to release service")
    void should_pass_query_parameter_to_release_service() {
      // given
      ReleasesRequest request = ReleaseRequestFactory.createDefault();

      // when
      restAssuredUtils.doGet(toMap(request));

      // then
      verify(releasesService).findAllReleases(Collections.emptyList(), new TimeRange(request.getDateFrom(), request.getDateTo()));
    }

    @Test
    @DisplayName("Should return releases from release service")
    void should_return_releases() {
      // given
      var request = ReleaseRequestFactory.createDefault();
      var releases = List.of(ReleaseDtoFactory.createDefault());
      doReturn(releases).when(releasesService).findAllReleases(any(), any());

      // when
      var validatableResponse = restAssuredUtils.doGet(toMap(request));

      // then
      validatableResponse
          .contentType(ContentType.JSON)
          .statusCode(OK.value());

      var result = validatableResponse.extract().as(ReleaseDto[].class);
      assertThat(Arrays.asList(result)).isEqualTo(releases);
    }

    @ParameterizedTest(name = "Should return 400 on invalid query request <{0}>")
    @MethodSource("requestProvider")
    @DisplayName("Should return 400 on invalid query request")
    void test_invalid_query_requests(ReleasesRequest request) {
      // when
      var validatableResponse = restAssuredUtils.doGet(toMap(request));

      // then
      validatableResponse
          .contentType(ContentType.JSON)
          .statusCode(BAD_REQUEST.value());
    }

    private Stream<Arguments> requestProvider() {
      var validFrom = LocalDate.now();
      var validTo = LocalDate.now().plusDays(10);

      return Stream.of(
          Arguments.of(new ReleasesRequest(validFrom.plusDays(20), validTo))
      );
    }

    private Map<String, Object> toMap(ReleasesRequest request) {
      Map<String, Object> map = new HashMap<>();
      map.put("dateFrom", request.getDateFrom().toString());
      map.put("dateTo", request.getDateTo().toString());

      return map;
    }
  }

  @Nested
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  @DisplayName("Tests for endpoint '" + Endpoints.Rest.RELEASES + "'")
  class QueryReleasesTest {

    private RestAssuredMockMvcUtils restAssuredUtils;

    @BeforeEach
    void setUp() {
      restAssuredUtils = new RestAssuredMockMvcUtils(Endpoints.Rest.RELEASES);
    }

    @Test
    @DisplayName("Should pass query request parameter to release service")
    void should_pass_query_parameter_to_release_service() {
      // given
      PaginatedReleasesRequest request = PaginatedReleaseRequestFactory.createDefault();

      // when
      restAssuredUtils.doGet(toMap(request));

      // then
      verify(releasesService).findReleases(
          Collections.emptyList(),
          new TimeRange(request.getDateFrom(), request.getDateTo()),
          new PageRequest(request.getPage(), request.getSize(), null)
      );
    }

    @Test
    @DisplayName("SortingTransformer is called with given Sort")
    void should_call_sorting_transformer() {
      // given
      Map<String, Object> requestMap = Map.of("sort", "artist,ASC");

      // when
      restAssuredUtils.doGet(requestMap);

      // then
      verify(sortingTransformer).transform(Sort.by(Sort.Direction.ASC, "artist"));
    }

    @Test
    @DisplayName("SortingTransformer is called with default Sort if none is given")
    void should_call_sorting_transformer_with_default() {
      // when
      restAssuredUtils.doGet();

      // then
      verify(sortingTransformer).transform(Sort.by(Sort.Direction.ASC, "releaseDate", "artist", "albumTitle"));
    }

    @Test
    @DisplayName("Sorting is passed to release service")
    void should_call_release_service_with_sorting() {
      // given
      var sorting = new Sorting(List.of(new Sorting.Order(Sorting.Direction.ASC, "artist")));
      var expectedPageRequest = new PageRequest(1, 40, sorting);
      doReturn(sorting).when(sortingTransformer).transform(any());

      // when
      restAssuredUtils.doGet();

      // then
      verify(releasesService).findReleases(any(), any(), eq(expectedPageRequest));
    }

    @Test
    @DisplayName("Should return the page from release service")
    void should_return_releases() {
      // given
      var request = PaginatedReleaseRequestFactory.createDefault();
      var releases = List.of(ReleaseDtoFactory.createDefault());
      var page = new Page<>(releases, new Pagination(1, 1, 5));
      doReturn(page).when(releasesService).findReleases(any(), any(), any());

      // when
      var validatableResponse = restAssuredUtils.doGet(toMap(request));

      // then
      validatableResponse
          .contentType(ContentType.JSON)
          .statusCode(OK.value());

      var jsonPath = validatableResponse.extract().jsonPath();
      var paginationResult = jsonPath.getObject("pagination", Pagination.class);
      var itemsResult = jsonPath.getObject("items", ReleaseDto[].class);
      assertThat(paginationResult).isEqualTo(page.getPagination());
      assertThat(Arrays.asList(itemsResult)).isEqualTo(page.getItems());
    }

    @ParameterizedTest(name = "Should return 400 on invalid query request <{0}>")
    @MethodSource("requestProvider")
    @DisplayName("Should return 400 on invalid query request")
    void test_invalid_query_requests(PaginatedReleasesRequest request) {
      // when
      var validatableResponse = restAssuredUtils.doGet(toMap(request));

      // then
      validatableResponse
          .contentType(ContentType.JSON)
          .statusCode(BAD_REQUEST.value());
    }

    private Stream<Arguments> requestProvider() {
      var validPage = 1;
      var validSize = 10;
      var validFrom = LocalDate.now();
      var validTo = LocalDate.now().plusDays(10);

      return Stream.of(
          Arguments.of(new PaginatedReleasesRequest(0, validSize, validFrom, validTo)),
          Arguments.of(new PaginatedReleasesRequest(validPage, 0, validFrom, validTo)),
          Arguments.of(new PaginatedReleasesRequest(validPage, 51, validFrom, validTo)),
          Arguments.of(new PaginatedReleasesRequest(validPage, validSize, validFrom.plusDays(20), validTo))
      );
    }

    private Map<String, Object> toMap(PaginatedReleasesRequest request) {
      Map<String, Object> map = new HashMap<>();
      map.put("page", request.getPage());
      map.put("size", request.getSize());
      map.put("dateFrom", request.getDateFrom().toString());
      map.put("dateTo", request.getDateTo().toString());

      return map;
    }
  }

  @Nested
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  @DisplayName("Tests for endpoint '" + Endpoints.Rest.MY_RELEASES + "'")
  class QueryMyReleasesTest {

    private RestAssuredMockMvcUtils restAssuredUtils;

    @BeforeEach
    void setUp() {
      restAssuredUtils = new RestAssuredMockMvcUtils(Endpoints.Rest.MY_RELEASES);
    }

    @Test
    @DisplayName("Should call follow artist service")
    void should_call_follow_artist_service() {
      // given
      PaginatedReleasesRequest request = PaginatedReleaseRequestFactory.createDefault();

      // when
      restAssuredUtils.doGet(toMap(request));

      // then
      verify(followArtistService).getFollowedArtistsOfCurrentUser();
    }

    @Test
    @DisplayName("Should pass query request parameter to release service")
    void should_pass_query_parameter_to_release_service() {
      // given
      PaginatedReleasesRequest request = PaginatedReleaseRequestFactory.createDefault();

      // when
      restAssuredUtils.doGet(toMap(request));

      // then
      verify(releasesService).findReleases(
          Collections.emptyList(),
          new TimeRange(request.getDateFrom(), request.getDateTo()),
          new PageRequest(request.getPage(), request.getSize(), null)
      );
    }

    @Test
    @DisplayName("SortingTransformer is called with given Sort")
    void should_call_sorting_transformer() {
      // given
      Map<String, Object> requestMap = Map.of("sort", "artist,ASC");

      // when
      restAssuredUtils.doGet(requestMap);

      // then
      verify(sortingTransformer).transform(Sort.by(Sort.Direction.ASC, "artist"));
    }

    @Test
    @DisplayName("SortingTransformer is called with default Sort if none is given")
    void should_call_sorting_transformer_with_default() {
      // when
      restAssuredUtils.doGet();

      // then
      verify(sortingTransformer).transform(Sort.by(Sort.Direction.ASC, "releaseDate", "artist", "albumTitle"));
    }

    @Test
    @DisplayName("Sorting is passed to release service")
    void should_call_release_service_with_sorting() {
      // given
      var sorting = new Sorting(List.of(new Sorting.Order(Sorting.Direction.ASC, "artist")));
      var expectedPageRequest = new PageRequest(1, 40, sorting);
      doReturn(sorting).when(sortingTransformer).transform(any());

      // when
      restAssuredUtils.doGet();

      // then
      verify(releasesService).findReleases(any(), any(), eq(expectedPageRequest));
    }

    @Test
    @DisplayName("Should pass artist names to release service")
    void should_pass_artist_names_to_release_service() {
      // given
      PaginatedReleasesRequest request = PaginatedReleaseRequestFactory.createDefault();
      var artist1 = DtoFactory.ArtistDtoFactory.withName("A");
      var artist2 = DtoFactory.ArtistDtoFactory.withName("B");
      var artist3 = DtoFactory.ArtistDtoFactory.withName("C");
      doReturn(List.of(artist1, artist2, artist3)).when(followArtistService).getFollowedArtistsOfCurrentUser();

      // when
      restAssuredUtils.doGet(toMap(request));

      // then
      verify(releasesService).findReleases(
          eq(List.of(artist1.getArtistName(), artist2.getArtistName(), artist3.getArtistName())),
          any(),
          any()
      );
    }

    @Test
    @DisplayName("Should return the page from release service")
    void should_return_releases() {
      // given
      var request = PaginatedReleaseRequestFactory.createDefault();
      var releases = List.of(ReleaseDtoFactory.createDefault());
      var page = new Page<>(releases, new Pagination(1, 1, 5));
      doReturn(page).when(releasesService).findReleases(any(), any(), any());

      // when
      var validatableResponse = restAssuredUtils.doGet(toMap(request));

      // then
      validatableResponse
          .contentType(ContentType.JSON)
          .statusCode(OK.value());

      var jsonPath = validatableResponse.extract().jsonPath();
      var paginationResult = jsonPath.getObject("pagination", Pagination.class);
      var itemsResult = jsonPath.getObject("items", ReleaseDto[].class);
      assertThat(paginationResult).isEqualTo(page.getPagination());
      assertThat(Arrays.asList(itemsResult)).isEqualTo(page.getItems());
    }

    @ParameterizedTest(name = "Should return 400 on invalid query request <{0}>")
    @MethodSource("requestProvider")
    @DisplayName("Should return 400 on invalid query request")
    void test_invalid_query_requests(PaginatedReleasesRequest request) {
      // when
      var validatableResponse = restAssuredUtils.doGet(toMap(request));

      // then
      validatableResponse
          .contentType(ContentType.JSON)
          .statusCode(BAD_REQUEST.value());
    }

    private Stream<Arguments> requestProvider() {
      var validPage = 1;
      var validSize = 10;
      var validFrom = LocalDate.now();
      var validTo = LocalDate.now().plusDays(10);

      return Stream.of(
          Arguments.of(new PaginatedReleasesRequest(0, validSize, validFrom, validTo)),
          Arguments.of(new PaginatedReleasesRequest(validPage, 0, validFrom, validTo)),
          Arguments.of(new PaginatedReleasesRequest(validPage, 51, validFrom, validTo)),
          Arguments.of(new PaginatedReleasesRequest(validPage, validSize, validFrom.plusDays(20), validTo))
      );
    }

    private Map<String, Object> toMap(PaginatedReleasesRequest request) {
      Map<String, Object> map = new HashMap<>();
      map.put("page", request.getPage());
      map.put("size", request.getSize());
      map.put("dateFrom", request.getDateFrom().toString());
      map.put("dateTo", request.getDateTo().toString());

      return map;
    }
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
      verify(releasesService).createImportJob();
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
      verify(releasesService).createRetryCoverDownloadJob();
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
      verify(releasesService).queryImportJobResults();
    }

    @Test
    @DisplayName("Should return result from release service with status OK")
    void should_return_status_created() {
      // given
      var importJobResultDto = List.of(
          ImportJobResultDtoFactory.createDefault(),
          ImportJobResultDtoFactory.createDefault()
      );
      doReturn(importJobResultDto).when(releasesService).queryImportJobResults();

      // when
      ValidatableMockMvcResponse validatableResponse = restAssuredUtils.doGet();

      // then
      validatableResponse.statusCode(OK.value());
      var result = validatableResponse.extract().body().jsonPath().getList(".", ImportJobResultDto.class);
      assertThat(result).isEqualTo(importJobResultDto);
    }
  }

  @Nested
  @DisplayName("Tests updating a release")
  class UpdateReleaseTest {

    private RestAssuredMockMvcUtils restAssuredUtils;

    @BeforeEach
    void setUp() {
      restAssuredUtils = new RestAssuredMockMvcUtils(Endpoints.Rest.RELEASES);
    }

    @Test
    @DisplayName("Should call release service")
    void should_call_release_service() {
      // given
      var releaseId = 1L;
      var state = "state";

      // when
      restAssuredUtils.doPut("/1", ReleaseUpdateRequest.builder().state(state).build());

      // then
      verify(releasesService).updateReleaseState(releaseId, state);
    }

    @Test
    @DisplayName("Should return OK")
    void should_return_status_ok() {
      // when
      ValidatableMockMvcResponse validatableResponse = restAssuredUtils.doPut("/1", ReleaseUpdateRequest.builder().state("state").build());

      // then
      validatableResponse.statusCode(OK.value());
    }
  }
}
