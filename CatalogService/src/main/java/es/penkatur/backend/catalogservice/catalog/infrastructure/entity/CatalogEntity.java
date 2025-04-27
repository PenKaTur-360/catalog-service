package es.penkatur.backend.catalogservice.catalog.infrastructure.entity;

import es.penkatur.backend.sharedkernel.infraestructure.entity.UUIDAuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "t_catalogs")
public class CatalogEntity extends UUIDAuditableEntity {
    @Column(name = "user_id", nullable = false)
    private UUID userId;
    private String key;
    private String name;
    private String author;
    private String version;
    @Column(nullable = false)
    private String url;
    // TODO: add ingredients
    // TODO: add meals
    @Column(name = "external_updated_at")
    private Instant externalUpdatedAt;
}
