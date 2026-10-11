package com.fulfilment.application.monolith.stores;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

@QuarkusTest
class StoreEndpointTest {

  @Test
  void createsAndReadsStore() {
    int storeId =
        given()
            .contentType(ContentType.JSON)
            .body("{\"name\":\"Endpoint test store\",\"quantityProductsInStock\":5}")
            .when()
            .post("store")
            .then()
            .statusCode(201)
            .body("name", equalTo("Endpoint test store"))
            .body("quantityProductsInStock", equalTo(5))
            .extract()
            .path("id");

    given()
        .when()
        .get("store/" + storeId)
        .then()
        .statusCode(200)
        .body("name", equalTo("Endpoint test store"))
        .body("quantityProductsInStock", equalTo(5));

    given()
        .contentType(ContentType.JSON)
        .body("{\"name\":\"Updated endpoint test store\",\"quantityProductsInStock\":8}")
        .when()
        .put("store/" + storeId)
        .then()
        .statusCode(200)
        .body("name", equalTo("Updated endpoint test store"))
        .body("quantityProductsInStock", equalTo(8));

    given().when().delete("store/" + storeId).then().statusCode(204);
    given()
        .when()
        .get("store/" + storeId)
        .then()
        .statusCode(404)
        .body("errorType", equalTo("NOT_FOUND"));
  }

  @Test
  void validationErrorsReturnMeaningfulErrorType() {
    given()
        .contentType(ContentType.JSON)
        .body("{\"id\":999,\"name\":\"Invalid store\",\"quantityProductsInStock\":5}")
        .when()
        .post("store")
        .then()
        .statusCode(422)
        .body("errorType", equalTo("INVALID_REQUEST"))
        .body("code", equalTo(422))
        .body("error", equalTo("Id was invalidly set on request."));
  }

  @Test
  void returnsNotFoundWhenReadingUnknownStore() {
    given()
        .when()
        .get("store/999999")
        .then()
        .statusCode(404)
        .body("errorType", equalTo("NOT_FOUND"))
        .body("code", equalTo(404));
  }
}
