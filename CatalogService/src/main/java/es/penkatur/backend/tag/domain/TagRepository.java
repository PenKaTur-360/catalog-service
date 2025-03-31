package es.penkatur.backend.tag.domain;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

import java.time.Instant;
import java.util.UUID;

public interface TagRepository {
    Multi<Tag> findAll(Instant updatedAt);

    Uni<Tag> findById(UUID id);

    Uni<Tag> save(Tag tag);
}
