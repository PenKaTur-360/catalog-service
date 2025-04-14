package es.penkatur.backend.catalog.application;

import es.penkatur.backend.catalog.domain.Catalog;
import io.smallrye.mutiny.Uni;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface CatalogService {
    Uni<List<Catalog>> findAllCatalogsByUpdatedAtAfter(Instant updatedAt);

    Uni<Catalog> findCatalogById(UUID id);

    Uni<Catalog> createCatalog(Catalog catalog);

    Uni<Catalog> updateCatalog(Catalog catalog);

    Uni<Void> refreshCatalog(UUID id);

    Uni<Boolean> deleteCatalog(UUID id);
}
