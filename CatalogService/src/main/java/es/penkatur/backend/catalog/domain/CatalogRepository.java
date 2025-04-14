package es.penkatur.backend.catalog.domain;

import io.smallrye.mutiny.Uni;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface CatalogRepository {
    Uni<List<Catalog>> findAllByUpdatedAtAfter(Instant updatedAt);

    Uni<Catalog> findById(UUID id);

    Uni<Catalog> save(Catalog catalog);

    Uni<Boolean> delete(UUID id);
}
