package es.penkatur.backend.tag.infraestructure.persistence;

import es.penkatur.backend.tag.infraestructure.entity.TagEntity;
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.smallrye.mutiny.Multi;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@ApplicationScoped
public class TagPanacheRepository implements PanacheRepositoryBase<TagEntity, UUID> {

    public Multi<TagEntity> findAllByUpdatedAtAfter(Instant updatedAt) {
        var tags = find("updatedAt > ?1", updatedAt.truncatedTo(ChronoUnit.SECONDS)).list();
        return tags.onItem().transformToMulti(list -> Multi.createFrom().iterable(list));
    }
}
