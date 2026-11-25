package br.edu.ifg.luziana.pw.controller;

import br.edu.ifg.luziana.pw.model.CadastroDTO;
import br.edu.ifg.luziana.pw.model.LogDTO;
import br.edu.ifg.luziana.pw.model.LoginDTO;
import br.edu.ifg.luziana.pw.model.bo.LogBO;
import br.edu.ifg.luziana.pw.model.bo.UsuarioBO;
import br.edu.ifg.luziana.pw.model.entity.Usuario;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.HashMap;
import java.util.Map;

@Path("/login")
public class LoginController {

    @Inject
    UsuarioBO usuarioBO;

    @Inject
    LogBO logBO;

    @CheckedTemplate
    public static class Templates {
        public static native TemplateInstance login();
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @PermitAll
    public TemplateInstance login() {
        return Templates.login();
    }

    @POST
    @Path("/entrar")
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    public Response entrar(LoginDTO dto) {
        return usuarioBO.autenticar(dto);
    }

    @POST
    @Path("/cadastrar")
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    public Response cadastrar(CadastroDTO dto) {
        return usuarioBO.cadastrar(dto);
    }
}
