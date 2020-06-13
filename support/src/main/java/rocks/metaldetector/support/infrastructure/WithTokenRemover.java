package rocks.metaldetector.support.infrastructure;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

public interface WithTokenRemover {

  String REMOVED_FOR_LOGGING_STRING = "REMOVED_FOR_LOGGING";

  default String removeTokenForLogging(String headerString) {
    if (headerString != null && headerString.contains(AUTHORIZATION)) {
      int startIndex = headerString.indexOf(AUTHORIZATION) + AUTHORIZATION.length() + 2;
      int endIndex = headerString.indexOf('"', startIndex);
      headerString = new StringBuilder(headerString).replace(startIndex, endIndex, REMOVED_FOR_LOGGING_STRING).toString();
    }

    return headerString;
  }
}
