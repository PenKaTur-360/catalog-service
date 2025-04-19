package es.penkatur.backend.tag.infrastructure.entity;

import es.penkatur.backend.sharedkernel.infrastructure.entity.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "t_tags")
public class TagEntity extends AuditableEntity {
    @Id
    private UUID id;
    @Column(nullable = false)
    private String name;
    private String color;
}
