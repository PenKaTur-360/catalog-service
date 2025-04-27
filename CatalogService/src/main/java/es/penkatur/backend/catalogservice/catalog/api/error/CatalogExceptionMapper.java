package es.penkatur.backend.catalogservice.catalog.api.error;

import es.penkatur.backend.catalogservice.catalog.domain.exceptions.CatalogException;
import es.penkatur.backend.catalogservice.catalog.domain.exceptions.InvalidCatalogException;
import es.penkatur.backend.catalogservice.catalog.infraestructure.exceptions.CatalogNotFoundException;
import es.penkatur.backend.sharedkernel.api.error.ErrorResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

@Provider
@ApplicationScoped
public class CatalogExceptionMapper implements ExceptionMapper<CatalogException> {

    private final Logger logger;

    @Inject
    public CatalogExceptionMapper(Logger logger) {
        this.logger = logger;
    }

    @Override
    public Response toResponse(CatalogException exception) {
        int status;

        if (exception instanceof CatalogNotFoundException) {
            status = Response.Status.NOT_FOUND.getStatusCode();
            logger.info(exception.getMessage());
        } else if (exception instanceof InvalidCatalogException) {
            status = Response.Status.BAD_REQUEST.getStatusCode();
            logger.warn(exception.getMessage());
        } else {
            status = Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();
            logger.error(exception.getMessage(), exception);
        }

        ErrorResponse errorResponse = ErrorResponse.from(
                status,
                exception.getErrorCode(),
                exception.getMessage()
        );

        return Response
                .status(status)
                .entity(errorResponse)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
