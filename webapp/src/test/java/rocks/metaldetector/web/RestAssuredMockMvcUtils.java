package rocks.metaldetector.web;

import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.response.ValidatableMockMvcResponse;
import org.springframework.http.MediaType;

import java.util.Collections;
import java.util.Map;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;

public class RestAssuredMockMvcUtils {

  private final String requestUri;

  public RestAssuredMockMvcUtils(String requestUri) {
    this.requestUri = requestUri;
  }

  public ValidatableMockMvcResponse doGet() {
    return doGet("", Collections.emptyMap());
  }

  public ValidatableMockMvcResponse doGet(MediaType mediaType) {
    return given()
            .accept(mediaType)
          .when()
            .get(requestUri)
          .then();
  }

  public ValidatableMockMvcResponse doGet(Map<String,Object> params) {
    return doGet("", params);
  }

  public ValidatableMockMvcResponse doGet(String pathSegment) {
    return doGet(pathSegment, Collections.emptyMap());
  }

  public ValidatableMockMvcResponse doGet(String pathSegment, Map<String,Object> params) {
    return given()
             .accept(ContentType.JSON)
             .params(params)
           .when()
             .get(requestUri + pathSegment)
        .then();
  }

  public ValidatableMockMvcResponse doGetWithAttributes(Map<String,Object> attributes) {
    return given()
            .accept(ContentType.JSON)
            .attributes(attributes)
          .when()
            .get(requestUri)
          .then();
  }

  public ValidatableMockMvcResponse doPost() {
    return given()
           .contentType(ContentType.JSON)
           .accept(ContentType.JSON)
          .when()
           .post(requestUri)
        .then();
  }

  public ValidatableMockMvcResponse doPost(String pathSegment) {
    return given()
        .contentType(ContentType.JSON)
        .accept(ContentType.JSON)
        .when()
        .post(requestUri + pathSegment)
        .then();
  }

  public ValidatableMockMvcResponse doPost(Object request) {
    return given()
            .accept(ContentType.JSON)
            .contentType(ContentType.JSON)
            .body(request)
          .when()
            .post(requestUri)
        .then();
  }

  public ValidatableMockMvcResponse doPost(Map<String, String> params, ContentType contentType) {
    return given()
            .accept(contentType)
            .formParams(params)
          .when()
            .post(requestUri)
          .then();
  }

  public ValidatableMockMvcResponse doPut(Object request) {
    return given()
            .accept(ContentType.JSON)
            .contentType(ContentType.JSON)
            .body(request)
          .when()
            .put(requestUri)
        .then();
  }
}
