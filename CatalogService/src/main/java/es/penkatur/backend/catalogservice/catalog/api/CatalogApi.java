package es.penkatur.backend.catalogservice.catalog.api;

import es.penkatur.backend.catalogservice.catalog.api.dto.CatalogDTO;
import es.penkatur.backend.sharedkernel.api.error.ErrorResponse;
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
import java.util.concurrent.CompletionStage;

@Path("/v1/catalogs")
@Tag(name = "Catalog Management API")
@Produces(MediaType.APPLICATION_JSON)
public interface CatalogApi {

    @GET
    @Operation(
            summary = "Retrieves a list of catalogs",
            description = "Fetches all available catalogs. Optionally, filters catalogs updated after a given timestamp.")
    @APIResponse(
            responseCode = "200",
            description = "List of catalogs successfully retrieved",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = CatalogDTO.class, type = SchemaType.ARRAY)))
    @APIResponse(
            responseCode = "204",
            description = "No catalogs found that match the criteria")
    @APIResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    CompletionStage<List<CatalogDTO>> listAllCatalogs(@QueryParam("updatedAt") Instant updatedAt);

    @POST
    @Operation(
            summary = "Create a new catalog",
            description = "Creates a new catalog based on the provided information.")
    @APIResponse(
            responseCode = "200",
            description = "Catalog successfully created",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = CatalogDTO.class, type = SchemaType.OBJECT)))
    @APIResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    CompletionStage<CatalogDTO> createCatalog(@Valid CatalogDTO catalog);

    @GET
    @Path("/{id}")
    @Operation(
            summary = "Retrieves a catalog by ID",
            description = "Fetches a specific catalog based on its unique identifier.")
    @APIResponse(
            responseCode = "200",
            description = "Catalog successfully found",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = CatalogDTO.class, type = SchemaType.OBJECT)))
    @APIResponse(
            responseCode = "404",
            description = "Catalog not found")
    @APIResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    CompletionStage<CatalogDTO> getCatalog(@PathParam("id") UUID catalogId);

    @PUT
    @Path("/{id}")
    @Operation(
            summary = "Update a catalog by ID",
            description = "Updates the catalog information based on the provided ID and catalog details.")
    @APIResponse(
            responseCode = "200",
            description = "Catalog successfully updated",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = CatalogDTO.class, type = SchemaType.OBJECT)))
    @APIResponse(
            responseCode = "404",
            description = "Catalog not found")
    @APIResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    CompletionStage<CatalogDTO> updateCatalog(@PathParam("id") UUID catalogId, @Valid CatalogDTO catalog);

    @DELETE
    @Path("/{id}")
    @Operation(
            summary = "Delete a catalog by ID",
            description = "Deletes the catalog identified by the provided ID. Returns `true` if the catalog was successfully deleted, or `false` if the catalog was not found or could not be deleted.")
    @APIResponse(
            responseCode = "200",
            description = "Catalog successfully deleted (true) or not found/cannot be deleted (false)")
    @APIResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    CompletionStage<Boolean> deleteCatalog(@PathParam("id") UUID catalogId);

    @POST
    @Path("/{id}/refresh")
    @Operation(
            summary = "Refresh a catalog by ID",
            description = "Triggers a refresh for the catalog identified by the provided ID. The catalog itself is not updated, but the system refreshes its state or cache.")
    @APIResponse(
            responseCode = "200",
            description = "Catalog refresh successfully triggered")
    @APIResponse(
            responseCode = "404",
            description = "Catalog not found")
    @APIResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    CompletionStage<Response> refreshCatalog(@PathParam("id") UUID catalogId);
}
