package es.penkatur.backend.catalogservice.catalog.infraestructure.persistence;

import es.penkatur.backend.catalogservice.catalog.domain.Catalog;
import es.penkatur.backend.catalogservice.catalog.domain.CatalogRepository;
import es.penkatur.backend.catalogservice.catalog.domain.exceptions.CatalogOperationException;
import es.penkatur.backend.catalogservice.catalog.infraestructure.exceptions.CatalogNotFoundException;
import es.penkatur.backend.catalogservice.catalog.infraestructure.persistence.mapper.CatalogMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class CatalogRepositoryImpl implements CatalogRepository {

    private final Logger logger;
    private final CatalogPanacheRepository repository;

    @Inject
    public CatalogRepositoryImpl(Logger logger, CatalogPanacheRepository repository) {
        this.repository = repository;
        this.logger = logger;
    }

    @Override
    public List<Catalog> findAllByUpdatedAtAfter(Instant updatedAt) {
        try {
            return repository.findAllByUpdatedAtAfter(updatedAt)
                    .stream()
                    .map(CatalogMapper::toDomain)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.errorf(e, "Error searching catalogs: %s", updatedAt.toString());
            throw new CatalogOperationException("Error searching catalogs in database", e);
        }
    }

    @Override
    public Catalog findById(UUID id) {
        if (id == null) throw new IllegalArgumentException("ID cannot be null");

        try {
            var entity = repository.findById(id);
            if (entity == null) throw new CatalogNotFoundException(id);
            return CatalogMapper.toDomain(entity);
        } catch (CatalogNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.errorf(e, "Error searching catalog: %s", id);
            throw new CatalogOperationException("Error searching catalog in database", e);
        }
    }

    @Override
    public Catalog save(Catalog catalog) {
        if (catalog == null) throw new IllegalArgumentException("Catalog cannot be null");

        try {
            if (catalog.getCreatedAt() == null) {
                var catalogEntity = CatalogMapper.toEntity(catalog);
                repository.persist(catalogEntity);
                logger.debugf("Catalog created successfully: %s", catalogEntity.getId());
                return CatalogMapper.toDomain(catalogEntity);
            } else {
                var entity = repository.findById(catalog.getId());
                if (entity == null) throw new CatalogNotFoundException(catalog.getId());

                entity.setName(catalog.getName());
                entity.setAuthor(catalog.getAuthor());
                entity.setVersion(catalog.getVersion());
                entity.setUrl(catalog.getUrl());
                entity.setExternalUpdatedAt(catalog.getExternalUpdatedAt());

                repository.persist(entity);
                logger.debugf("Catalog updated successfully with ID: %s", catalog.getId());
                return CatalogMapper.toDomain(entity);
            }
        } catch (CatalogNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error to save catalog: %s", catalog.getId(), e);
            throw new CatalogOperationException("Error to save catalog in database", e);
        }
    }

    @Override
    public boolean delete(UUID id) {
        if (id == null) throw new IllegalArgumentException("ID cannot be null");

        try {
            var entity = repository.findById(id);
            if (entity == null) throw new CatalogNotFoundException(id);
            return repository.deleteById(id);
        } catch (CatalogNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.errorf(e, "Error deleting catalog: %s", id);
            throw new CatalogOperationException("Error deleting catalog in database", e);
        }
    }
}
