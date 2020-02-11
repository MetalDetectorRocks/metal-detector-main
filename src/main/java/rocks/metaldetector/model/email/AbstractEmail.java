package rocks.metaldetector.model.email;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractEmail {

  private final List<ViewModelEntry> viewModelEntries;

  AbstractEmail() {
    viewModelEntries = new ArrayList<>();
  }

  public abstract String getRecipient();

  public abstract String getSubject();

  public abstract String getTemplateName();

  abstract void buildViewModel();

  public Map<String, Object> getEnhancedViewModel(String baseUrl) {
    buildViewModel();
    Map<String, Object> viewModel = new HashMap<>();
    for (ViewModelEntry entry : viewModelEntries) {
      if (entry.isRelativeUrl()) {
        viewModel.put(entry.getName(), baseUrl + entry.getValue());
      }
      else {
        viewModel.put(entry.getName(), entry.getValue());
      }
    }

    return viewModel;
  }

  void addViewModelEntry(ViewModelEntry entry) {
    viewModelEntries.add(entry);
  }

}

@Getter
@Builder
@AllArgsConstructor
class ViewModelEntry {

  private final String name;
  private final String value;
  private final boolean relativeUrl;

}
