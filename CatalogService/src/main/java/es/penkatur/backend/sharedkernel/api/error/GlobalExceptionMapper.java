package es.penkatur.backend.sharedkernel.api.error;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

@Provider
@ApplicationScoped
public class GlobalExceptionMapper implements ExceptionMapper<Exception> {

    private final Logger logger;

    @Inject
    public GlobalExceptionMapper(Logger logger) {
        this.logger = logger;
    }

    @Override
    public Response toResponse(Exception exception) {
        if (exception instanceof WebApplicationException) {
            return handleWebApplicationException((WebApplicationException) exception);
        } else if (exception instanceof IllegalArgumentException) {
            return handleBadRequestException(exception);
        } else {
            return handleGenericException(exception);
        }
    }

    private Response handleWebApplicationException(WebApplicationException exception) {
        int status = exception.getResponse().getStatus();
        String code = determineErrorCode(status);
        String message = exception.getMessage();

        if (status >= 500) {
            logger.errorf(exception, "Server error: %s", message);
        } else {
            logger.warnf("Client error: %s", message);
        }

        ErrorResponse errorResponse = ErrorResponse.from(status, code, message);

        return Response
                .status(status)
                .entity(errorResponse)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    private Response handleBadRequestException(Exception exception) {
        int status = Response.Status.BAD_REQUEST.getStatusCode();
        String message = exception.getMessage();
        String code = "BAD_REQUEST";

        logger.warnf("Validation error: %s", message);

        ErrorResponse errorResponse = ErrorResponse.from(status, code, message);

        return Response
                .status(status)
                .entity(errorResponse)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    private Response handleGenericException(Exception exception) {
        String message = "An internal error has occurred. Please contact support.";
        int status = Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();
        String code = "INTERNAL_ERROR";

        ErrorResponse errorResponse = ErrorResponse.from(status, code, message);
        logger.errorf(exception, "Unhandled internal error");

        return Response
                .status(status)
                .entity(errorResponse)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    private String determineErrorCode(int status) {
        return switch (status) {
            case 400 -> "BAD_REQUEST";
            case 401 -> "UNAUTHORIZED";
            case 403 -> "FORBIDDEN";
            case 404 -> "NOT_FOUND";
            case 409 -> "CONFLICT";
            case 422 -> "UNPROCESSABLE_ENTITY";
            case 500 -> "INTERNAL_ERROR";
            case 503 -> "SERVICE_UNAVAILABLE";
            default -> "ERROR";
        };
    }
}
