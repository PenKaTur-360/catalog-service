package es.penkatur.backend.sharedkernel.api.error;

import jakarta.json.bind.annotation.JsonbProperty;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "Standard response for API errors")
public record ErrorResponse(
        @Schema(description = "HTTP status code")
        @JsonbProperty("status")
        int status,

        @Schema(description = "Application-specific error code")
        @JsonbProperty("code")
        String code,

        @Schema(description = "Descriptive error message")
        @JsonbProperty("message")
        String message,

        @Schema(description = "Timestamp when the error occurred")
        @JsonbProperty("timestamp")
        Instant timestamp,

        @Schema(description = "Unique identifier for error tracking")
        @JsonbProperty("traceId")
        String traceId) {
        public static ErrorResponse from(int status, String code, String message) {
                return new ErrorResponse(
                        status,
                        code,
                        message,
                        Instant.now(),
                        generateTraceId()
                );
        }

        private static String generateTraceId() {
                return String.format("PKT-ERR-%d-%d", System.currentTimeMillis(), (int)(Math.random() * 10000));
        }
}
