package es.penkatur.backend.catalogservice.catalog.application;

import es.penkatur.backend.catalogservice.catalog.domain.Catalog;
import es.penkatur.backend.catalogservice.catalog.domain.CatalogRepository;
import es.penkatur.backend.catalogservice.catalog.domain.events.CatalogEventPublisher;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.control.ActivateRequestContext;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class CatalogServiceImpl implements CatalogService {

    private final Logger logger;
    private final CatalogRepository repository;
    private final CatalogEventPublisher catalogPublisherEvent;

    @Inject
    public CatalogServiceImpl(Logger logger, CatalogRepository repository, CatalogEventPublisher catalogPublisherEvent) {
        this.catalogPublisherEvent = catalogPublisherEvent;
        this.repository = repository;
        this.logger = logger;
    }

    @Override
    @ActivateRequestContext
    public List<Catalog> findAllCatalogsByUpdatedAtAfter(Instant updatedAt) {
        var timestamp = Optional.ofNullable(updatedAt).orElse(Instant.parse("0001-01-01T00:00:00Z"));
        logger.debugf("Find a list of catalogs from %s", timestamp.toString());
        return repository.findAllByUpdatedAtAfter(timestamp);
    }

    @Override
    @ActivateRequestContext
    public Catalog findCatalogById(UUID id) {
        logger.debugf("Searching for catalog with ID: %s", id);
        try {
            return repository.findById(id);
        } catch (Exception e) {
            logger.warnf("Catalog with ID %s not found", id);
            return null;
        }
    }

    @Override
    @Transactional
    public Catalog createCatalog(Catalog catalog) {
        // TODO: validar la url del catalogo
        logger.debugf("Creating a new catalog with key: %s", catalog.getKey());
        var result = repository.save(catalog);
        refreshCatalog(catalog.getId());
        return result;
    }

    @Override
    @Transactional
    public Catalog updateCatalog(Catalog catalog) {
        // TODO: validar la url del catalogo
        logger.debugf("Updating a catalog with key: %s", catalog.getKey());
        return repository.save(catalog);
    }

    @Override
    public void refreshCatalog(UUID id) {
        logger.debugf("Set catalog to refresh with ID: %s", id);
        catalogPublisherEvent.publishCatalogUpdateEvent(id);
    }

    @Override
    @Transactional
    public boolean deleteCatalog(UUID id) {
        logger.debugf("Deleting catalog with ID: %s", id);
        return repository.delete(id);
    }
}
