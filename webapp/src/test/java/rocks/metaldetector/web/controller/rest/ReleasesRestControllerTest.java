package rocks.metaldetector.web.controller.rest;

import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import io.restassured.module.mockmvc.response.ValidatableMockMvcResponse;
import org.assertj.core.api.WithAssertions;
import org.assertj.core.data.TemporalUnitWithinOffset;
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
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.SortHandlerMethodArgumentResolver;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder;
import rocks.metaldetector.butler.facade.ReleaseService;
import rocks.metaldetector.butler.facade.dto.ReleaseDto;
import rocks.metaldetector.service.artist.FollowArtistService;
import rocks.metaldetector.service.dashboard.ArtistCollector;
import rocks.metaldetector.service.dashboard.ReleaseCollector;
import rocks.metaldetector.service.exceptions.RestExceptionsHandler;
import rocks.metaldetector.support.DetectorSort;
import rocks.metaldetector.support.Page;
import rocks.metaldetector.support.PageRequest;
import rocks.metaldetector.support.Pagination;
import rocks.metaldetector.support.TimeRange;
import rocks.metaldetector.testutil.DtoFactory;
import rocks.metaldetector.testutil.DtoFactory.ReleaseDtoFactory;
import rocks.metaldetector.testutil.DtoFactory.ReleaseRequestFactory;
import rocks.metaldetector.web.RestAssuredMockMvcUtils;
import rocks.metaldetector.web.api.request.PaginatedReleasesRequest;
import rocks.metaldetector.web.api.request.ReleaseUpdateRequest;
import rocks.metaldetector.web.api.request.ReleasesRequest;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static rocks.metaldetector.support.Endpoints.Rest.ALL_RELEASES;
import static rocks.metaldetector.support.Endpoints.Rest.RELEASES;
import static rocks.metaldetector.support.Endpoints.Rest.TOP_UPCOMING_RELEASES;
import static rocks.metaldetector.testutil.DtoFactory.PaginatedReleaseRequestFactory;

@ExtendWith(MockitoExtension.class)
class ReleasesRestControllerTest implements WithAssertions {

  @Mock
  private ReleaseService releasesService;

  @Mock
  private FollowArtistService followArtistService;

  @Mock
  private ArtistCollector artistCollector;

  @Mock
  private ReleaseCollector releaseCollector;

  @BeforeEach
  void setUp() {
    ReleasesRestController underTest = new ReleasesRestController(releasesService, followArtistService, artistCollector, releaseCollector);
    StandaloneMockMvcBuilder mockMvcBuilder = MockMvcBuilders.standaloneSetup(underTest, RestExceptionsHandler.class)
        .setCustomArgumentResolvers(new SortHandlerMethodArgumentResolver());
    RestAssuredMockMvc.standaloneSetup(mockMvcBuilder);
  }

  @AfterEach
  void tearDown() {
    reset(releasesService, followArtistService, artistCollector, releaseCollector);
  }

  @Nested
  @TestInstance(PER_CLASS)
  @DisplayName("Tests for endpoint '" + ALL_RELEASES + "'")
  class QueryAllReleasesTest {

    private RestAssuredMockMvcUtils restAssuredUtils;

    @BeforeEach
    void setUp() {
      restAssuredUtils = new RestAssuredMockMvcUtils(ALL_RELEASES);
    }

    @Test
    @DisplayName("Should pass query request parameter to release service")
    void should_pass_query_parameter_to_release_service() {
      // given
      ReleasesRequest request = ReleaseRequestFactory.createDefault();

      // when
      restAssuredUtils.doGet(toMap(request));

      // then
      verify(releasesService).findAllReleases(emptyList(), new TimeRange(request.getDateFrom(), request.getDateTo()));
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

    @ParameterizedTest(name = "Should return 422 on invalid query request <{0}>")
    @MethodSource("requestProvider")
    @DisplayName("Should return 422 on invalid query request")
    void test_invalid_query_requests(ReleasesRequest request) {
      // when
      var validatableResponse = restAssuredUtils.doGet(toMap(request));

      // then
      validatableResponse
          .contentType(ContentType.JSON)
          .statusCode(UNPROCESSABLE_ENTITY.value());
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
  @TestInstance(PER_CLASS)
  @DisplayName("Tests for endpoint '" + RELEASES + "'")
  class QueryReleasesTest {

    private RestAssuredMockMvcUtils restAssuredUtils;

    @BeforeEach
    void setUp() {
      restAssuredUtils = new RestAssuredMockMvcUtils(RELEASES);
    }

    @Test
    @DisplayName("Should call follow artist service if releasesFilter is 'my'")
    void should_call_follow_artist_service() {
      // given
      PaginatedReleasesRequest request = PaginatedReleaseRequestFactory.createDefault();
      request.setReleasesFilter("my");

      // when
      restAssuredUtils.doGet(toMap(request));

      // then
      verify(followArtistService).getFollowedArtistsOfCurrentUser();
    }

    @Test
    @DisplayName("Should not call follow artist service if releasesFilter is 'all'")
    void should_not_call_follow_artist_service() {
      // given
      PaginatedReleasesRequest request = PaginatedReleaseRequestFactory.createDefault();

      // when
      restAssuredUtils.doGet(toMap(request));

      // then
      verifyNoInteractions(followArtistService);
    }

    @Test
    @DisplayName("Should not call releaseService if user does not follow any artists")
    void should_not_call_release_service() {
      // given
      PaginatedReleasesRequest request = PaginatedReleaseRequestFactory.createDefault();
      request.setReleasesFilter("my");
      doReturn(emptyList()).when(followArtistService).getFollowedArtistsOfCurrentUser();

      // when
      restAssuredUtils.doGet(toMap(request));

      // then
      verifyNoInteractions(releasesService);
    }

    @Test
    @DisplayName("Should return empty page if user does not follow any artists")
    void should_return_empty_page() {
      // given
      PaginatedReleasesRequest request = PaginatedReleaseRequestFactory.createDefault();
      request.setReleasesFilter("my");
      doReturn(emptyList()).when(followArtistService).getFollowedArtistsOfCurrentUser();

      // when
      var validatableResponse = restAssuredUtils.doGet(toMap(request));

      // then
      var jsonPath = validatableResponse.extract().jsonPath();
      var itemsResult = jsonPath.getObject("items", ReleaseDto[].class);
      var paginationResult = jsonPath.getObject("pagination", Pagination.class);
      assertThat(itemsResult).isEmpty();
      assertThat(paginationResult).isEqualTo(Page.empty().getPagination());
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
          eq(emptyList()),
          eq(new TimeRange(request.getDateFrom(), request.getDateTo())),
          any(),
          eq(new PageRequest(request.getPage(), request.getSize(), new DetectorSort(request.getSort(), request.getDirection())))
      );
    }

    @Test
    @DisplayName("Should pass followed artists names to release service")
    void should_pass_artists_to_release_service() {
      // given
      PaginatedReleasesRequest request = PaginatedReleaseRequestFactory.createDefault();
      request.setReleasesFilter("my");
      var artist = DtoFactory.ArtistDtoFactory.withName("superArtist");
      doReturn(List.of(artist)).when(followArtistService).getFollowedArtistsOfCurrentUser();

      // when
      restAssuredUtils.doGet(toMap(request));

      // then
      verify(releasesService).findReleases(eq(List.of(artist.getArtistName())), any(), any(), any());
    }

    @Test
    @DisplayName("Should pass search query to release service")
    void should_pass_search_query_to_release_service() {
      // given
      PaginatedReleasesRequest request = PaginatedReleaseRequestFactory.createDefault();
      request.setQuery("query");

      // when
      restAssuredUtils.doGet(toMap(request));

      // then
      verify(releasesService).findReleases(any(), any(), eq(request.getQuery()), any());
    }

    @Test
    @DisplayName("Should return the page from release service")
    void should_return_releases() {
      // given
      var request = PaginatedReleaseRequestFactory.createDefault();
      var releases = List.of(ReleaseDtoFactory.createDefault());
      var page = new Page<>(releases, new Pagination(1, 1, 5));
      doReturn(page).when(releasesService).findReleases(any(), any(), any(), any());

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

    @ParameterizedTest(name = "Should return 422 on invalid query request <{0}>")
    @MethodSource("requestProvider")
    @DisplayName("Should return 422 on invalid query request")
    void test_invalid_query_requests(PaginatedReleasesRequest request) {
      // when
      var validatableResponse = restAssuredUtils.doGet(toMap(request));

      // then
      validatableResponse
          .statusCode(UNPROCESSABLE_ENTITY.value());
    }

    private Stream<Arguments> requestProvider() {
      var validPage = 1;
      var validSize = 10;
      var validSort = "release_date";
      var validDirection = "asc";
      var validReleasesFilter = "all";
      var validFrom = LocalDate.now();
      var validTo = LocalDate.now().plusDays(10);
      var query = "query";

      return Stream.of(
          Arguments.of(new PaginatedReleasesRequest(0, validSize, validSort, validDirection, validFrom, validTo, query, validReleasesFilter)),
          Arguments.of(new PaginatedReleasesRequest(validPage, 0, validSort, validDirection, validFrom, validTo, query, validReleasesFilter)),
          Arguments.of(new PaginatedReleasesRequest(validPage, 51, validSort, validDirection, validFrom, validTo, query, validReleasesFilter)),
          Arguments.of(new PaginatedReleasesRequest(validPage, validSize, "", validDirection, validFrom, validTo, query, validReleasesFilter)),
          Arguments.of(new PaginatedReleasesRequest(validPage, validSize, " ", validDirection, validFrom, validTo, query, validReleasesFilter)),
          Arguments.of(new PaginatedReleasesRequest(validPage, validSize, validSort, "", validFrom, validTo, query, validReleasesFilter)),
          Arguments.of(new PaginatedReleasesRequest(validPage, validSize, validSort, "foo", validFrom, validTo, query, validReleasesFilter)),
          Arguments.of(new PaginatedReleasesRequest(validPage, validSize, validSort, validDirection, validFrom, validTo, query, "")),
          Arguments.of(new PaginatedReleasesRequest(validPage, validSize, validSort, validDirection, validFrom, validTo, query, "foo")),
          Arguments.of(new PaginatedReleasesRequest(validPage, validSize, validSort, validDirection, validFrom.plusDays(20), validTo, query, validReleasesFilter))
      );
    }

    private Map<String, Object> toMap(PaginatedReleasesRequest request) {
      Map<String, Object> map = new HashMap<>();
      map.put("page", request.getPage());
      map.put("size", request.getSize());
      map.put("sort", request.getSort());
      map.put("direction", request.getDirection());
      map.put("dateFrom", request.getDateFrom().toString());
      map.put("dateTo", request.getDateTo().toString());
      map.put("query", request.getQuery());
      map.put("releasesFilter", request.getReleasesFilter());

      return map;
    }
  }

  @Nested
  @DisplayName("Tests updating a release")
  class UpdateReleaseTest {

    private RestAssuredMockMvcUtils restAssuredUtils;

    @BeforeEach
    void setUp() {
      restAssuredUtils = new RestAssuredMockMvcUtils(RELEASES);
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

  @Nested
  @DisplayName("Tests for endpoint '" + TOP_UPCOMING_RELEASES + "'")
  class FetchTopUpcomingReleasesTest {

    private RestAssuredMockMvcUtils restAssuredUtils;

    @BeforeEach
    void setUp() {
      restAssuredUtils = new RestAssuredMockMvcUtils(TOP_UPCOMING_RELEASES);
    }

    @Test
    @DisplayName("should call artist collector")
    void should_call_artist_collector() {
      // given
      var minFollower = 10;
      Map<String, Object> requestParams = new HashMap<>();
      requestParams.put("minFollower", minFollower);

      // when
      restAssuredUtils.doGet(requestParams);

      // then
      verify(artistCollector).collectTopFollowedArtists(minFollower);
    }

    @Test
    @DisplayName("should call release collector with fixed time range")
    void should_call_release_collector_with_fixed_time_range() {
      // given
      var expectedFromDate = LocalDate.now();
      var expectedToDate = expectedFromDate.plusMonths(6);
      var offset = new TemporalUnitWithinOffset(1, DAYS);
      ArgumentCaptor<TimeRange> timeRangeCaptor = ArgumentCaptor.forClass(TimeRange.class);

      // when
      restAssuredUtils.doGet();

      // then
      verify(releaseCollector).collectTopReleases(timeRangeCaptor.capture(), any(), anyInt());
      TimeRange timeRange = timeRangeCaptor.getValue();
      assertThat(timeRange.getDateFrom()).isCloseTo(expectedFromDate, offset);
      assertThat(timeRange.getDateTo()).isCloseTo(expectedToDate, offset);
    }

    @Test
    @DisplayName("should call release collector with top artists")
    void should_call_release_collector_with_top_artists() {
      // given
      var artist1 = DtoFactory.ArtistDtoFactory.withName("A");
      var artist2 = DtoFactory.ArtistDtoFactory.withName("B");
      var artists = List.of(artist1, artist2);
      doReturn(artists).when(artistCollector).collectTopFollowedArtists(anyInt());

      // when
      restAssuredUtils.doGet();

      // then
      verify(releaseCollector).collectTopReleases(any(), eq(artists), anyInt());
    }

    @Test
    @DisplayName("should call release collector with limit parameter")
    void should_call_release_collector_with_limit_parameter() {
      // given
      var limit = 1;
      Map<String, Object> requestParams = new HashMap<>();
      requestParams.put("limit", limit);

      // when
      restAssuredUtils.doGet(requestParams);

      // then
      verify(releaseCollector).collectTopReleases(any(), any(), eq(limit));
    }

    @Test
    @DisplayName("should return releases from release collector")
    void should_return_releases_from_release_collector() {
      // given
      var release1 = ReleaseDtoFactory.withArtistName("A");
      var release2 = ReleaseDtoFactory.withArtistName("B");
      var releases = List.of(release1, release2);
      doReturn(releases).when(releaseCollector).collectTopReleases(any(), any(), anyInt());

      // when
      var validatableResponse = restAssuredUtils.doGet();

      // then
      var responseBody = validatableResponse.extract().as(ReleaseDto[].class);
      assertThat(responseBody).containsExactly(releases.toArray(new ReleaseDto[0]));
    }

    @Test
    @DisplayName("should return status code 200")
    void should_return_status_code_200() {
      // when
      var result = restAssuredUtils.doGet();

      // then
      result.assertThat(status().isOk());
    }
  }
}
