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
        Response resp = usuarioBO.autenticar(dto);
        if (resp.getStatus() == 200) {
            Usuario u = (Usuario) resp.getEntity();
            try {
                LogDTO log = new LogDTO("LOGIN", "Login realizado com sucesso",
                        u.getNome() + " " + (u.getSobrenome() != null ? u.getSobrenome() : ""),
                        u.getEmail(), "web");
                logBO.registrar(log);
            } catch (Exception ignored) {
            }
            Map<String, Object> body = new HashMap<>();
            body.put("id", u.getId());
            body.put("nome", u.getNome());
            body.put("sobrenome", u.getSobrenome());
            body.put("email", u.getEmail());
            body.put("telefone", u.getTelefone());
            body.put("cpf", u.getCpf());
            body.put("cidade", u.getCidade());
            body.put("estado", u.getEstado());
            body.put("tipo", u.getTipo());
            body.put("bio", u.getBio());
            return Response.ok(body).build();
        }
        Object entity = resp.getEntity();
        String msg = entity != null ? entity.toString() : "E-mail ou senha inválidos.";
        return Response.status(resp.getStatus())
                .entity(Map.of("erro", msg))
                .build();
    }

    @POST
    @Path("/cadastrar")
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    public Response cadastrar(CadastroDTO dto) {
        Response resp = usuarioBO.cadastrar(dto);
        if (resp.getStatus() == 201) {
            Usuario u = (Usuario) resp.getEntity();
            try {
                LogDTO log = new LogDTO("CADASTRO", "Novo usuário cadastrado",
                        u.getNome() + " " + (u.getSobrenome() != null ? u.getSobrenome() : ""),
                        u.getEmail(), "web");
                logBO.registrar(log);
            } catch (Exception ignored) {
            }
            Map<String, Object> body = new HashMap<>();
            body.put("id", u.getId());
            body.put("nome", u.getNome());
            body.put("sobrenome", u.getSobrenome());
            body.put("email", u.getEmail());
            body.put("tipo", u.getTipo());
            return Response.status(Response.Status.CREATED).entity(body).build();
        }
        Object entity = resp.getEntity();
        String msg = entity != null ? entity.toString() : "Erro no cadastro.";
        return Response.status(resp.getStatus())
                .entity(Map.of("erro", msg))
                .build();
    }
}
