package es.penkatur.backend.common.domain;

import java.time.Instant;

public interface BaseModel<ID> {
    ID getId();

    Instant getCreatedAt();

    Instant getUpdatedAt();

    void changeUpdatedAt(Instant updatedAt);
}
