package es.penkatur.backend.catalogservice.tag.infrastructure.persistence.mapper;

import es.penkatur.backend.catalogservice.tag.domain.Tag;
import es.penkatur.backend.catalogservice.tag.infrastructure.entity.TagEntity;

public class TagMapper {

    public static Tag toDomain(TagEntity entity) {
        return Tag.builder()
                .id(entity.getId())
                .name(entity.getName())
                .color(entity.getColor())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public static TagEntity toEntity(Tag tag) {
        var entity = new TagEntity();
        entity.setId(tag.getId());
        entity.setName(tag.getName());
        entity.setColor(tag.getColor());
        return entity;
    }
}
