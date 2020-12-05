package rocks.metaldetector.web;

import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.config.RestAssuredMockMvcConfig;
import io.restassured.module.mockmvc.response.ValidatableMockMvcResponse;
import org.springframework.http.MediaType;

import java.util.Collections;
import java.util.Map;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.config;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static io.restassured.module.mockmvc.config.MockMvcConfig.mockMvcConfig;

public class RestAssuredMockMvcUtils {

  private static final RestAssuredMockMvcConfig NO_SECURITY_CONFIG = config()
      .mockMvcConfig(mockMvcConfig().dontAutomaticallyApplySpringSecurityMockMvcConfigurer());

  private final String requestUri;

  public RestAssuredMockMvcUtils(String requestUri) {
    this.requestUri = requestUri;
  }

  public ValidatableMockMvcResponse doGet() {
    return doGet("", Collections.emptyMap());
  }

  public ValidatableMockMvcResponse doGet(MediaType mediaType) {
    return given()
            .config(NO_SECURITY_CONFIG)
            .accept(mediaType)
          .when()
            .get(requestUri)
          .then();
  }

  public ValidatableMockMvcResponse doGet(Map<String, Object> params) {
    return doGet("", params);
  }

  public ValidatableMockMvcResponse doGet(String pathSegment) {
    return doGet(pathSegment, Collections.emptyMap());
  }

  public ValidatableMockMvcResponse doGet(String pathSegment, Map<String, Object> params) {
    return given()
            .config(NO_SECURITY_CONFIG)
            .accept(ContentType.JSON)
            .params(params)
          .when()
            .get(requestUri + pathSegment)
          .then();
  }

  public ValidatableMockMvcResponse doGetWithAttributes(Map<String, Object> attributes) {
    return given()
            .config(NO_SECURITY_CONFIG)
            .accept(ContentType.JSON)
            .attributes(attributes)
          .when()
            .get(requestUri)
          .then();
  }

  public ValidatableMockMvcResponse doPost() {
    return given()
            .config(NO_SECURITY_CONFIG)
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
          .when()
            .post(requestUri)
          .then();
  }

  public ValidatableMockMvcResponse doPost(String pathSegment) {
    return given()
            .config(NO_SECURITY_CONFIG)
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
          .when()
            .post(requestUri + pathSegment)
          .then();
  }

  public ValidatableMockMvcResponse doPost(String pathSegment, Map<String, Object> requestParams) {
    return given()
            .config(NO_SECURITY_CONFIG)
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .params(requestParams)
          .when()
            .post(requestUri + pathSegment)
          .then();
  }

  public ValidatableMockMvcResponse doPost(Object request) {
    return given()
            .config(NO_SECURITY_CONFIG)
            .accept(ContentType.JSON)
            .contentType(ContentType.JSON)
            .body(request)
          .when()
            .post(requestUri)
          .then();
  }

  public ValidatableMockMvcResponse doPost(Map<String, String> params, ContentType contentType) {
    return given()
            .config(NO_SECURITY_CONFIG)
            .accept(contentType)
            .formParams(params)
          .when()
            .post(requestUri)
          .then();
  }

  public ValidatableMockMvcResponse doPut(String pathParam, Object request) {
    return given()
            .config(NO_SECURITY_CONFIG)
            .accept(ContentType.JSON)
            .contentType(ContentType.JSON)
            .body(request)
          .when()
            .put(requestUri + pathParam)
          .then();
  }

  public ValidatableMockMvcResponse doPut(Object request) {
    return doPut("", request);
  }
}
