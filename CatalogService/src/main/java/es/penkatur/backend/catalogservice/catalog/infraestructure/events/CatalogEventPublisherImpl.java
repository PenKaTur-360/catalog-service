package es.penkatur.backend.catalogservice.catalog.infraestructure.events;

import es.penkatur.backend.catalogservice.catalog.domain.events.CatalogEventPublisher;
import es.penkatur.backend.catalogservice.catalog.domain.events.CatalogUpdateEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;

import java.util.UUID;

@ApplicationScoped
public class CatalogEventPublisherImpl implements CatalogEventPublisher {

    @Inject
    Event<CatalogUpdateEvent> catalogUpdateEvent;

    @Override
    public void publishCatalogUpdateEvent(UUID catalogId) {
        catalogUpdateEvent.fire(new CatalogUpdateEvent(catalogId));
    }
}
