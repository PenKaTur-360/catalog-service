package es.penkatur.backend.catalogservice.catalog.api;

import es.penkatur.backend.catalogservice.catalog.application.CatalogService;
import es.penkatur.backend.catalogservice.catalog.domain.Catalog;
import es.penkatur.backend.catalogservice.catalog.domain.CatalogRepository;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static io.restassured.RestAssured.given;

@QuarkusTest
class CatalogResourceTest {
    @Inject
    CatalogService service;

    @InjectMock
    CatalogRepository repository;

    @Test
    void testEmptyCatalogsEndpoint() {
        Mockito.when(service.findAllCatalogsByUpdatedAtAfter(Mockito.any(Instant.class)))
                .thenReturn(Collections.emptyList());

        given()
                .when().get("/catalog-service/v1/catalogs")
                .then()
                .statusCode(204);
    }

    @Test
    void testNonEmptyCatalogsEndpoint() {
        Catalog testCatalog = Catalog.builder()
                .userId(UUID.randomUUID())
                .key("test-key")
                .url("http://test-url.com")
                .name("Test Catalog")
                .build();

        Mockito.when(service.findAllCatalogsByUpdatedAtAfter(Mockito.any(Instant.class)))
                .thenReturn(List.of(testCatalog));

        given()
                .when().get("/catalog-service/v1/catalogs")
                .then()
                .statusCode(200);
    }
}