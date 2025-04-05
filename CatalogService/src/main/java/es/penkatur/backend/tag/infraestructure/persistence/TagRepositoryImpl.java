package es.penkatur.backend.tag.infraestructure.persistence;

import es.penkatur.backend.tag.domain.Tag;
import es.penkatur.backend.tag.domain.TagRepository;
import es.penkatur.backend.tag.infraestructure.persistence.mapper.TagMapper;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.Instant;
import java.util.UUID;

@ApplicationScoped
public class TagRepositoryImpl implements TagRepository {

    private final TagPanacheRepository repository;

    @Inject
    public TagRepositoryImpl(TagPanacheRepository repository) {
        this.repository = repository;
    }

    @Override
    public Multi<Tag> findAllByUpdatedAtAfter(Instant updatedAt) {
        return repository.findAllByUpdatedAtAfter(updatedAt).map(TagMapper::toDomain);
    }

    @Override
    public Uni<Tag> findById(UUID id) {
        return repository.findById(id).map(TagMapper::toDomain);
    }

    @Override
    public Uni<Tag> save(Tag tag) {
        var swCreate = tag.getCreatedAt() == null;
        tag.changeUpdatedAt(Instant.now());
        var tagEntity = TagMapper.toEntity(tag);

        if (swCreate) return repository.persist(tagEntity).map(TagMapper::toDomain);
        else return repository.update(
                        "color = ?1, name = ?2, updatedAt = ?3 where id = ?4",
                        tagEntity.getColor(), tagEntity.getName(), tagEntity.getUpdatedAt(), tagEntity.getId()
                )
                .map(updated -> updated > 0 ? tag : null);
    }
}
