package rocks.metaldetector.web;

import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.response.MockMvcResponse;
import io.restassured.module.mockmvc.response.ValidatableMockMvcResponse;

import java.util.Collections;
import java.util.Map;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;

public class RestAssuredMockMvcUtils {

  private final String requestUri;

  public RestAssuredMockMvcUtils(String requestUri) {
    this.requestUri = requestUri;
//    RestAssured.defaultParser = Parser.JSON;
  }

  public ValidatableMockMvcResponse doGet(ContentType accept) {
    return doGet("", accept, Collections.emptyMap());
  }

  public ValidatableMockMvcResponse doGet(ContentType accept, Map<String,Object> params) {
    return doGet("", accept, params);
  }

  public ValidatableMockMvcResponse doGet(String pathSegment, ContentType accept) {
    return doGet(pathSegment, accept, Collections.emptyMap());
  }

  public ValidatableMockMvcResponse doGet(String pathSegment, ContentType accept, Map<String,Object> params) {
    return given()
             .accept(accept)
             .params(params)
           .when()
             .get(requestUri + pathSegment)
        .then();
  }

  public ValidatableMockMvcResponse doPost(String pathSegment, ContentType accept) {
    return given()
           .contentType(accept)
           .accept(accept)
          .when()
           .post(requestUri + pathSegment)
        .then();
  }

  public ValidatableMockMvcResponse doPost(Object request, ContentType accept) {
    return given()
            .accept(accept)
            .contentType(accept)
            .body(request)
          .when()
            .post(requestUri)
        .then();
  }

  public ValidatableMockMvcResponse doPut(Object request, ContentType accept) {
    return given()
            .accept(accept)
            .contentType(accept)
            .body(request)
          .when()
            .put(requestUri)
        .then();
  }
}
