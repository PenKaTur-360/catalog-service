package es.penkatur.backend.catalogservice.catalog.infrastructure.persistence.mapper;

import es.penkatur.backend.catalogservice.catalog.domain.Catalog;
import es.penkatur.backend.catalogservice.catalog.infrastructure.entity.CatalogEntity;

public class CatalogMapper {

    public static Catalog toDomain(CatalogEntity entity) {
        return Catalog.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .key(entity.getKey())
                .name(entity.getName())
                .author(entity.getAuthor())
                .version(entity.getVersion())
                .url(entity.getUrl())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public static CatalogEntity toEntity(Catalog catalog) {
        var entity = new CatalogEntity();
        entity.setId(catalog.getId());
        entity.setUserId(catalog.getUserId());
        entity.setKey(catalog.getKey());
        entity.setName(catalog.getName());
        entity.setAuthor(catalog.getAuthor());
        entity.setVersion(catalog.getVersion());
        entity.setUrl(catalog.getUrl());
        entity.setCreatedAt(catalog.getCreatedAt());
        entity.setUpdatedAt(catalog.getUpdatedAt());
        return entity;
    }
}
