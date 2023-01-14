package rocks.metaldetector.service.email;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface Email {

  String getRecipient();

  String getSubject();

  String getTemplateName();

  List<ViewModelEntry> getViewModelEntries();

  default Map<String, Object> createViewModel(String frontendUrl) {
    return getViewModelEntries().stream()
        .collect(
            Collectors.toMap(
                ViewModelEntry::name,
                entry -> entry.prependFrontendUrl() ? frontendUrl + entry.value() : entry.value()
            )
        );
  }
}
