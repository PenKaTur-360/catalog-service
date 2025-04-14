package es.penkatur.backend.catalog.api;

import es.penkatur.backend.catalog.api.dto.CatalogDTO;
import io.smallrye.mutiny.Uni;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Path("/v1/catalogs")
@Tag(name = "Catalog Management API")
@Produces(MediaType.APPLICATION_JSON)
public interface CatalogApi {

    @GET
    @Operation(summary = "Retrieves a list of tags",
            description = "Fetches all available catalogs. Optionally, filters tags updated after a given timestamp.")
    @APIResponse(responseCode = "200",
            description = "List of tags successfully retrieved",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CatalogDTO.class, type = SchemaType.ARRAY)))
    @APIResponse(responseCode = "204", description = "No content available")
    @APIResponse(responseCode = "500", description = "Internal server error")
    Uni<List<CatalogDTO>> listAllCatalogs(@QueryParam("updatedAt") Instant updatedAt);

    @POST
    Uni<CatalogDTO> createCatalog(@Valid CatalogDTO catalog);

    @GET
    @Path("/{id}")
    @Operation(summary = "Retrieves a catalog by ID",
            description = "Fetches a specific catalog based on its unique identifier.")
    @APIResponse(responseCode = "200", description = "Catalog successfully found")
    @APIResponse(responseCode = "404", description = "Catalog not found")
    Uni<CatalogDTO> getCatalog(@PathParam("id") UUID catalogId);

    @PUT
    @Path("/{id}")
    Uni<CatalogDTO> updateCatalog(@PathParam("id") UUID catalogId, @Valid CatalogDTO catalog);

    @DELETE
    @Path("/{id}")
    Uni<Boolean> deleteCatalog(@PathParam("id") UUID catalogId);

    @POST
    @Path("/{id}/refresh")
    Uni<Response> refreshCatalog(@PathParam("id") UUID catalogId);
}
