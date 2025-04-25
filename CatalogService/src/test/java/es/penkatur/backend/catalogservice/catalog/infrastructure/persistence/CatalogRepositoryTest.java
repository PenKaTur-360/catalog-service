package es.penkatur.backend.catalogservice.catalog.infrastructure.persistence;

import es.penkatur.backend.catalogservice.catalog.domain.Catalog;
import es.penkatur.backend.catalogservice.catalog.domain.CatalogRepository;
import es.penkatur.backend.catalogservice.catalog.infraestructure.exceptions.CatalogNotFoundException;
import es.penkatur.backend.catalogservice.catalog.infraestructure.persistence.CatalogPanacheRepository;
import es.penkatur.backend.sharedkernel.infrastructure.persistence.PostgresContainerTestProfile;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@Transactional
@TestProfile(PostgresContainerTestProfile.class)
class CatalogRepositoryTest {

    @Inject
    CatalogRepository repository;

    @Inject
    CatalogPanacheRepository panacheRepository;

    @BeforeEach
    void cleanDatabase() {
        panacheRepository.deleteAll();
    }

    @Test
    void shouldCreateAndFindCatalog() {
        var now = Instant.now();
        var catalog = Catalog.builder()
                .userId(UUID.randomUUID())
                .key("catalog-key")
                .name("catalog-name")
                .author("catalog-author")
                .version("1.0.0-SNAPSHOT")
                .url("catalog-url")
                .build();

        saveOrUpdate(catalog);
        assertNotNull(catalog.getId(), "Catalog ID should not be null");
        assertNull(catalog.getCreatedAt(), "Catalog creation date should not be null");
        assertNull(catalog.getUpdatedAt(), "Catalog update date should not be null");

        var result = find(catalog.getId());
        assertNotNull(result, "Catalog should be found by ID");
        assertEquals("catalog-key", result.getKey(), "Catalog key should match expected value");

        List<Catalog> result2 = findAll(now.minus(1, ChronoUnit.MINUTES));
        assertNotNull(result2, "Result list should not be null");
        assertEquals(1, result2.size(), "Result list should contain exactly one catalog");
        assertEquals("catalog-key", result2.getFirst().getKey(), "Catalog key in result list should match expected value");
    }

    @Test
    void shouldCreateAndNotFoundCatalog() {
        var catalog = Catalog.builder()
                .userId(UUID.randomUUID())
                .key("catalog-key")
                .name("catalog-name")
                .author("catalog-author")
                .version("1.0.0-SNAPSHOT")
                .url("catalog-url")
                .build();

        saveOrUpdate(catalog);
        List<Catalog> result = findAll(Instant.now().plus(1, ChronoUnit.MINUTES));
        assertNotNull(result, "Result list should not be null");
        assertEquals(0, result.size(), "Result list should be empty");
    }

    @Test
    void shouldCreateAndUpdateCatalog() throws InterruptedException {
        var catalog = Catalog.builder()
                .userId(UUID.randomUUID())
                .key("catalog-key")
                .name("catalog-name")
                .author("catalog-author")
                .version("1.0.0-SNAPSHOT")
                .url("catalog-url")
                .build();
        var id = catalog.getId();

        saveOrUpdate(catalog);
        Thread.sleep(Duration.of(1, ChronoUnit.SECONDS));

        var searchedCatalog = find(catalog.getId());
        searchedCatalog.changeName("catalog-name-changed");
        searchedCatalog.changeAuthor("catalog-author-changed");
        searchedCatalog.changeVersion("1.0.0");
        var updatedCatalog = saveOrUpdate(searchedCatalog);

        List<Catalog> result = findAll(Instant.now().minus(1, ChronoUnit.HOURS));
        assertNotNull(result, "Result list should not be null");
        assertEquals(1, result.size(), "Result list should contain exactly one catalog");

        assertEquals(id, updatedCatalog.getId(), "Catalog ID should match the expected ID");
        assertEquals("catalog-name-changed", updatedCatalog.getName(), "Catalog name should be updated to 'catalog-name-changed'");
        assertEquals("catalog-author-changed", updatedCatalog.getAuthor(), "Catalog author should be updated to 'catalog-author-changed'");
        assertEquals("1.0.0", updatedCatalog.getVersion(), "Catalog version should be updated to '1.0.0'");
    }

    @Test
    void shouldCreateAndRemoveCatalog() {
        var catalog = Catalog.builder()
                .userId(UUID.randomUUID())
                .key("catalog-key")
                .name("catalog-name")
                .author("catalog-author")
                .version("1.0.0-SNAPSHOT")
                .url("catalog-url")
                .build();

        saveOrUpdate(catalog);

        var result = find(catalog.getId());
        assertNotNull(result, "Catalog should be found by ID");
        repository.delete(catalog.getId());

        assertThrowsExactly(CatalogNotFoundException.class,
                () -> find(catalog.getId()),
                "Expected no catalog to be found by ID");
    }

    @Test
    void shouldCreateAndRemoveAnotherCatalog() {
        var catalog = Catalog.builder()
                .userId(UUID.randomUUID())
                .key("catalog-key")
                .name("catalog-name")
                .author("catalog-author")
                .version("1.0.0-SNAPSHOT")
                .url("catalog-url")
                .build();

        saveOrUpdate(catalog);

        var result = find(catalog.getId());
        assertNotNull(result, "Catalog should be found by ID");
        repository.delete(UUID.randomUUID());

        var result2 = find(catalog.getId());
        assertNotNull(result2, "Catalog should be found by ID");
    }

    private Catalog saveOrUpdate(Catalog catalog) {
        return repository.save(catalog);
    }

    private Catalog find(UUID id) {
        return repository.findById(id);
    }

    private List<Catalog> findAll(Instant timestamp) {
        return repository.findAllByUpdatedAtAfter(timestamp);
    }
}
