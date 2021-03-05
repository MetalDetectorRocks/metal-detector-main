package rocks.metaldetector.support.infrastructure;

import java.util.List;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

public interface WithSensitiveDataRemover {

  String REMOVED_FOR_LOGGING_STRING = "REMOVED_FOR_LOGGING";
  String PAYLOAD_IDENTIFIER = "payload={";
  List<String> SENSITIVE_DATA_FIELD_NAME = List.of(
          "plainPassword",
          "verifyPlainPassword",
          "code",
          "token"
  );

  default String removeTokenFromHeader(String headerString) {
    if (headerString != null && headerString.contains(AUTHORIZATION)) {
      int startIndex = headerString.indexOf(AUTHORIZATION) + AUTHORIZATION.length() + 2;
      int endIndex = headerString.indexOf('"', startIndex);
      headerString = new StringBuilder(headerString).replace(startIndex, endIndex, REMOVED_FOR_LOGGING_STRING).toString();
    }

    return headerString;
  }

  default String removeSensitiveDataFromPayload(String originalMessage) {
    if (originalMessage != null && originalMessage.toLowerCase().contains(PAYLOAD_IDENTIFIER)) {
      String prePayload = originalMessage.substring(0, originalMessage.indexOf(PAYLOAD_IDENTIFIER));
      String payload = originalMessage.substring(originalMessage.indexOf(PAYLOAD_IDENTIFIER));
      for (String fieldName : SENSITIVE_DATA_FIELD_NAME) {
        if (payload.contains(fieldName)) {
          int sensitiveDataBeginIndex = payload.indexOf(fieldName) + fieldName.length() + "\":\"".length();
          int sensitiveDataEndIndex = payload.indexOf('"', sensitiveDataBeginIndex);
          payload = new StringBuilder(payload).replace(sensitiveDataBeginIndex, sensitiveDataEndIndex, REMOVED_FOR_LOGGING_STRING).toString();
        }
      }
      return prePayload.concat(payload);
    }

    return originalMessage;
  }
}
