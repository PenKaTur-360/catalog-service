package es.penkatur.backend.catalogservice.catalog.api;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

@QuarkusTest
class CatalogResourceTest {
    @Test
    void testAllCatalogsEndpoint() {
        given()
          .when().get("/catalog-service/v1/catalogs")
          .then()
             .statusCode(200);
    }

}