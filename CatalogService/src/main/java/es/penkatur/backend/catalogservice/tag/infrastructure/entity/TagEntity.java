package es.penkatur.backend.catalogservice.tag.infrastructure.entity;

import es.penkatur.backend.sharedkernel.infraestructure.entity.UUIDAuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "t_tags")
public class TagEntity extends UUIDAuditableEntity {
    @Column(nullable = false)
    private String name;
    private String color;
}
