package es.penkatur.backend.catalog.infrastructure.persistence;

import es.penkatur.backend.catalog.domain.Catalog;
import es.penkatur.backend.catalog.domain.CatalogRepository;
import es.penkatur.backend.sharedkernel.infrastructure.persistence.PostgresContainerTestProfile;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import jakarta.inject.Inject;
import org.hibernate.reactive.mutiny.Mutiny;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestProfile(PostgresContainerTestProfile.class)
class CatalogRepositoryTest {

    @Inject
    CatalogRepository repository;

    @Inject
    Mutiny.SessionFactory sessionFactory;

    @Inject
    CatalogPanacheRepository panacheRepository;

    @BeforeEach
    void cleanDatabase() {
        sessionFactory.withTransaction(session ->
                panacheRepository.deleteAll()
        ).await().indefinitely();
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
        assertNotNull(catalog.getCreatedAt(), "Catalog creation date should not be null");
        assertNotNull(catalog.getUpdatedAt(), "Catalog update date should not be null");

        var result = sessionFactory.withTransaction(session ->
                repository.findById(catalog.getId())
        ).await().indefinitely();
        assertNotNull(result, "Catalog should be found by ID");
        assertEquals("catalog-key", result.getKey(), "Catalog key should match expected value");

        List<Catalog> result2 = sessionFactory.withTransaction(session ->
                repository.findAllByUpdatedAtAfter(now.minus(1, ChronoUnit.MINUTES))
        ).await().indefinitely();
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
        List<Catalog> result = sessionFactory.withTransaction(session ->
                repository.findAllByUpdatedAtAfter(Instant.now().plus(1, ChronoUnit.MINUTES))
        ).await().indefinitely();
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

        catalog.changeName("catalog-name-changed");
        catalog.changeAuthor("catalog-author-changed");
        catalog.changeVersion("1.0.0");
        saveOrUpdate(catalog);

        List<Catalog> result = sessionFactory.withTransaction(session ->
                repository.findAllByUpdatedAtAfter(Instant.now().minus(1, ChronoUnit.HOURS))
        ).await().indefinitely();
        assertNotNull(result, "Result list should not be null");
        assertEquals(1, result.size(), "Result list should contain exactly one catalog");

        assertEquals(id, catalog.getId(), "Catalog ID should match the expected ID");
        assertNotEquals(catalog.getCreatedAt(), catalog.getUpdatedAt(), "Updated timestamp should differ from created timestamp after update");
        assertEquals("catalog-name-changed", catalog.getName(), "Catalog name should be updated to 'catalog-name-changed'");
        assertEquals("catalog-author-changed", catalog.getAuthor(), "Catalog author should be updated to 'catalog-author-changed'");
        assertEquals("1.0.0", catalog.getVersion(), "Catalog version should be updated to '1.0.0'");
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

        var result = sessionFactory.withTransaction(session ->
                repository.findById(catalog.getId())
        ).await().indefinitely();
        assertNotNull(result, "Catalog should be found by ID");
        sessionFactory.withTransaction(session ->
                repository.delete(catalog.getId())
        ).await().indefinitely();

        var result2 = sessionFactory.withTransaction(session ->
                repository.findById(catalog.getId())
        ).await().indefinitely();
        assertNull(result2, "Expected no catalog to be found by ID");
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

        var result = sessionFactory.withTransaction(session ->
                repository.findById(catalog.getId())
        ).await().indefinitely();
        assertNotNull(result, "Catalog should be found by ID");
        sessionFactory.withTransaction(session ->
                repository.delete(UUID.randomUUID())
        ).await().indefinitely();

        var result2 = sessionFactory.withTransaction(session ->
                repository.findById(catalog.getId())
        ).await().indefinitely();
        assertNotNull(result2, "Catalog should be found by ID");
    }

    private void saveOrUpdate(Catalog catalog) {
        sessionFactory.withTransaction(session ->
                repository.save(catalog)
        ).await().indefinitely();
    }
}
