package com.metalr2.web;

import com.metalr2.config.constants.Endpoints;
import io.restassured.RestAssured;
import io.restassured.authentication.FormAuthConfig;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;

public class RestAssuredRequestHandler<T> {

  private final String requestUri;
  private final String username;
  private final String password;

  public RestAssuredRequestHandler(String requestUri, String username, String password) {
    this.requestUri = requestUri;
    this.username   = username;
    this.password   = password;
    RestAssured.defaultParser = Parser.JSON;
  }

  public ValidatableResponse doPost(ContentType accept, T request) {
    return given()
              .contentType(accept)
              .accept(accept)
              .body(request)
              .auth()
                .form(username,
                      password,
                      new FormAuthConfig(Endpoints.Guest.LOGIN,"username","password"))
            .when()
              .post(requestUri)
            .then();
  }

  public ValidatableResponse doDelete(ContentType accept, T request) {
    return given()
              .contentType(accept)
              .accept(accept)
              .body(request)
              .auth()
                .form(username,
                      password,
                      new FormAuthConfig(Endpoints.Guest.LOGIN,"username","password"))
            .when()
              .delete(requestUri)
            .then();
  }
}
