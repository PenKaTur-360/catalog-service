package es.penkatur.backend.catalogservice.catalog.infraestructure.persistence;

import es.penkatur.backend.catalogservice.catalog.infraestructure.entity.CatalogEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class CatalogPanacheRepository implements PanacheRepositoryBase<CatalogEntity, UUID> {

    public List<CatalogEntity> findAllByUpdatedAtAfter(Instant updatedAt) {
        return find("updatedAt > ?1", updatedAt.truncatedTo(ChronoUnit.SECONDS)).list();
    }
}
