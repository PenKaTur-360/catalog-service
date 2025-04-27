package es.penkatur.backend.catalogservice.catalog.infrastructure.events;

import es.penkatur.backend.catalogservice.catalog.domain.Catalog;
import es.penkatur.backend.catalogservice.catalog.domain.CatalogRepository;
import es.penkatur.backend.catalogservice.catalog.domain.events.CatalogUpdateEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.ObservesAsync;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.util.UUID;

@ApplicationScoped
public class CatalogUpdateEventListener {

    private final Logger logger;
    private final CatalogRepository catalogRepository;

    @Inject
    public CatalogUpdateEventListener(Logger logger, CatalogRepository catalogRepository) {
        this.logger = logger;
        this.catalogRepository = catalogRepository;
    }

    @Transactional
    public void onCatalogUpdate(@ObservesAsync CatalogUpdateEvent event) {
        if (event == null || event.catalogId() == null) {
            logger.warn("Event received without a valid catalog ID");
            return;
        }

        UUID catalogId = event.catalogId();
        logger.infof("Processing event for catalog: %s", catalogId);

        try {
            Catalog catalog = catalogRepository.findById(catalogId);
            if (catalog == null) {
                logger.warnf("Catalog not found for update: %s", catalogId);
                return;
            }

            // TODO: Realizar la actualización específica

            logger.infof("Catalog successfully updated: %s", catalogId);
        } catch (Exception e) {
            logger.errorf(e, "Error processing event for catalog: %s", catalogId);
        }
    }
}
