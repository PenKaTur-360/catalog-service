package es.penkatur.backend.catalogservice.tag.application;

import es.penkatur.backend.catalogservice.tag.domain.Tag;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface TagService {
    List<Tag> findAllTagsByUpdatedAtAfter(Instant updatedAt);
    Tag findTagById(UUID id);
    Tag updateTag(Tag tag);
}
