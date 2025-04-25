package es.penkatur.backend.catalogservice.catalog.infrastructure.persistence.mapper;

import es.penkatur.backend.catalogservice.catalog.domain.Catalog;
import es.penkatur.backend.catalogservice.catalog.infraestructure.entity.CatalogEntity;
import es.penkatur.backend.catalogservice.catalog.infraestructure.persistence.mapper.CatalogMapper;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
class CatalogMapperTest {

    @Test
    void shouldMapDomainToEntity() {
        var catalog = Catalog.builder()
                .userId(UUID.randomUUID())
                .key("catalog-key")
                .name("catalog-name")
                .author("catalog-author")
                .version("1.0.0-SNAPSHOT")
                .url("catalog-url")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        checkCatalog(catalog, CatalogMapper.toEntity(catalog));
    }

    @Test
    void shouldMapEntityToDomain() {
        var entity = new CatalogEntity();
        entity.setId(UUID.randomUUID());
        entity.setUserId(UUID.randomUUID());
        entity.setKey("entity-catalog-key");
        entity.setName("entity-catalog-name");
        entity.setAuthor("entity-catalog-author");
        entity.setVersion("1.0.0-SNAPSHOT");
        entity.setUrl("entity-catalog-url");
        entity.setCreatedAt(Instant.now().truncatedTo(ChronoUnit.SECONDS));
        entity.setUpdatedAt(Instant.now().truncatedTo(ChronoUnit.SECONDS));
        checkCatalog(CatalogMapper.toDomain(entity), entity);
    }

    private void checkCatalog(Catalog catalog, CatalogEntity entity) {
        assertEquals(catalog.getId(), entity.getId(), "The IDs do not match!");
        assertEquals(catalog.getUserId(), entity.getUserId(), "The user do not match!");
        assertEquals(catalog.getKey(), entity.getKey(), "The key do not match!");
        assertEquals(catalog.getName(), entity.getName(), "The name do not match!");
        assertEquals(catalog.getAuthor(), entity.getAuthor(), "The author do not match!");
        assertEquals(catalog.getVersion(), entity.getVersion(), "The version do not match!");
        assertEquals(catalog.getUrl(), entity.getUrl(), "The url do not match!");
        assertEquals(catalog.getCreatedAt(), entity.getCreatedAt(), "The creation dates do not match!");
        assertEquals(catalog.getUpdatedAt(), entity.getUpdatedAt(), "The update dates do not match!");
    }
}
