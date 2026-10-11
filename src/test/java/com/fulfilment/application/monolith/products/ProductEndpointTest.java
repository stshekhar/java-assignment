package com.fulfilment.application.monolith.products;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.IsNot.not;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class ProductEndpointTest {

  @Test
  public void testCrudProduct() {
    final String path = "product";

    // List all, should have all 3 products the database has initially:
    given()
        .when()
        .get(path)
        .then()
        .statusCode(200)
        .body(containsString("TONSTAD"), containsString("KALLAX"), containsString("BESTÅ"));

    // Delete the TONSTAD:
    given().when().delete(path + "/1").then().statusCode(204);

    // List all, TONSTAD should be missing now:
    given()
        .when()
        .get(path)
        .then()
        .statusCode(200)
        .body(not(containsString("TONSTAD")), containsString("KALLAX"), containsString("BESTÅ"));
  }

  @Test
  public void validationErrorsReturnMeaningfulErrorType() {
    given()
        .contentType(ContentType.JSON)
        .body("{\"id\":999,\"name\":\"Invalid product\",\"stock\":0}")
        .when()
        .post("product")
        .then()
        .statusCode(422)
        .body("errorType", equalTo("INVALID_REQUEST"))
        .body("code", equalTo(422))
        .body("error", equalTo("Id was invalidly set on request."));
  }

  @Test
  public void createsReadsUpdatesAndDeletesProduct() {
    String product =
        """
        {"name":"Unit test product","description":"Created by endpoint test","price":12.50,"stock":3}
        """;

    int productId =
        given()
            .contentType(ContentType.JSON)
            .body(product)
            .when()
            .post("product")
            .then()
            .statusCode(201)
            .body("name", equalTo("Unit test product"))
            .extract()
            .path("id");

    given()
        .when()
        .get("product/" + productId)
        .then()
        .statusCode(200)
        .body("description", equalTo("Created by endpoint test"));

    given()
        .contentType(ContentType.JSON)
        .body(
            """
            {"name":"Updated test product","description":"Updated","price":15.00,"stock":4}
            """)
        .when()
        .put("product/" + productId)
        .then()
        .statusCode(200)
        .body("name", equalTo("Updated test product"))
        .body("stock", equalTo(4));

    given().when().delete("product/" + productId).then().statusCode(204);

    given()
        .when()
        .get("product/" + productId)
        .then()
        .statusCode(404)
        .body("errorType", equalTo("NOT_FOUND"))
        .body("code", equalTo(404));
  }

  @Test
  public void rejectsUpdateWithoutProductName() {
    given()
        .contentType(ContentType.JSON)
        .body("{\"description\":\"Missing name\",\"stock\":1}")
        .when()
        .put("product/1")
        .then()
        .statusCode(422)
        .body("errorType", equalTo("INVALID_REQUEST"))
        .body("error", equalTo("Product Name was not set on request."));
  }

  @Test
  public void returnsNotFoundWhenReadingUnknownProduct() {
    given()
        .when()
        .get("product/999999")
        .then()
        .statusCode(404)
        .body("errorType", equalTo("NOT_FOUND"));
  }
}
