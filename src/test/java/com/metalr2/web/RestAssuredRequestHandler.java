package com.metalr2.web;

import com.metalr2.web.dto.request.FollowArtistRequest;
import io.restassured.authentication.FormAuthConfig;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;

public class RestAssuredRequestHandler {

  private final String requestUri;

  public RestAssuredRequestHandler(String requestUri) {
    this.requestUri = requestUri;
  }

  public ValidatableResponse doPost(ContentType accept, FollowArtistRequest request) {
    return given()
              .contentType(accept)
              .accept(accept)
              .body(request)
              .auth()
                .form("john.doe@example.com",
                      "john.doe",
                        new FormAuthConfig("/login", "username", "password"))
            .when()
              .post(requestUri)
            .then();
  }

  public ValidatableResponse doDelete(ContentType accept, FollowArtistRequest request) {
    return given()
              .contentType(accept)
              .accept(accept)
              .body(request)
              .auth()
                .form("john.doe@example.com",
                      "john.doe",
                        new FormAuthConfig("/login", "username", "password").withCsrfFieldName("_csrf"))
            .when()
              .delete(requestUri)
            .then();
  }
}
