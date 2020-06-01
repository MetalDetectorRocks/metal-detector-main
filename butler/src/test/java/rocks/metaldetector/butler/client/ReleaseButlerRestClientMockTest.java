package rocks.metaldetector.butler.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.ResourceLoader;
import rocks.metaldetector.butler.ButlerDtoFactory.ButlerReleasesResponseFactory;
import rocks.metaldetector.butler.api.ButlerImportJobResponse;
import rocks.metaldetector.butler.api.ButlerReleasesResponse;

import java.io.Reader;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.times;
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

  @Test
  @DisplayName("Should use resource loader to load a resource from classpath")
  void test() throws Exception {
    // given
    when(resourceLoader.getResource(anyString())).thenReturn(new ClassPathResource(""));
    when(objectMapper.readValue(any(Reader.class), any(Class.class))).thenReturn(null);

    // when
    underTest.queryReleases(null);

    // then
    verify(resourceLoader, times(1)).getResource(startsWith("classpath:"));
  }

  @Test
  @DisplayName("Should use object mapper to map classpath resource that should be returned")
  void should_return_mock_query_response() throws Exception {
    // given
    ButlerReleasesResponse expectedResult = ButlerReleasesResponseFactory.createDefault();
    when(resourceLoader.getResource(anyString())).thenReturn(new ClassPathResource(""));
    when(objectMapper.readValue(any(Reader.class), any(Class.class))).thenReturn(expectedResult);

    // when
    ButlerReleasesResponse response = underTest.queryReleases(null);

    // then
    verify(objectMapper, times(1)).readValue(any(Reader.class), any(Class.class));
    assertThat(response).isEqualTo(expectedResult);
  }

  @Test
  @DisplayName("Should do nothing on create import job")
  void should_do_nothing_on_create_import_job() {
    // when
    underTest.createImportJob();
  }

  @Test
  @DisplayName("Should return mocked import job responses")
  void should_return_mocked_import_job_responses() {
    // when
    List<ButlerImportJobResponse> responses = underTest.queryImportJobResults();

    // then
    assertThat(responses).hasSize(4);
  }
}
