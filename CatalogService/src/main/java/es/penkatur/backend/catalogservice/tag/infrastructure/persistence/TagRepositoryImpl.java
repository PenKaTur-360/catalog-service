package es.penkatur.backend.catalogservice.tag.infrastructure.persistence;

import es.penkatur.backend.catalogservice.catalog.domain.exceptions.CatalogOperationException;
import es.penkatur.backend.catalogservice.tag.domain.Tag;
import es.penkatur.backend.catalogservice.tag.domain.TagRepository;
import es.penkatur.backend.catalogservice.tag.domain.exceptions.TagOperationException;
import es.penkatur.backend.catalogservice.tag.infrastructure.exceptions.TagNotFoundException;
import es.penkatur.backend.catalogservice.tag.infrastructure.persistence.mapper.TagMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class TagRepositoryImpl implements TagRepository {

    private final Logger logger;
    private final TagPanacheRepository repository;

    @Inject
    public TagRepositoryImpl(Logger logger, TagPanacheRepository repository) {
        this.repository = repository;
        this.logger = logger;
    }

    @Override
    public List<Tag> findAllByUpdatedAtAfter(Instant updatedAt) {
        try {
            return repository.findAllByUpdatedAtAfter(updatedAt)
                    .stream()
                    .map(TagMapper::toDomain)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.errorf(e, "Error searching tags: %s", updatedAt.toString());
            throw new TagOperationException("Error searching tags in database", e);
        }
    }

    @Override
    public Tag findById(UUID id) {
        if (id == null) throw new IllegalArgumentException("ID cannot be null");

        try {
            var entity = repository.findById(id);
            if (entity == null) throw new TagNotFoundException(id);
            return TagMapper.toDomain(entity);
        } catch (TagNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.errorf(e, "Error searching tag: %s", id);
            throw new CatalogOperationException("Error searching tag in database", e);
        }
    }

    @Override
    public Tag save(Tag tag) {
        if (tag == null) throw new IllegalArgumentException("Tag cannot be null");

        try {
            if (tag.getCreatedAt() == null) {
                var tagEntity = TagMapper.toEntity(tag);
                repository.persist(tagEntity);
                logger.debugf("Tag created successfully: %s", tagEntity.getId());
                return TagMapper.toDomain(tagEntity);
            } else {
                var entity = repository.findById(tag.getId());
                if (entity == null) throw new TagNotFoundException(tag.getId());

                entity.setColor(tag.getColor());
                repository.persist(entity);
                logger.debugf("Tag updated successfully with ID: %s", tag.getId());
                return TagMapper.toDomain(entity);
            }
        } catch (TagNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error to save tag: %s", tag.getId(), e);
            throw new TagOperationException("Error to save tag in database", e);
        }
    }
}
