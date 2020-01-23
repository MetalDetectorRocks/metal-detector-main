package com.metalr2.web.controller.rest;

import com.metalr2.config.constants.Endpoints;
import com.metalr2.service.releases.ReleasesService;
import com.metalr2.testutil.WithIntegrationTestProfile;
import com.metalr2.web.DtoFactory;
import com.metalr2.web.RestAssuredRequestHandler;
import com.metalr2.web.dto.releases.ReleaseDto;
import com.metalr2.web.dto.releases.ReleasesButlerRequest;
import com.metalr2.web.dto.request.ReleasesRequest;
import com.metalr2.web.dto.response.ReleasesResponse;
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
    reset(releasesService);
  }

  private RestAssuredRequestHandler requestHandler;

  @ParameterizedTest(name = "[{index}] => ButlerRequest <{0}> | RestControllerRequest <{1}> | MockedDtos <{2}> | ExpectedResponses <{3}>")
  @MethodSource("inputProvider")
  @DisplayName("POST should return valid results")
  void post_valid_result(ReleasesButlerRequest requestButler, ReleasesRequest request, List<ReleaseDto> mockedDtos, List<ReleasesResponse> expectedResponses) {
    // given
    when(releasesService.getReleases(requestButler)).thenReturn(mockedDtos);
    when(modelMapper.map(request, ReleasesButlerRequest.class)).thenReturn(requestButler);

    for (int i = 0; i < mockedDtos.size(); i++) {
      when(modelMapper.map(mockedDtos.get(i), ReleasesResponse.class)).thenReturn(expectedResponses.get(i));
    }

    // when
    ValidatableResponse validatableResponse = requestHandler.doPost(request, ContentType.JSON);

    // then
    validatableResponse
        .contentType(ContentType.JSON)
        .statusCode(HttpStatus.OK.value());

    List<ReleasesResponse> response = validatableResponse.extract().body().jsonPath().getList(".", ReleasesResponse.class);

    assertThat(response).isEqualTo(expectedResponses);
    verify(releasesService, times(1)).getReleases(requestButler);
    verify(modelMapper, times(1)).map(request, ReleasesButlerRequest.class);
    verify(modelMapper, times(mockedDtos.size())).map(any(), eq(ReleasesResponse.class));
  }

  private static Stream<Arguments> inputProvider() {
    LocalDate date = LocalDate.now();
    ReleaseDto releaseDto1 = DtoFactory.ReleaseDtoFactory.withOneResult("A1", date);
    ReleaseDto releaseDto2 = DtoFactory.ReleaseDtoFactory.withOneResult("A2", date);
    ReleaseDto releaseDto3 = DtoFactory.ReleaseDtoFactory.withOneResult("A3", date);

    ReleasesResponse releaseResponse1 = DtoFactory.ReleaseResponseFactory.withOneResult("A1", date);
    ReleasesResponse releaseResponse2 = DtoFactory.ReleaseResponseFactory.withOneResult("A2", date);
    ReleasesResponse releaseResponse3 = DtoFactory.ReleaseResponseFactory.withOneResult("A3", date);

    ReleasesButlerRequest requestAllButler = new ReleasesButlerRequest(null, null, Collections.singletonList("A1"));
    ReleasesRequest requestAll = new ReleasesRequest(null, null, Collections.emptyList());

    ReleasesButlerRequest requestA1Butler = new ReleasesButlerRequest(null, null, Collections.singletonList("A1"));
    ReleasesRequest requestA1 = new ReleasesRequest(null, null, Collections.emptyList());

    ReleasesButlerRequest requestA4Butler = new ReleasesButlerRequest(null, null, Collections.singletonList("A1"));
    ReleasesRequest requestA4 = new ReleasesRequest(null, null, Collections.emptyList());

    return Stream.of(
        Arguments.of(requestAllButler, requestAll, List.of(releaseDto1, releaseDto2, releaseDto3), List.of(releaseResponse1, releaseResponse2, releaseResponse3)),
        Arguments.of(requestA1Butler, requestA1, List.of(releaseDto1), List.of(releaseResponse1)),
        Arguments.of(requestA4Butler, requestA4, Collections.emptyList(), Collections.emptyList()));
  }

  @Test
  @DisplayName("POST with bad requests should return 400")
  void bad_requests() {
    // given
    ReleasesRequest request = new ReleasesRequest(null, null, null);

    // when
    ValidatableResponse validatableResponse = requestHandler.doPost(request, ContentType.JSON);

    // then
    validatableResponse
        .contentType(ContentType.JSON)
        .statusCode(HttpStatus.BAD_REQUEST.value());
  }

}