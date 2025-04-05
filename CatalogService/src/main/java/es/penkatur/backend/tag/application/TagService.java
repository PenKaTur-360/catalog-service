package es.penkatur.backend.tag.application;

import es.penkatur.backend.tag.domain.Tag;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

import java.time.Instant;
import java.util.UUID;

public interface TagService {
    Multi<Tag> findAllTagsByUpdatedAtAfter(Instant updatedAt);

    Uni<Tag> findTagById(UUID id);
}
