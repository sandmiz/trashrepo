package br.edu.ifg.luziana.pw.controller;

import br.edu.ifg.luziana.pw.model.PerfilDTO;
import br.edu.ifg.luziana.pw.model.bo.PerfilBO;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Map;

@Path("/perfil")
public class PerfilController {

    @Inject
    PerfilBO perfilBO;

    @CheckedTemplate
    public static class Templates {
        public static native TemplateInstance perfil();
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Authenticated
    public TemplateInstance perfil() {
        return Templates.perfil();
    }

    @POST
    @Path("/salvar")
    @Produces(MediaType.APPLICATION_JSON)
    @Authenticated
    public Response salvar(PerfilDTO dto, @QueryParam("usuarioId") Integer usuarioId) {
        if (usuarioId == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("erro", "Informe o id do usuário (usuarioId)."))
                    .build();
        }
        return perfilBO.salvar(usuarioId, dto);
    }

    @GET
    @Path("/buscar")
    @PermitAll
    @Produces(MediaType.APPLICATION_JSON)
    public Response buscar(@QueryParam("id") Integer id) {
        if (id == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("erro", "Informe o id."))
                    .build();
        }
        return perfilBO.buscar(id);
    }

    @GET
    @Path("/info")
    @Produces(MediaType.APPLICATION_JSON)
    @Authenticated
    public Response info() {
        return perfilBO.info();
    }
}
