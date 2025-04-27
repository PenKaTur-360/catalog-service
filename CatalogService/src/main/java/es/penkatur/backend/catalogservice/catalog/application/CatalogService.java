package es.penkatur.backend.catalogservice.catalog.application;

import es.penkatur.backend.catalogservice.catalog.domain.Catalog;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface CatalogService {
    List<Catalog> findAllCatalogsByUpdatedAtAfter(Instant updatedAt);
    Catalog findCatalogById(UUID id);
    void refreshCatalog(UUID id);
    Catalog createCatalog(Catalog catalog);
    Catalog updateCatalog(Catalog catalog);
    boolean deleteCatalog(UUID id);
}
