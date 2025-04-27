package es.penkatur.backend.sharedkernel.infraestructure.persistence;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.control.ActivateRequestContext;
import jakarta.transaction.Transactional;

import java.util.function.Supplier;

@ApplicationScoped
public class ContextualOperations {

    @ActivateRequestContext
    public <T> T executeInSession(Supplier<T> operation) {
        return operation.get();
    }

    @Transactional
    public <T> T executeInTransaction(Supplier<T> operation) {
        return operation.get();
    }
}
