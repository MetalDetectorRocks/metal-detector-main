package rocks.metaldetector.web.controller.rest;

import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import rocks.metaldetector.config.constants.Endpoints;
import rocks.metaldetector.service.artist.ArtistsService;
import rocks.metaldetector.service.releases.ReleasesService;
import rocks.metaldetector.testutil.WithIntegrationTestProfile;
import rocks.metaldetector.web.DtoFactory.DetectorReleaseResponseFactory;
import rocks.metaldetector.web.DtoFactory.ReleaseDtoFactory;
import rocks.metaldetector.web.RestAssuredRequestHandler;
import rocks.metaldetector.web.dto.releases.ButlerReleasesRequest;
import rocks.metaldetector.web.dto.releases.ReleaseDto;
import rocks.metaldetector.web.dto.request.DetectorReleasesRequest;
import rocks.metaldetector.web.dto.response.DetectorReleasesResponse;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
class ReleasesRestControllerIT implements WithAssertions, WithIntegrationTestProfile {

  @MockBean
  private ReleasesService releasesService;

  @MockBean
  private ArtistsService artistsService;

  @MockBean
  ModelMapper modelMapper;

  @LocalServerPort
  private int port;

  @BeforeEach
  void setUp() {
    String requestUri = "http://localhost:" + port + Endpoints.Rest.RELEASES;
    requestHandler = new RestAssuredRequestHandler(requestUri);
  }

  @AfterEach
  void tearDown() {
    reset(releasesService, artistsService);
  }

  private RestAssuredRequestHandler requestHandler;

  @ParameterizedTest(name = "[{index}] => ButlerRequest <{0}> | RestControllerRequest <{1}> | MockedDtos <{2}> | ExpectedResponses <{3}>")
  @MethodSource("inputProvider")
  @DisplayName("POST should return valid results")
  void post_valid_result(ButlerReleasesRequest requestButler, DetectorReleasesRequest request, List<ReleaseDto> mockedDtos, List<DetectorReleasesResponse> expectedResponses) {
    // given
    when(releasesService.getReleases(requestButler)).thenReturn(mockedDtos);
    when(modelMapper.map(request, ButlerReleasesRequest.class)).thenReturn(requestButler);
    when(artistsService.findFollowedArtistsForCurrentUser()).thenReturn(Collections.emptyList());

    for (int i = 0; i < mockedDtos.size(); i++) {
      when(modelMapper.map(mockedDtos.get(i), DetectorReleasesResponse.class)).thenReturn(expectedResponses.get(i));
    }

    // when
    ValidatableResponse validatableResponse = requestHandler.doPost(request, ContentType.JSON);

    // then
    validatableResponse
        .contentType(ContentType.JSON)
        .statusCode(HttpStatus.OK.value());

    List<DetectorReleasesResponse> response = validatableResponse.extract().body().jsonPath().getList(".", DetectorReleasesResponse.class);

    assertThat(response).isEqualTo(expectedResponses);
    verify(releasesService, times(1)).getReleases(requestButler);
    verify(modelMapper, times(1)).map(request, ButlerReleasesRequest.class);
    verify(modelMapper, times(mockedDtos.size())).map(any(), eq(DetectorReleasesResponse.class));
  }

  @Test
  @DisplayName("POST with bad requests should return 400")
  void bad_requests() {
    // given
    DetectorReleasesRequest request = new DetectorReleasesRequest(null, null, null);

    // when
    ValidatableResponse validatableResponse = requestHandler.doPost(request, ContentType.JSON);

    // then
    validatableResponse
        .contentType(ContentType.JSON)
        .statusCode(HttpStatus.BAD_REQUEST.value());
  }

  private static Stream<Arguments> inputProvider() {
    LocalDate date = LocalDate.now();
    ReleaseDto releaseDto1 = ReleaseDtoFactory.withOneResult("A1", date);
    ReleaseDto releaseDto2 = ReleaseDtoFactory.withOneResult("A2", date);
    ReleaseDto releaseDto3 = ReleaseDtoFactory.withOneResult("A3", date);

    DetectorReleasesResponse releaseResponse1 = DetectorReleaseResponseFactory.withOneResult("A1", date);
    DetectorReleasesResponse releaseResponse2 = DetectorReleaseResponseFactory.withOneResult("A2", date);
    DetectorReleasesResponse releaseResponse3 = DetectorReleaseResponseFactory.withOneResult("A3", date);

    ButlerReleasesRequest requestAllButler = new ButlerReleasesRequest(null, null, Collections.singletonList("A1"));
    DetectorReleasesRequest requestAll = new DetectorReleasesRequest(null, null, Collections.emptyList());

    ButlerReleasesRequest requestA1Butler = new ButlerReleasesRequest(null, null, Collections.singletonList("A1"));
    DetectorReleasesRequest requestA1 = new DetectorReleasesRequest(null, null, Collections.emptyList());

    ButlerReleasesRequest requestA4Butler = new ButlerReleasesRequest(null, null, Collections.singletonList("A1"));
    DetectorReleasesRequest requestA4 = new DetectorReleasesRequest(null, null, Collections.emptyList());

    return Stream.of(
        Arguments.of(requestAllButler, requestAll, List.of(releaseDto1, releaseDto2, releaseDto3), List.of(releaseResponse1, releaseResponse2, releaseResponse3)),
        Arguments.of(requestA1Butler, requestA1, List.of(releaseDto1), List.of(releaseResponse1)),
        Arguments.of(requestA4Butler, requestA4, Collections.emptyList(), Collections.emptyList()));
  }
}