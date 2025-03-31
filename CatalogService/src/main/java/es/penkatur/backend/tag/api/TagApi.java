package es.penkatur.backend.tag.api;

import es.penkatur.backend.tag.api.dto.TagDTO;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
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
import java.util.UUID;

@Path("/v1/tags")
@Tag(name = "Tag Management API")
@Produces(MediaType.APPLICATION_JSON)
public interface TagApi {

    @GET
    @Operation(summary = "Retrieves a list of tags",
            description = "Fetches all available tags. Optionally, filters tags updated after a given timestamp.")
    @APIResponse(responseCode = "200",
            description = "List of tags successfully retrieved",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TagDTO.class, type = SchemaType.ARRAY)))
    @APIResponse(responseCode = "204", description = "No content available")
    @APIResponse(responseCode = "500", description = "Internal server error")
    Multi<TagDTO> listAllTags(@QueryParam("updatedAt") Instant updatedAt);

    @GET
    @Path("/{id}")
    @Operation(summary = "Retrieves a tag by ID",
            description = "Fetches a specific tag based on its unique identifier.")
    @APIResponse(responseCode = "200", description = "Tag successfully found")
    @APIResponse(responseCode = "404", description = "Tag not found")
    Uni<TagDTO> getTag(@PathParam("id") UUID tagId);

    @PUT
    @Path("/{id}")
    @Operation(summary = "Updates an existing tag",
            description = "Modifies the details of a tag identified by its unique ID.")
    @APIResponse(responseCode = "200", description = "Tag successfully updated")
    @APIResponse(responseCode = "201", description = "Tag successfully created")
    @APIResponse(responseCode = "204", description = "Tag successfully updated")
    @APIResponse(responseCode = "404", description = "Tag not found")
    @APIResponse(responseCode = "400", description = "Invalid input data")
    Uni<Response> updateTag(@PathParam("id") UUID tagId, TagDTO tag);
}
