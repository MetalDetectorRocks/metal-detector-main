package rocks.metaldetector.web.api.request;

import rocks.metaldetector.support.infrastructure.ArtifactForFramework;

import javax.validation.constraints.AssertTrue;
import java.time.LocalDate;

public interface WithTimeRangeValidation {

  @AssertTrue(message = "If dates are set, dateFrom has to be equal to or before dateTo!")
  @ArtifactForFramework
  default boolean isDateFromValid() {
    if (getDateFrom() != null && getDateTo() != null) {
      return getDateFrom().equals(getDateTo()) || getDateFrom().isBefore(getDateTo());
    }
    return true;
  }

  @AssertTrue(message = "dateTo must not be set without dateFrom!")
  @ArtifactForFramework
  default boolean isDateToValid() {
    return getDateFrom() != null || getDateTo() == null;
  }

  LocalDate getDateFrom();

  LocalDate getDateTo();

}
