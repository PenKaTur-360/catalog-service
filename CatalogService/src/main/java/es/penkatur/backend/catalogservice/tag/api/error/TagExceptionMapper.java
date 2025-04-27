package es.penkatur.backend.catalogservice.tag.api.error;

import es.penkatur.backend.catalogservice.tag.domain.exceptions.InvalidTagException;
import es.penkatur.backend.catalogservice.tag.domain.exceptions.TagException;
import es.penkatur.backend.catalogservice.tag.infrastructure.exceptions.TagNotFoundException;
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
public class TagExceptionMapper implements ExceptionMapper<TagException> {

    private final Logger logger;

    @Inject
    public TagExceptionMapper(Logger logger) {
        this.logger = logger;
    }

    @Override
    public Response toResponse(TagException exception) {
        int status;

        if (exception instanceof TagNotFoundException) {
            status = Response.Status.NOT_FOUND.getStatusCode();
            logger.info(exception.getMessage());
        } else if (exception instanceof InvalidTagException) {
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
