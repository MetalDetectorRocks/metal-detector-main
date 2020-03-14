package rocks.metaldetector.web;

import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.response.ValidatableMockMvcResponse;

import java.util.Collections;
import java.util.Map;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;

public class RestAssuredMockMvcUtils {

  private final ContentType CONTENT_TYPE = ContentType.JSON;
  private final String requestUri;

  public RestAssuredMockMvcUtils(String requestUri) {
    this.requestUri = requestUri;
  }

  public ValidatableMockMvcResponse doGet() {
    return doGet("", Collections.emptyMap());
  }

  public ValidatableMockMvcResponse doGet(Map<String,Object> params) {
    return doGet("", params);
  }

  public ValidatableMockMvcResponse doGet(String pathSegment) {
    return doGet(pathSegment, Collections.emptyMap());
  }

  public ValidatableMockMvcResponse doGet(String pathSegment, Map<String,Object> params) {
    return given()
             .accept(CONTENT_TYPE)
             .params(params)
           .when()
             .get(requestUri + pathSegment)
        .then();
  }

  public ValidatableMockMvcResponse doPost(String pathSegment) {
    return given()
           .contentType(CONTENT_TYPE)
           .accept(CONTENT_TYPE)
          .when()
           .post(requestUri + pathSegment)
        .then();
  }

  public ValidatableMockMvcResponse doPost(Object request) {
    return given()
            .accept(CONTENT_TYPE)
            .contentType(CONTENT_TYPE)
            .body(request)
          .when()
            .post(requestUri)
        .then();
  }

  public ValidatableMockMvcResponse doPut(Object request) {
    return given()
            .accept(CONTENT_TYPE)
            .contentType(CONTENT_TYPE)
            .body(request)
          .when()
            .put(requestUri)
        .then();
  }
}
