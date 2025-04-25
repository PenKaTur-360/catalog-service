package es.penkatur.backend.sharedkernel.infraestructure.entity;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class AuditingEntityListener {
    @PrePersist
    public void setCreationDate(Object entity) {
        if (entity instanceof AuditableEntity) {
            var now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
            ((AuditableEntity) entity).setCreatedAt(now);
            ((AuditableEntity) entity).setUpdatedAt(now);
        }
    }

    @PreUpdate
    public void setUpdateDate(Object entity) {
        if (entity instanceof AuditableEntity)
            ((AuditableEntity) entity).setUpdatedAt(Instant.now().truncatedTo(ChronoUnit.SECONDS));
    }
}
