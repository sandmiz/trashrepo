package br.edu.ifg.luziana.pw.controller;

import br.edu.ifg.luziana.pw.model.LogDTO;
import br.edu.ifg.luziana.pw.model.VeiculoDTO;
import br.edu.ifg.luziana.pw.model.bo.LogBO;
import br.edu.ifg.luziana.pw.model.bo.VeiculoBO;
import br.edu.ifg.luziana.pw.model.dao.UsuarioDAO;
import br.edu.ifg.luziana.pw.model.entity.Usuario;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
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
    public TemplateInstance cadastro() {
        return Templates.cadastro();
    }

    @POST
    @Path("/salvar")
    @Produces(MediaType.APPLICATION_JSON)
    public Response salvar(VeiculoDTO dto, @QueryParam("usuarioId") Integer usuarioId) {
        Response resp = veiculoBO.salvar(dto, usuarioId);
        if (resp.getStatus() == 201) {
            try {
                String nome = null;
                String email = null;
                if (usuarioId != null) {
                    Usuario u = usuarioDAO.findById(usuarioId);
                    if (u != null) {
                        nome = u.getNome() + (u.getSobrenome() != null ? " " + u.getSobrenome() : "");
                        email = u.getEmail();
                    }
                }
                LogDTO log = new LogDTO("CADASTRO_VEICULO",
                        "Cadastrou " + dto.getMarca() + " " + dto.getModelo() + " " + dto.getAno(),
                        nome, email, "web");
                logBO.registrar(log);
            } catch (Exception ignored) {
            }
        }
        return resp;
    }

    @GET
    @Path("/listar")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listar() {
        return veiculoBO.listar();
    }
}
