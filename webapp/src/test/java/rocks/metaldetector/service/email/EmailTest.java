package rocks.metaldetector.service.email;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

class EmailTest implements WithAssertions {

  private final Email underTest = new DummyEmailImpl();

  @Test
  @DisplayName("should return map of name and value")
  void should_return_map_of_name_and_value() {
    // when
    Map<String, Object> viewModel = underTest.createViewModel(null);

    // then
    assertThat(viewModel).hasSize(3);
    assertThat(viewModel).containsEntry("foo", "bar");
    assertThat(viewModel).containsEntry("bar", "foo");
  }

  @Test
  @DisplayName("should return map of name and value with prepended frontend url")
  void should_return_map_of_name_and_value_with_prepended_frontend_url() {
    // given
    String frontendUrl = "http://localhost";

    // when
    Map<String, Object> viewModel = underTest.createViewModel(frontendUrl);

    // then
    assertThat(viewModel).hasSize(3);
    assertThat(viewModel).containsEntry("url", frontendUrl + "/test-site");
  }
}

class DummyEmailImpl implements Email {

  @Override
  public String getRecipient() {
    return null;
  }

  @Override
  public String getSubject() {
    return null;
  }

  @Override
  public String getTemplateName() {
    return null;
  }

  @Override
  public List<ViewModelEntry> getViewModelEntries() {
    return List.of(
        new ViewModelEntry("foo", "bar"),
        new ViewModelEntry("bar", "foo"),
        new ViewModelEntry("url", "/test-site", true)
    );
  }
}
