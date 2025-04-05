package es.penkatur.backend.tag.infrastructure.persistence;

import es.penkatur.backend.tag.infrastructure.entity.TagEntity;
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class TagPanacheRepository implements PanacheRepositoryBase<TagEntity, UUID> {

    public Uni<List<TagEntity>> findAllByUpdatedAtAfter(Instant updatedAt) {
        return find("updatedAt > ?1", updatedAt.truncatedTo(ChronoUnit.SECONDS)).list();
    }
}
