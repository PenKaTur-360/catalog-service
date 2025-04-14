package es.penkatur.backend.catalog.application;

import es.penkatur.backend.catalog.domain.Catalog;
import es.penkatur.backend.catalog.domain.CatalogRepository;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class CatalogServiceTest {

    @Inject
    CatalogService service;

    @InjectMock
    CatalogRepository repository;

    @Test
    void shouldFoundCatalog() {
        var catalog = getBuilder().build();
        Mockito.when(repository.findById(catalog.getId())).thenReturn(Uni.createFrom().item(catalog));

        Catalog result = service.findCatalogById(catalog.getId()).await().indefinitely();
        check(catalog, result);
    }

    @Test
    void shouldNotFoundCatalog() {
        var catalog = getBuilder().build();
        Mockito.when(repository.findById(catalog.getId())).thenReturn(Uni.createFrom().item(catalog));

        var result = service.findCatalogById(UUID.randomUUID()).await().indefinitely();
        assertNull(result, "Expected result to be null since the catalog should not be found.");
    }

    @Test
    void shouldFoundAllCatalogs() {
        var catalog1 = getBuilder().key("catalog1").build();
        var catalog2 = getBuilder().key("catalog2").build();
        var now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        Mockito.when(repository.findAllByUpdatedAtAfter(now)).thenReturn(Multi.createFrom().items(catalog1, catalog2).collect().asList());

        List<Catalog> result = service.findAllCatalogsByUpdatedAtAfter(now).await().indefinitely();
        assertNotNull(result, "Expected result to be non-null, but it was null");
        assertEquals(2, result.size(), "Expected the result list to contain exactly 2 catalogs.");

        List<Catalog> result2 = service.findAllCatalogsByUpdatedAtAfter(now.minus(1, ChronoUnit.MINUTES)).await().indefinitely();
        assertNotNull(result2, "Expected result2 to be non-null, but it was null");
        assertEquals(0, result2.size(), "Expected the result list to be empty since no tags should match the criteria.");
    }

    @Test
    void shouldCreateCatalog() {
        var catalog = getBuilder().build();
        Mockito.when(repository.save(catalog)).thenReturn(Uni.createFrom().item(catalog));

        Uni<Catalog> result = service.createCatalog(catalog);
        assertNotNull(result, "Expected result to be non-null, but it was null");
        check(catalog, result.await().indefinitely());
    }

    @Test
    void shouldUpdateCatalog() {
        var catalog = getBuilder().build();
        Mockito.when(repository.save(catalog)).thenReturn(Uni.createFrom().item(catalog));

        Uni<Catalog> result = service.updateCatalog(catalog);
        assertNotNull(result, "Expected result to be non-null, but it was null");
        check(catalog, result.await().indefinitely());
    }

    @Test
    void shouldRefreshCatalog() {
        // TODO
        assertNull(service.refreshCatalog(UUID.randomUUID()));
    }

    @Test
    void shouldDeleteCatalog() {
        var id = UUID.randomUUID();
        Mockito.when(repository.delete(id)).thenReturn(Uni.createFrom().item(Boolean.TRUE));

        Uni<Boolean> result = service.deleteCatalog(id);
        assertNotNull(result, "Expected result to be non-null, but it was null");
        assertTrue(result.await().indefinitely(), "Expected the result to be true after awaiting the asynchronous operation");
    }

    @Test
    void shouldNotDeleteCatalog() {
        var id = UUID.randomUUID();
        Mockito.when(repository.delete(id)).thenReturn(Uni.createFrom().item(Boolean.FALSE));

        Uni<Boolean> result = service.deleteCatalog(id);
        assertNotNull(result, "Expected result to be non-null, but it was null");
        assertFalse(result.await().indefinitely(), "Expected the result to be false after awaiting the asynchronous operation");
    }

    private void check(Catalog expected, Catalog actual) {
        assertNotNull(actual, "Expected result to be non-null, but it was null");
        assertEquals(expected.getId(), actual.getId(), "Expected the ID of the catalog to match.");
        assertEquals(expected.getUserId(), actual.getUserId(), "Expected the user of the catalog to match.");
        assertEquals(expected.getKey(), actual.getKey(), "Expected the key of the catalog to match.");
        assertEquals(expected.getName(), actual.getName(), "Expected the name of the catalog to match.");
        assertEquals(expected.getAuthor(), actual.getAuthor(), "Expected the author of the catalog to match.");
        assertEquals(expected.getVersion(), actual.getVersion(), "Expected the version of the catalog to match.");
        assertEquals(expected.getUrl(), actual.getUrl(), "Expected the url of the catalog to match.");
        assertEquals(expected.getCreatedAt(), actual.getCreatedAt(), "Expected the creation dates of the catalog to match.");
        assertEquals(expected.getUpdatedAt(), actual.getUpdatedAt(), "Expected the update dates of the catalog to match.");
    }

    private Catalog.CatalogBuilder getBuilder() {
        return Catalog.builder()
                .userId(UUID.randomUUID())
                .key("catalog-key")
                .name("catalog-name")
                .author("catalog-author")
                .version("1.0.0-SNAPSHOT")
                .url("catalog-url");
    }
}
