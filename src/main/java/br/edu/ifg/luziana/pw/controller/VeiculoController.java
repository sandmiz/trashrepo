package br.edu.ifg.luziana.pw.controller;

import br.edu.ifg.luziana.pw.model.LogDTO;
import br.edu.ifg.luziana.pw.model.VeiculoDTO;
import br.edu.ifg.luziana.pw.model.bo.LogBO;
import br.edu.ifg.luziana.pw.model.bo.VeiculoBO;
import br.edu.ifg.luziana.pw.model.dao.UsuarioDAO;
import br.edu.ifg.luziana.pw.model.entity.Usuario;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/cadastro-veiculo")
public class VeiculoController {

    @Inject
    VeiculoBO veiculoBO;

    @Inject
    LogBO logBO;

    @Inject
    UsuarioDAO usuarioDAO;

    @CheckedTemplate
    public static class Templates {
        public static native TemplateInstance cadastro();
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @RolesAllowed({ "admin", "vendedor" })
    public TemplateInstance cadastro() {
        return Templates.cadastro();
    }

    @POST
    @Path("/salvar")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({ "admin", "vendedor" })
    public Response salvar(VeiculoDTO dto) {
        return veiculoBO.salvar(dto);
    }

    @GET
    @Path("/listar")
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    public Response listar() {
        return veiculoBO.listar();
    }
}
