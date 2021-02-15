package rocks.metaldetector.service.email;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static rocks.metaldetector.support.Endpoints.Frontend.IMPRINT;
import static rocks.metaldetector.support.Endpoints.Frontend.NOTIFICATION_SETTINGS;
import static rocks.metaldetector.support.Endpoints.Frontend.PRIVACY_POLICY;

public abstract class AbstractEmail {

  private final List<ViewModelEntry> viewModelEntries;

  AbstractEmail() {
    viewModelEntries = new ArrayList<>();
    addDefaultEntries();
  }

  private void addDefaultEntries() {
    viewModelEntries.add(ViewModelEntry.builder()
            .name("notificationSettingsUrl")
            .value(NOTIFICATION_SETTINGS)
            .relativeUrl(true)
            .build()
    );
    viewModelEntries.add(ViewModelEntry.builder()
            .name("imprintUrl")
            .value(IMPRINT)
            .relativeUrl(true)
            .build()
    );
    viewModelEntries.add(ViewModelEntry.builder()
            .name("privacyPolicyUrl")
            .value(PRIVACY_POLICY)
            .relativeUrl(true)
            .build()
    );
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
  private final Object value;
  private final boolean relativeUrl;

}
