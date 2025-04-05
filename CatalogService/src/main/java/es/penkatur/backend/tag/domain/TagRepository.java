package es.penkatur.backend.tag.domain;

import io.smallrye.mutiny.Uni;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface TagRepository {
    Uni<List<Tag>> findAllByUpdatedAtAfter(Instant updatedAt);

    Uni<Tag> findById(UUID id);

    Uni<Tag> save(Tag tag);
}
