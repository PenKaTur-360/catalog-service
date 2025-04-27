package es.penkatur.backend.sharedkernel.infraestructure.entity;

import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@MappedSuperclass
public abstract class UUIDAuditableEntity extends AuditableEntity {
    @Id
    private UUID id;
}
