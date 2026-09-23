package br.com.scriptum.resource;

import br.com.scriptum.model.Documento;
import br.com.scriptum.service.DocumentoService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/documentos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DocumentoResource {

    @Inject
    DocumentoService documentoService;

    public static class DocumentoManualRequest {
        public String titulo;
        public List<String> paragrafos;
    }

    public static class DocumentoEscaneadoRequest {
        public String titulo;
        public String language;
        public String imageBase64;
    }

    @GET
    @Path("/{id}")
    public Response buscarDocumento(@PathParam("id") Long id) {
        Documento doc = documentoService.buscarPorId(id);
        
        if (doc == null) {
            return Response.status(Response.Status.NOT_FOUND)
                           .entity("Documento não encontrado para o ID: " + id)
                           .build();
        }
        
        return Response.ok(doc).build();
    }

    @POST
    @Path("/manual")
    public Response criarManual(DocumentoManualRequest request) {
        try {
            Documento doc = documentoService.criarDocumentoManual(request.titulo, request.paragrafos);
            return Response.status(Response.Status.CREATED).entity(doc).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/escaneado")
    public Response criarEscaneado(DocumentoEscaneadoRequest request) {
        Documento doc = documentoService.criarDocumentoEscaneado(request.titulo, request.language, request.imageBase64);
        return Response.status(Response.Status.ACCEPTED).entity(doc).build(); 
    }

    @PUT
    @Path("/{id}")
    public Response editarDocumento(@PathParam("id") Long id, List<String> novosParagrafos) {
        try {
            Documento doc = documentoService.editarDocumento(id, novosParagrafos);
            if (doc == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            return Response.ok(doc).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }
}