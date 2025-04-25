package es.penkatur.backend.catalogservice.catalog.infraestructure.events;

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

    @Inject
    public CatalogUpdateEventListener(Logger logger) {
        this.logger = logger;
    }

    @Transactional
    public void onCatalogUpdate(@ObservesAsync CatalogUpdateEvent event) {
        UUID catalogId = event.catalogId();
        logger.debugf("Refreshing catalog with ID: %s", catalogId);
    }
}
