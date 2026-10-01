package br.com.scriptum.resource;

import io.smallrye.jwt.build.Jwt;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {

    private static final String SECRET = "scriptum-chave-secreta-super-segura-32-bytes";

    public static class LoginRequest {
        public String usuario;
        public String senha;
    }

    @POST
    @Path("/login")
    public Response login(LoginRequest request) {
        if ("admin".equals(request.usuario) && "123456".equals(request.senha)) {
            
            SecretKey key = new SecretKeySpec(SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA256");

            String token = Jwt.issuer("scriptum-issuer")
                              .upn(request.usuario)
                              .claim("role", "admin")
                              .jws()
                              .sign(key); 

            return Response.ok("{\"token\":\"" + token + "\"}").build();
        }
        return Response.status(Response.Status.UNAUTHORIZED).entity("Credenciais inválidas").build();
    }
}