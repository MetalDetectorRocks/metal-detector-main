package rocks.metaldetector.butler.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.ResourceLoader;
import rocks.metaldetector.butler.ButlerDtoFactory.ButlerReleasesResponseFactory;
import rocks.metaldetector.butler.api.ButlerImportJob;
import rocks.metaldetector.butler.api.ButlerReleasesResponse;

import java.io.Reader;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReleaseButlerRestClientMockTest implements WithAssertions {

  @Mock
  private ResourceLoader resourceLoader;

  @Mock
  private ObjectMapper objectMapper;

  @InjectMocks
  ReleaseButlerRestClientMock underTest;

  @ParameterizedTest(name = "Should use resource loader to load a resource from classpath")
  @MethodSource("queryMethodProvider")
  @DisplayName("Should use resource loader to load a resource from classpath")
  void test(Function<ReleaseButlerRestClientMock, ButlerReleasesResponse> function) throws Exception {
    // given
    when(resourceLoader.getResource(anyString())).thenReturn(new ClassPathResource(""));
    when(objectMapper.readValue(any(Reader.class), any(Class.class))).thenReturn(null);

    // when
    function.apply(underTest);

    // then
    verify(resourceLoader).getResource(startsWith("classpath:"));
  }

  @ParameterizedTest(name = "Should use object mapper to map classpath resource that should be returned")
  @MethodSource("queryMethodProvider")
  @DisplayName("Should use object mapper to map classpath resource that should be returned")
  void should_return_mock_query_response(Function<ReleaseButlerRestClientMock, ButlerReleasesResponse> function) throws Exception {
    // given
    ButlerReleasesResponse expectedResult = ButlerReleasesResponseFactory.createDefault();
    when(resourceLoader.getResource(anyString())).thenReturn(new ClassPathResource(""));
    when(objectMapper.readValue(any(Reader.class), any(Class.class))).thenReturn(expectedResult);

    // when
    ButlerReleasesResponse response = function.apply(underTest);

    // then
    verify(objectMapper).readValue(any(Reader.class), any(Class.class));
    assertThat(response).isEqualTo(expectedResult);
  }

  private static Stream<Arguments> queryMethodProvider() {
    Function<ReleaseButlerRestClientMock, ButlerReleasesResponse> queryReleases = underTest -> underTest.queryReleases(null);
    Function<ReleaseButlerRestClientMock, ButlerReleasesResponse> queryAllReleases = underTest -> underTest.queryAllReleases(null);
    return Stream.of(
            Arguments.of(queryReleases),
            Arguments.of(queryAllReleases)
    );
  }

  @Test
  @DisplayName("Should do nothing on create import job")
  void should_do_nothing_on_create_import_job() {
    // when
    underTest.createImportJob();
  }

  @Test
  @DisplayName("Should do nothing on create cover download job")
  void should_do_nothing_on_create_cover_download_job() {
    // when
    underTest.createRetryCoverDownloadJob();
  }

  @Test
  @DisplayName("Should return mocked import job responses")
  void should_return_mocked_import_job_responses() {
    // when
    List<ButlerImportJob> responses = underTest.queryImportJobResults();

    // then
    assertThat(responses).hasSize(4);
  }

  @Test
  @DisplayName("Should do nothing on update release")
  void should_do_nothing_on_update_release() {
    // when
    underTest.updateReleaseState(1L, "state");
  }
}
