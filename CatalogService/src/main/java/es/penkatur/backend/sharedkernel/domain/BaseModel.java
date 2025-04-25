package es.penkatur.backend.sharedkernel.domain;

import java.time.Instant;

public interface BaseModel<ID> {
    ID getId();

    Instant getCreatedAt();

    Instant getUpdatedAt();
}
