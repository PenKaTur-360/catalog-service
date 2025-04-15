package es.penkatur.backend.catalog.infrastructure.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "t_catalogs")
public class CatalogEntity {
    @Id
    private UUID id;
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
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
    @Column(name = "external_updated_at")
    private Instant externalUpdatedAt;
}
