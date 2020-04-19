package rocks.metaldetector.support.infrastructure;

public interface WithTokenRemover {

  String HEADER_NAME = "Authorization";
  String REMOVED_FOR_LOGGING_STRING = "REMOVED_FOR_LOGGING";

  default String removeTokenForLogging(String headerString) {
    if (headerString != null && headerString.contains(HEADER_NAME)) {
      int startIndex = headerString.indexOf(HEADER_NAME) + HEADER_NAME.length() + 2;
      int endIndex = headerString.indexOf('"', startIndex);
      headerString = new StringBuilder(headerString).replace(startIndex, endIndex, REMOVED_FOR_LOGGING_STRING).toString();
    }

    return headerString;
  }
}
