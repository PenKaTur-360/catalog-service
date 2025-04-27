package es.penkatur.backend.catalogservice.tag.infrastructure.persistence;

import es.penkatur.backend.catalogservice.tag.infrastructure.entity.TagEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class TagPanacheRepository implements PanacheRepositoryBase<TagEntity, UUID> {
    public List<TagEntity> findAllByUpdatedAtAfter(Instant updatedAt) {
        return find("updatedAt > ?1", updatedAt.truncatedTo(ChronoUnit.SECONDS)).list();
    }
}
