package es.penkatur.backend.catalogservice.catalog.api;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
class CatalogServiceTest {
    @Test
    void testHelloEndpoint() {
        given()
          .when().get("/catalog-service/v1/catalogs")
          .then()
             .statusCode(200)
             .body(is("Hello from Quarkus REST"));
    }

}