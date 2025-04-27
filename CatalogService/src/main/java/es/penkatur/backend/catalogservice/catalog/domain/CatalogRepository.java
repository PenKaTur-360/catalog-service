package es.penkatur.backend.catalogservice.catalog.domain;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface CatalogRepository {
    List<Catalog> findAllByUpdatedAtAfter(Instant updatedAt);
    Catalog findById(UUID id);
    Catalog save(Catalog catalog);
    boolean delete(UUID id);
}
