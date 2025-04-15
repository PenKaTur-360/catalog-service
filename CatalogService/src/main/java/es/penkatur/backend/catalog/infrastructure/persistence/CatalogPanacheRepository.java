package es.penkatur.backend.catalog.infrastructure.persistence;

import es.penkatur.backend.catalog.infrastructure.entity.CatalogEntity;
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class CatalogPanacheRepository implements PanacheRepositoryBase<CatalogEntity, UUID> {

    public Uni<List<CatalogEntity>> findAllByUpdatedAtAfter(Instant updatedAt) {
        return find("updatedAt > ?1", updatedAt.truncatedTo(ChronoUnit.SECONDS)).list();
    }
}
