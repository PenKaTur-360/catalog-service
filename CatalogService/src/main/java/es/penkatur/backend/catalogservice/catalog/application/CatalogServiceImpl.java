package es.penkatur.backend.catalogservice.catalog.application;

import es.penkatur.backend.catalogservice.catalog.domain.Catalog;
import es.penkatur.backend.catalogservice.catalog.domain.CatalogRepository;
import es.penkatur.backend.catalogservice.catalog.domain.events.CatalogEventPublisher;
import es.penkatur.backend.catalogservice.catalog.domain.exceptions.CatalogOperationException;
import es.penkatur.backend.catalogservice.catalog.domain.exceptions.InvalidCatalogException;
import es.penkatur.backend.catalogservice.catalog.infrastructure.exceptions.CatalogNotFoundException;
import es.penkatur.backend.sharedkernel.infraestructure.persistence.ContextualOperations;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class CatalogServiceImpl implements CatalogService {

    private final Logger logger;
    private final CatalogRepository repository;
    private final ContextualOperations contextualOperations;
    private final CatalogEventPublisher catalogPublisherEvent;

    @Inject
    public CatalogServiceImpl(Logger logger, CatalogRepository repository, CatalogEventPublisher catalogPublisherEvent,
                              ContextualOperations contextualOperations) {
        this.catalogPublisherEvent = catalogPublisherEvent;
        this.contextualOperations = contextualOperations;
        this.repository = repository;
        this.logger = logger;
    }

    @Override
    public List<Catalog> findAllCatalogsByUpdatedAtAfter(Instant updatedAt) {
        var timestamp = Optional.ofNullable(updatedAt).orElse(Instant.parse("0001-01-01T00:00:00Z"));
        logger.debugf("Find a list of catalogs from %s", timestamp.toString());
        return contextualOperations.executeInSession(() -> repository.findAllByUpdatedAtAfter(timestamp));
    }

    @Override
    public Catalog findCatalogById(UUID id) {
        if (id == null)
            throw new IllegalArgumentException("ID cannot be null");

        logger.debugf("Searching for catalog with ID: %s", id);
        try {
            return contextualOperations.executeInSession(() -> repository.findById(id));
        } catch (CatalogNotFoundException e) {
            logger.infof("Catalog with ID %s not found", id);
            throw e;
        } catch (Exception e) {
            logger.errorf("Error searching catalog with id %s", id, e);
            throw new CatalogOperationException("Error searching catalog", e);
        }
    }

    @Override
    public Catalog createCatalog(Catalog catalog) {
        try {
            logger.debugf("Creating a new catalog with key: %s", catalog.getKey());
            return saveAndRefresh(catalog);
        } catch (Exception e) {
            logger.error("Error creating catalog", e);
            throw new CatalogOperationException("Error creating catalog", e);
        }
    }

    @Override
    public Catalog updateCatalog(Catalog catalog) {
        try {
            logger.debugf("Updating a catalog with key: %s", catalog.getKey());
            return saveAndRefresh(catalog);
        } catch (Exception e) {
            logger.error("Error updating catalog", e);
            throw new CatalogOperationException("Error updating catalog", e);
        }
    }

    private Catalog saveAndRefresh(Catalog catalog) {
        try {
            catalog.validate();
            Catalog result = contextualOperations.executeInTransaction(() -> repository.save(catalog));

            try {
                refreshCatalogSafely(catalog.getId());
            } catch (Exception e) {
                logger.warn("Could not schedule catalog update", e);
            }

            return result;
        } catch (InvalidCatalogException e) {
            logger.error("Invalid catalog data", e);
            throw e;
        }
    }

    @Override
    public void refreshCatalog(UUID id) {
        if (id == null)
            throw new IllegalArgumentException("ID cannot be null");

        logger.debugf("Set catalog to refresh with ID: %s", id);
        refreshCatalogSafely(id);
    }

    @Override
    public boolean deleteCatalog(UUID id) {
        if (id == null)
            throw new IllegalArgumentException("ID cannot be null");

        logger.debugf("Deleting catalog with ID: %s", id);
        try {
            return contextualOperations.executeInTransaction(() -> repository.delete(id));
        } catch (CatalogNotFoundException e) {
            logger.infof("Catalog with ID %s not found", id);
            throw e;
        } catch (Exception e) {
            logger.errorf("Error deleting catalog with id %s", id, e);
            throw new CatalogOperationException("Error deleting catalog", e);
        }
    }

    private void refreshCatalogSafely(UUID id) {
        try {
            catalogPublisherEvent.publishCatalogUpdateEvent(id);
        } catch (Exception e) {
            logger.warn("Error publishing update event", e);
        }
    }
}
