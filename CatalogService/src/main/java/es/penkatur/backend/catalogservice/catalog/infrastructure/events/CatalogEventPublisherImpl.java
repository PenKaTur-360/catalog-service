package es.penkatur.backend.catalogservice.catalog.infrastructure.events;

import es.penkatur.backend.catalogservice.catalog.domain.events.CatalogEventPublisher;
import es.penkatur.backend.catalogservice.catalog.domain.events.CatalogUpdateEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.util.UUID;
import java.util.concurrent.CompletionStage;

@ApplicationScoped
public class CatalogEventPublisherImpl implements CatalogEventPublisher {

    private final Logger logger;
    private final Event<CatalogUpdateEvent> catalogUpdateEvent;

    @Inject
    public CatalogEventPublisherImpl(Logger logger, Event<CatalogUpdateEvent> catalogUpdateEvent) {
        this.logger = logger;
        this.catalogUpdateEvent = catalogUpdateEvent;
    }

    @Override
    public void publishCatalogUpdateEvent(UUID catalogId) {
        try {
            if (catalogId == null)
                throw new IllegalArgumentException("Catalog ID cannot be null");

            logger.infof("Publishing event to update catalog: %s", catalogId);
            CompletionStage<CatalogUpdateEvent> future = catalogUpdateEvent.fireAsync(new CatalogUpdateEvent(catalogId));

            future.whenComplete((result, throwable) -> {
                if (throwable != null) {
                    logger.errorf("Error processing catalog (%s) event", catalogId, throwable);
                } else {
                    logger.debugf("Catalog (%s) event processed successfully", catalogId);
                }
            });

            logger.infof("Event successfully published for catalog %s", catalogId);
        } catch (Exception e) {
            logger.error("Error publishing event for catalog %s", catalogId, e);
            throw new RuntimeException("Error publishing update event", e);
        }
    }
}
