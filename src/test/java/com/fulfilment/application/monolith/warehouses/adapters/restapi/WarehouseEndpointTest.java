package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

@QuarkusTest
class WarehouseEndpointTest {

  @Test
  void listsAndReadsWarehousesByDatabaseIdAndBusinessUnitCode() {
    given()
        .when()
        .get("warehouse")
        .then()
        .statusCode(200)
        .body("businessUnitCode", hasItem("MWH.001"));

    given()
        .when()
        .get("warehouse/1")
        .then()
        .statusCode(200)
        .body("businessUnitCode", equalTo("MWH.001"));

    given()
        .when()
        .get("warehouse/MWH.001")
        .then()
        .statusCode(200)
        .body("location", equalTo("ZWOLLE-001"));
  }

  @Test
  void createsReplacesAndArchivesWarehouse() {
    String warehouse =
        """
        {"businessUnitCode":"MWH.COVERAGE.001","location":"AMSTERDAM-002","capacity":60,"stock":20}
        """;

    given()
        .contentType(ContentType.JSON)
        .body(warehouse)
        .when()
        .post("warehouse")
        .then()
        .statusCode(200)
        .body("businessUnitCode", equalTo("MWH.COVERAGE.001"))
        .body("stock", equalTo(20));

    given()
        .contentType(ContentType.JSON)
        .body(
            """
            {"location":"ZWOLLE-002","capacity":40,"stock":20}
            """)
        .when()
        .post("warehouse/MWH.COVERAGE.001/replacement")
        .then()
        .statusCode(200)
        .body("location", equalTo("ZWOLLE-002"))
        .body("capacity", equalTo(40));

    given()
        .when()
        .get("warehouse/MWH.COVERAGE.001")
        .then()
        .statusCode(200)
        .body("location", equalTo("ZWOLLE-002"));

    given().when().delete("warehouse/MWH.COVERAGE.001").then().statusCode(204);

    given()
        .when()
        .get("warehouse")
        .then()
        .statusCode(200)
        .body("businessUnitCode", not(hasItem("MWH.COVERAGE.001")));
  }

  @Test
  void returnsNotFoundForUnknownWarehouseAndReplacement() {
    given().when().get("warehouse/999999").then().statusCode(404);

    given()
        .contentType(ContentType.JSON)
        .body(
            """
            {"location":"AMSTERDAM-001","capacity":20,"stock":0}
            """)
        .when()
        .post("warehouse/MWH.UNKNOWN/replacement")
        .then()
        .statusCode(404);
  }

  @Test
  void rejectsWarehouseWithUnknownLocation() {
    given()
        .contentType(ContentType.JSON)
        .body(
            """
            {"businessUnitCode":"MWH.COVERAGE.INVALID","location":"UNKNOWN","capacity":20,"stock":0}
            """)
        .when()
        .post("warehouse")
        .then()
        .statusCode(400);
  }
}
