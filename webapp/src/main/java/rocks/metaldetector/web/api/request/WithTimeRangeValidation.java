package rocks.metaldetector.web.api.request;

import rocks.metaldetector.support.infrastructure.ArtifactForFramework;

import javax.validation.constraints.AssertTrue;
import java.time.LocalDate;

public interface WithTimeRangeValidation {

  @AssertTrue(message = "If dates are set, dateFrom has to be equal to or before dateTo!")
  @ArtifactForFramework
  default boolean isValid() {
    if (getDateFrom() != null && getDateTo() != null) {
      return getDateFrom().equals(getDateTo()) || getDateFrom().isBefore(getDateTo());
    }
    return true;
  }

  LocalDate getDateFrom();

  LocalDate getDateTo();

}
