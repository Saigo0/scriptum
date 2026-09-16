package br.com.scriptum.resource;

import br.com.scriptum.dto.NoteRequest;
import br.com.scriptum.model.Note;
import br.com.scriptum.service.NoteService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/notes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class NoteResource {

    @Inject
    NoteService noteService;

    @POST
    public Response createNote(@Valid NoteRequest request) {
        Note note = noteService.createNote(request);
        return Response.status(Response.Status.CREATED).entity(note).build();
    }

    @GET
    public Response getAllNotes() {
        return Response.ok(noteService.getAllNotes()).build();
    }
    
    @GET
    @Path("/{id}")
    public Response getNote(@PathParam("id") Long id) {
        Note note = noteService.getNote(id);
        if (note == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(note).build();
    }
}
