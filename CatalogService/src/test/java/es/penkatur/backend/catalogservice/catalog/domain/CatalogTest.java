package es.penkatur.backend.catalogservice.catalog.domain;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class CatalogTest {
    @Test
    void shouldCreateCatalog() {
        var catalog = Catalog.builder()
                .userId(UUID.randomUUID())
                .key("catalog-key")
                .url("catalog-url")
                .build();

        assertNotNull(catalog, "The catalog should not be null");
        assertNotNull(catalog.getId(), "The ID should not be null");
        assertNotNull(catalog.getUserId(), "The user should not be null");
        assertNotNull(catalog.getKey(), "The key should not be null");
        assertNull(catalog.getName(), "The name should be null");
        assertNull(catalog.getAuthor(), "The author should be null");
        assertNull(catalog.getVersion(), "The version should be null");
        assertNotNull(catalog.getUrl(), "The url should not be null");
        assertNull(catalog.getCreatedAt(), "The creation time should be null");
        assertNull(catalog.getUpdatedAt(), "The update time should be null");
        assertNull(catalog.getExternalUpdatedAt(), "The external update time should be null");
    }

    @Test
    void shouldCreateCompleteCatalog() {
        Instant now = Instant.now();
        var catalog = Catalog.builder()
                .userId(UUID.randomUUID())
                .key("catalog-key")
                .name("catalog-name")
                .author("catalog-author")
                .version("1.0.0-SNAPSHOT")
                .url("catalog-url")
                .createdAt(now)
                .updatedAt(now)
                .externalUpdatedAt(now)
                .build();

        assertNotNull(catalog, "The catalog should not be null");
        assertNotNull(catalog.getId(), "The ID should not be null");
        assertNotNull(catalog.getUserId(), "The user should not be null");
        assertNotNull(catalog.getKey(), "The key should not be null");
        assertNotNull(catalog.getName(), "The name should not be null");
        assertNotNull(catalog.getAuthor(), "The author should not be null");
        assertNotNull(catalog.getVersion(), "The version should not be null");
        assertNotNull(catalog.getUrl(), "The url should not be null");
        assertNotNull(catalog.getCreatedAt(), "The creation time should not be null");
        assertEquals(now.truncatedTo(ChronoUnit.SECONDS), catalog.getCreatedAt(), "The creation time should match the expected value");
        assertNotNull(catalog.getUpdatedAt(), "The update time should not be null");
        assertEquals(now.truncatedTo(ChronoUnit.SECONDS), catalog.getUpdatedAt(), "The update time should match the expected value");
        assertNotNull(catalog.getExternalUpdatedAt(), "The external update time should not be null");
        assertEquals(now.truncatedTo(ChronoUnit.SECONDS), catalog.getExternalUpdatedAt(), "The external update time should match the expected value");
    }

    @Test
    void shouldThrowsNullPointerExceptionForUserId() {
        assertThrowsExactly(NullPointerException.class,
                () -> Catalog.builder().build(),
                "userId is marked non-null but is null");
    }

    @Test
    void shouldThrowsNullPointerExceptionForKey() {
        assertThrowsExactly(NullPointerException.class,
                () -> Catalog.builder()
                        .userId(UUID.randomUUID())
                        .build(),
                "key is marked non-null but is null");
    }

    @Test
    void shouldThrowsNullPointerExceptionForUrl() {
        assertThrowsExactly(NullPointerException.class,
                () -> Catalog.builder()
                        .userId(UUID.randomUUID())
                        .key("catalog-key")
                        .name("catalog-name")
                        .author("catalog-author")
                        .version("1.0.0-SNAPSHOT")
                        .build(),
                "url is marked non-null but is null");
    }

    @Test
    void shouldChangeName() {
        var catalog = Catalog.builder()
                .userId(UUID.randomUUID())
                .key("catalog-key")
                .name("catalog-name")
                .author("catalog-author")
                .version("1.0.0-SNAPSHOT")
                .url("catalog-url")
                .build();

        catalog.changeName("catalog-name-edit");
        assertEquals("catalog-name-edit", catalog.getName(),
                "Expected the catalog name to be 'catalog-name-edit' after changing it.");
    }

    @Test
    void shouldChangeAuthor() {
        var catalog = Catalog.builder()
                .userId(UUID.randomUUID())
                .key("catalog-key")
                .name("catalog-name")
                .author("catalog-author")
                .version("1.0.0-SNAPSHOT")
                .url("catalog-url")
                .build();

        catalog.changeAuthor("catalog-author-edit");
        assertEquals("catalog-author-edit", catalog.getAuthor(),
                "Expected the catalog author to be 'catalog-author-edit' after changing it.");
    }

    @Test
    void shouldChangeVersion() {
        var catalog = Catalog.builder()
                .userId(UUID.randomUUID())
                .key("catalog-key")
                .name("catalog-name")
                .author("catalog-author")
                .version("1.0.0-SNAPSHOT")
                .url("catalog-url")
                .build();

        catalog.changeVersion("1.0.0");
        assertEquals("1.0.0", catalog.getVersion(),
                "Expected the catalog version to be '1.0.0' after changing it.");
    }

    @Test
    void shouldNotChangeVersion() {
        var catalog = Catalog.builder()
                .userId(UUID.randomUUID())
                .key("catalog-key")
                .name("catalog-name")
                .author("catalog-author")
                .version("1.0.0")
                .url("catalog-url")
                .build();

        assertThrows(IllegalArgumentException.class, () -> catalog.changeVersion("1.0.0-SNAPSHOT"),
                "The entered version must not be lower than the current version.");
    }

    @Test
    void shouldChangeExternalUpdate() {
        var now = Instant.now();
        var catalog = Catalog.builder()
                .userId(UUID.randomUUID())
                .key("catalog-key")
                .name("catalog-name")
                .author("catalog-author")
                .version("1.0.0-SNAPSHOT")
                .url("catalog-url")
                .externalUpdatedAt(now.minus(5, ChronoUnit.HOURS))
                .build();

        assertEquals(now.truncatedTo(ChronoUnit.SECONDS).minus(5, ChronoUnit.HOURS), catalog.getExternalUpdatedAt(),
                "The external update time should match the expected value");

        catalog.changeExternalUpdatedAt(now);
        assertEquals(now.truncatedTo(ChronoUnit.SECONDS), catalog.getExternalUpdatedAt(),
                "The external update time should match the expected value");
    }
}
