package br.com.scriptum.resource;

import br.com.scriptum.model.Grupo;
import br.com.scriptum.service.GrupoService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/grupos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GrupoResource {

    @Inject
    GrupoService grupoService;

    // Classe simples para receber o JSON
    public static class GrupoRequest {
        public String nome;
    }

    @POST
    public Response criar(GrupoRequest request) {
        try {
            Grupo grupo = grupoService.criarGrupo(request.nome);
            return Response.status(Response.Status.CREATED).entity(grupo).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @GET
    public Response listar() {
        return Response.ok(grupoService.listarGrupos()).build();
    }
}