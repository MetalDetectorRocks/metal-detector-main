package rocks.metaldetector.support.infrastructure;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * The Annotation should be used wherever code is written that is intended exclusively
 * for the used framework (e.g. Spring or Hibernate).
 *
 * With the help of this annotation the IntelliJ-Inspections for the code fragment can be deactivated.
 */
@Retention(value = RetentionPolicy.SOURCE)
public @interface ArtifactForFramework {
}
