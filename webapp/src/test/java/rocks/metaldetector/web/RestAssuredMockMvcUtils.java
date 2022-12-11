package rocks.metaldetector.web;

import io.restassured.http.ContentType;
import io.restassured.http.Headers;
import io.restassured.module.mockmvc.config.RestAssuredMockMvcConfig;
import io.restassured.module.mockmvc.response.ValidatableMockMvcResponse;
import org.springframework.http.MediaType;

import java.util.Collections;
import java.util.Map;

import static io.restassured.http.ContentType.JSON;
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
            .accept(JSON)
            .params(params)
          .when()
            .get(requestUri + pathSegment)
          .then();
  }

  public ValidatableMockMvcResponse doGetWithAttributes(Map<String, Object> attributes) {
    return given()
            .config(NO_SECURITY_CONFIG)
            .accept(JSON)
            .attributes(attributes)
          .when()
            .get(requestUri)
          .then();
  }

  public ValidatableMockMvcResponse doGetWithCookies(Map<String, Object> cookies) {
    return given()
          .config(NO_SECURITY_CONFIG)
          .accept(JSON)
          .cookies(cookies)
        .when()
          .get(requestUri)
        .then();
  }

  public Headers doGetWithCookiesReturningHeaders(Map<String, Object> cookies) {
    return given()
          .config(NO_SECURITY_CONFIG)
          .accept(JSON)
          .cookies(cookies)
        .when()
          .get(requestUri)
        .getHeaders();
  }

  public ValidatableMockMvcResponse doPost() {
    return given()
            .config(NO_SECURITY_CONFIG)
            .contentType(JSON)
            .accept(JSON)
          .when()
            .post(requestUri)
          .then();
  }

  public ValidatableMockMvcResponse doPost(String pathSegment) {
    return given()
            .config(NO_SECURITY_CONFIG)
            .contentType(JSON)
            .accept(JSON)
          .when()
            .post(requestUri + pathSegment)
          .then();
  }

  public ValidatableMockMvcResponse doPost(String pathSegment, Map<String, Object> requestParams) {
    return given()
            .config(NO_SECURITY_CONFIG)
            .contentType(JSON)
            .accept(JSON)
            .params(requestParams)
          .when()
            .post(requestUri + pathSegment)
          .then();
  }

  public ValidatableMockMvcResponse doPost(Object request) {
    return doPost(request, "");
  }

  public ValidatableMockMvcResponse doPost(Object request, String pathSegment) {
    return given()
            .config(NO_SECURITY_CONFIG)
            .accept(JSON)
            .contentType(JSON)
            .body(request)
          .when()
            .post(requestUri + pathSegment)
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

  public Headers doPostReturningHeaders(Object request) {
    return given()
            .config(NO_SECURITY_CONFIG)
              .accept(JSON)
              .contentType(JSON)
              .body(request)
            .when()
              .post(requestUri)
            .getHeaders();
  }

  public ValidatableMockMvcResponse doPut(String pathParam, Object request) {
    return given()
            .config(NO_SECURITY_CONFIG)
            .accept(JSON)
            .contentType(JSON)
            .body(request)
          .when()
            .put(requestUri + pathParam)
          .then();
  }

  public ValidatableMockMvcResponse doPut(Object request) {
    return doPut("", request);
  }

  public ValidatableMockMvcResponse doPatch(Object request) {
    return given()
            .config(NO_SECURITY_CONFIG)
            .accept(JSON)
            .contentType(JSON)
            .body(request)
          .when()
            .patch(requestUri)
          .then();
  }

  public ValidatableMockMvcResponse doDelete() {
    return doDelete("");
  }

  public ValidatableMockMvcResponse doDelete(String pathSegment) {
    return given()
            .config(NO_SECURITY_CONFIG)
            .contentType(JSON)
          .when()
            .delete(requestUri + pathSegment)
          .then();
  }
}
